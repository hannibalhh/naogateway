package naogateway

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Stash
import naogateway.traits.Delay
import naogateway.traits.ZMQ
import naogateway.traits.Log
import naogateway.value.NaoMessages.Nao

/**
 * NaoResponseActor send calls to nao 
 * without care of answering
 */
class NoResponseActor(nao:Nao) extends Actor with Delay with ZMQ with Log{

  import naogateway.value.NaoMessages._
  import naogateway.value.NaoMessages.Conversions
  import context._

  /**
   * In communicating state the actor takes every call and
   * convert to nao proto
   * send it to nao
   * won't send answer to caller
   * but actor have to wait on answer because of send and reply pattern
   * too early calls will be stashed
   */
  import org.zeromq.ZMQ.Socket
  def receive = {
    case c: Call => {
      trace("request: " + c)
      val socket = zmqsocket(nao.host,nao.port)
      import akka.pattern.after
      import context.dispatcher
      import scala.concurrent.duration._ 
      val caller = sender
      val answering = after(delay(c.module.name + "." + c.method.name) millis,
        using = context.system.scheduler) {
          import scala.concurrent.Future
          Future {
            socket.send(request(c).toByteArray, 0)
            socket.recv(0)
          }
        }
      answering onSuccess {
        case x => {
          socket.close
          become(receive)
        }
      }
    }
    case x => wrongMessage(x, "communicating")
  }
  

  
  /**
   * If we could use a req compatible socket type like
   * dealer (only request, but how many you want, no reply), 
   * we could use following code but
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

  trace("is started ")
}