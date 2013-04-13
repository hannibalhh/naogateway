package naogateway
import akka.actor.Actor
import akka.actor.ActorRef

class VisionActor extends Actor{

  import naogateway.value._
  import naogateway.value.NaoVisionMessages._
  import context._

  /**
   * In the start state actor wait for connection data (Nao object)
   * with name, ip and port from NaoActor
   */
  def receive = {
    case nao: Nao => {
      trace(nao + " comes in")
      become(communicating(nao))
    }
    case x => wrongMessage(x, "receive")
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
  def communicating(nao: Nao): Receive = {
    case c: VisionCall => {
      trace("request: " + c)
      val socket = zMQ.socket(url = "tcp://" + nao.host + ":" + nao.port)
      socket.send(request(c).toByteArray, 0)

      import scala.concurrent._
      val caller = sender
      val answering = future {
        (picture(socket.recv(0)),caller)
      }
      answering onSuccess {
        case (answer,caller) => {      
            trace("answer " + answer + " comes in for " + caller)
        	caller ! answer
        	socket.close
        }
      }
    }
    case c: RawVisionCall => {
      trace("request: " + c)
      val socket = zMQ.socket(url = "tcp://" + nao.host + ":" + nao.port)
      socket.send(request(c).toByteArray, 0)

      import scala.concurrent._
      val caller = sender
      val answering = future {
        (socket.recv(0),caller)
      }
      answering onSuccess {
        case (answer,caller) => {      
            trace("answer " + answer + " comes in for " + caller)
        	caller ! answer
        	socket.close
        }
      }
    }
    case x => wrongMessage(x, "communicating")
  }

  def trace(a: Any) = if (context.system.settings.config.getBoolean("log.visionactor.info")) log.info(a.toString)
  def error(a: Any) = if (context.system.settings.config.getBoolean("log.visionactor.error")) log.warning(a.toString)
  def wrongMessage(a: Any, state: String) = if (context.system.settings.config.getBoolean("log.visionactor.wrongMessage")) log.warning("wrong message: " + a + " in " + state)
  import akka.event.Logging
  val log = Logging(context.system, this)
  trace("is started ")
}