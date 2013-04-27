package naogateway

import akka.actor.Actor
import akka.actor.ActorRef
import naogateway.traits.Delay
import naogateway.traits.ZMQ
import naogateway.traits.Log
/**
 * NaoResponseActor send calls to nao and send the answer to caller
 */
class ResponseActor extends Actor with Delay with ZMQ with Log{
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
      trace(delays)
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
    case c: Call => {
      trace("request: " + c)
      val socket = zmqsocket(nao.host,nao.port)

      import akka.pattern.after
      import context.dispatcher
      import scala.concurrent.duration._
      val caller = sender
      val answering = after(delay(c.module.title + "." + c.method.title) millis,
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