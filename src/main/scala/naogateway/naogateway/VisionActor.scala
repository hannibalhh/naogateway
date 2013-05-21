package naogateway
import akka.actor.Actor
import akka.actor.ActorRef
import naogateway.traits.ZMQ
import naogateway.traits.Log
import naogateway.value.NaoMessages._
import naogateway.value.HAWCamserverMessages.CamRequest

/**
 * VisionActor communicates with HAWCamActor on nao with ZMQ
 */
class VisionActor(nao:Nao) extends Actor with Log with ZMQ{
  trace(nao)
  import naogateway.value.NaoMessages.Nao
  import naogateway.value.NaoVisionMessages._
  import context._

  /**
   * Protobuf trigger message (empty CamRequest) as bytearray
   * need to send to get a picture
   */
  val rawTrigger = CamRequest.newBuilder().build().toByteArray
  
  /**
   * first time, nao must be configured with colorspace, fps and resolution
   * then it will be send a rawTrigger Message to Nao
   * sender of vision call get the first picture as answer
   * if an error occoured, sender of vision call get a answer with error message
   */
  def configure(c: VisionCall) = {
      trace("request: " + c)
      val socket = zmqsocket(nao.host,nao.port)
      socket.send(request(c).toByteArray, 0)

      import scala.concurrent._
      val caller = sender
      val answering = future {
        (picture(socket.recv(0)),caller) // message with possible error, no picture
      }
      answering onSuccess {
        case (answer,caller) => {      
            trace("answer " + answer + " comes in for " + caller)
            if (!answer.hasError()){
            	socket.send(rawTrigger, 0)
            	caller ! picture(socket.recv(0)) // first picture
            	become(communicating)
            }
            else
              caller ! answer
        	socket.close     	
        }
      }
  }
  
  /**
   * in state receive
   * you can configure with a  vision call
   */
  import org.zeromq.ZMQ.Socket
  def receive = {
    case c: VisionCall => configure(c)
    case x => wrongMessage(x, "receive")
  }

  /**
   * in state communicating
   * you can configure with a new vision call
   * or Trigger to get a new picture
   */
  def communicating:Receive = {
    case c: VisionCall => configure(c)
  	case Trigger => {
  	  val socket = zmqsocket(nao.host,nao.port)
      socket.send(rawTrigger, 0)

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
  	case x => wrongMessage(x, "communicating")
  }

  trace("is started ")
}