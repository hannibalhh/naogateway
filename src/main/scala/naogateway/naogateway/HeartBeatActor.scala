package naogateway

import akka.actor.Actor
import akka.actor.Props

 class HeartBeatActor extends Actor{

  import naogateway.value._
  import naogateway.value.NaoMessages._
  import naogateway.value.NaoMessages.Conversions
  import context._
  
  def receive = {
    case nao:Nao => step(nao,online(nao),Online,maybeOffline(nao),MaybeOffline)
  }
  
  def maybeOffline(nao:Nao,count:Int = 0):Receive = {
    case Trigger if count <= 3 => {
      step(nao,online(nao),Online,maybeOffline(nao,count+1),MaybeOffline)    
    }
    case _ => {
     step(nao,online(nao),Online,receive,Offline)   
    }
  }
  
  def online(nao:Nao):Receive = {
    case Trigger => step(nao,online(nao),Online,maybeOffline(nao),MaybeOffline)    
  }
    
    /**
   * zMQ object contains a ZMQ Context with sockets
   * only request type of sockets (request reply pattern 
   * have to be strongly observed) is here allowed
   */
  object zMQ {
    import org.zeromq.ZContext
    def context = new ZContext
    def socket(cont: ZContext = context, url: String) = {
      import org.zeromq.ZMQ._
      val socket = cont.createSocket(REQ)
      socket.connect(url)
      socket
    }
  }

  /**
   * In communicating state the actor takes every call and
   * convert to nao proto
   * send it to nao
   * wait non blocking on anwser
   */
  import org.zeromq.ZMQ.Socket
  def step(nao: Nao,suc:PartialFunction[Any,Unit],succMessage:Any,fail:PartialFunction[Any,Unit],failMessage:Any) = {
	  val c = Call(Module("test"), Method("test"))
      trace("request: " + c)
      val socket = zMQ.socket(url = "tcp://" + nao.host + ":" + nao.port)
      socket.send(request(c).toByteArray, 0)

      import scala.concurrent._
      val caller = sender
      val answering = future {
        socket.recv(0)
      }
      answering onSuccess {
        case _ => {      
        	socket.close
        	sender ! succMessage
        	become(suc)
        }
      }
      answering onFailure {
        case _ => {      
        	socket.close
        	sender ! failMessage
        	become(fail)
        }
      }
  }
  
  def trace(a: Any) = if (context.system.settings.config.getBoolean("log.heartbeatactor.info")) log.info(a.toString)
  def error(a: Any) = if (context.system.settings.config.getBoolean("log.heartbeatactor.error")) log.warning(a.toString)
  def wrongMessage(a: Any, state: String) = if (context.system.settings.config.getBoolean("log.heartbeatactor.wrongMessage")) log.warning("wrong message: " + a + " in " + state)
  import akka.event.Logging
  val log = Logging(context.system, this)
  trace("is started ")
}