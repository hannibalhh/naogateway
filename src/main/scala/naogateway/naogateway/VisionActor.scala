package naogateway
import akka.actor.Actor
import akka.actor.ActorRef
import naogateway.traits.ZMQ
import naogateway.traits.Log
import naogateway.value.NaoMessages.Nao

/**
 * VisionActor communicates with HAWCamActor on nao with ZMQ
 */
class VisionActor(nao:Nao) extends Actor with Log with ZMQ{

  import naogateway.value.NaoMessages.Nao
  import naogateway.value.NaoVisionMessages._
  import context._

  /**
   * In start state the actor takes every call and
   * convert to nao proto
   * send it to nao
   * wait non blocking on anwser
   */
  import org.zeromq.ZMQ.Socket
  def receive = {
    case c: VisionCall => {
      trace("request: " + c)
      val socket = zmqsocket(nao.host,nao.port)
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
      val socket = zmqsocket(nao.host,nao.port)
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