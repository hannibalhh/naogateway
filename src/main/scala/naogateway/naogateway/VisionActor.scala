package naogateway
import akka.actor.Actor
import akka.actor.ActorRef

/**
 * VisionActor communicates with HAWCamActor on nao with ZMQ
 */
class VisionActor extends Actor with Log with ZMQ{

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
   * In communicating state the actor takes every call and
   * convert to nao proto
   * send it to nao
   * wait non blocking on anwser
   */
  import org.zeromq.ZMQ.Socket
  def communicating(nao: Nao): Receive = {
    case c: VisionCall => {
      trace("request: " + c)
      val socket = zMQ.socket(nao.host,nao.port)
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
      val socket = zMQ.socket(nao.host,nao.port)
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

  trace("is started ")
}