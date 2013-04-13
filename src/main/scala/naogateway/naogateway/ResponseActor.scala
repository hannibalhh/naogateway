package naogateway

import akka.actor.Actor
import akka.actor.ActorRef
/**
 * NaoResponseActor send calls to nao and send the answer to caller
 */
class ResponseActor extends Actor {

  import naogateway.value._
  import naogateway.value.NaoMessages._
  import naogateway.value.NaoMessages.Conversions
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
    case c: Call => {
      trace("request: " + c)
      val socket = zMQ.socket(url = "tcp://" + nao.host + ":" + nao.port)
      socket.send(request(c).toByteArray, 0)

      import scala.concurrent._
      val caller = sender
      val answering = future {
        (answer(socket.recv(0), c),caller)
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

  def trace(a: Any) = if (LogConf.ResponseActor.info) log.info(a.toString)
  def error(a: Any) = if (LogConf.ResponseActor.error) log.warning(a.toString)
  def wrongMessage(a: Any, state: String) = if (LogConf.ResponseActor.wrongMessage) log.warning("wrong message: " + a + " in " + state)
  import akka.event.Logging
  val log = Logging(context.system, this)
  trace("is started")
}