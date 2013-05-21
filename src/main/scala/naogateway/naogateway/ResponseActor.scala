package naogateway

import akka.actor.Actor
import akka.actor.ActorRef
import naogateway.traits.Delay
import naogateway.traits.ZMQ
import naogateway.traits.Log
import naogateway.value.NaoMessages.Nao
/**
 * NaoResponseActor send calls to nao and send the answer to caller
 */
class ResponseActor(nao:Nao) extends Actor with Delay with ZMQ with Log{
  val logname = "responseactor"
 
 import naogateway.value.NaoMessages._
  import naogateway.value.NaoMessages.Conversions
  import context._

  /**
   * In start state the actor takes every call and
   * convert to nao proto
   * send it to nao
   * wait non blocking on anwser
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
            (answer(socket.recv(0), c), caller)
          }
        }
      answering onSuccess {
        case (answer, caller) => {
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