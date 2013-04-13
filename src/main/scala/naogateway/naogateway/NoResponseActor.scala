package naogateway

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Stash

/**
 * NaoResponseActor send calls to nao 
 * without care of answering
 */
class NoResponseActor extends Actor with Stash{

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
   * won't send answer to caller
   * but actor have to wait on answer because of send and reply pattern
   * too early calls will be stashed
   */
  import org.zeromq.ZMQ.Socket
  def communicating(nao: Nao): Receive = {
    case c: Call => {
      trace("request: " + c)
      val socket = zMQ.socket(url = "tcp://" + nao.host + ":" + nao.port)
      socket.send(request(c).toByteArray, 0)

      import scala.concurrent._
      val answering = future {
        answer(socket.recv(0), c)
      }
      answering onSuccess {
        case x => {
          unstashAll
          become(communicating(nao))
        }
      }
    }
    case x => wrongMessage(x, "communicating")
  }
  
  def waiting(nao: Nao): Receive = {
    case c: Call => stash
    case x => wrongMessage(x, "waiting")
  }
  
  /**
   * If we could use a req compatible socket type like
   * dealer (only request, no reply), we could use following
   * zmq has a bug if someone use other types instead of req
   * to communicate with rep
   * https://zeromq.jira.com/browse/LIBZMQ-211
   */
  
//  /**
//   * In the start state actor wait for connection data (Nao object)
//   * with name, ip and port from NaoActor
//   */
//  def receive = {
//    case nao: Nao => {
//      trace(nao + " comes in")
//      val socket = zMQ.socket(url = "tcp://" + nao.host + ":" + nao.port)
//      become(communicating(socket))
//    }
//    case x => wrongMessage(x, "receive")
//  }
//
//  object zMQ {
//    import org.zeromq.ZContext
//    def context = new ZContext
//    def socket(cont: ZContext = context, url: String) = {
//      import org.zeromq.ZMQ._
//      val socket = cont.createSocket(DEALER)
//      socket.connect(url)
//      socket
//    }
//  }
//
//  /**
//   * In communicating state the actor takes every call and 
//   * convert to nao proto
//   * send it to nao
//   * won't wait on answer
//   */
//  import org.zeromq.ZMQ.Socket
//  def communicating(socket: Socket): Receive = {
//    case c: Call => {
//      trace("request: " + c)
//      socket.send(request(c).toByteArray, 0)
//    }
//    case x => wrongMessage(x, "communicating")
//  }

  def trace(a: Any) = if (context.system.settings.config.getBoolean("log.noresponseactor.info")) log.info(a.toString)
  def error(a: Any) = if (context.system.settings.config.getBoolean("log.noresponseactor.error")) log.warning(a.toString)
  def wrongMessage(a: Any, state: String) = if (context.system.settings.config.getBoolean("log.noresponseactor.wrongMessage")) log.warning("wrong message: " + a + " in " + state)
  import akka.event.Logging
  val log = Logging(context.system, this)
  trace("is started ")
}