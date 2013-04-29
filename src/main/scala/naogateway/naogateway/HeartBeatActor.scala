package naogateway

import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorRef
import naogateway.traits.Log
import naogateway.value.NaoMessages.Nao

/**
 * HeartBeatActor is used to know nao connection status
 * actor use zmq messages to test whether nao answer before timeout
 * configuration standard
 * 	heartbeatactor {
 * 		online.delay = 1000
 * 		maybeoffline.delay = 300
 * 	}
 * 	heartbeat is very stateful but thats the intention
 * 	if actor must be restarted nao status is unknown, so start state is
 * 	the right state
 */
class HeartBeatActor(nao:Nao) extends Actor with Log {

  val offlineTimes = context.system.settings.config.getInt("heartbeatactor.offlinetimes")
  val response = context.actorOf(Props(new ResponseActor(nao)), "response")

  import naogateway.value.NaoMessages._
  import naogateway.value.NaoMessages.Conversions
  import context._

  /**
   * start state is waiting on nao connecting information
   */
  def receive = {
    case Trigger => {
      step(nao, online(nao,sender), Online, maybeOffline(nao,sender), None, on,sender)
    }
  }

  /**
   * maybeoffline is a state which suppose that nao is not online
   * but give the nao up to 3 chances
   * one chance is used to go in maybeoffline, the last chance send on 
   * fail a Offline message instead of a MaybeOffline message,
   * because of that couting is offlineTimes-2 times by start at 0.
   */
  def maybeOffline(nao: Nao, caller:ActorRef,count: Int = 0): Receive = {
    case Trigger if count < offlineTimes-2 => {
      step(nao, online(nao,caller), Online, maybeOffline(nao,caller, count + 1), None, off,caller)
    }
    case _ => {
      step(nao, online(nao,caller), Online, receive, Offline, off,caller)
    }
  }

  /**
   * online state suppose nao is online and wait on next trigger
   */
  def online(nao: Nao,caller:ActorRef): Receive = {
    case Trigger => step(nao, online(nao,caller), Online, maybeOffline(nao,caller), None, on,caller)
  }

  /**
   * In communicating state the actor takes every call and
   * convert to nao proto
   * send it to nao with response actor
   * wait non blocking on anwser with ask pattern
   * you have to define parameter for success and fail
   * next state, message to sender and timeout duration
   */
  import scala.concurrent.duration._
  import org.zeromq.ZMQ.Socket
  def step(nao: Nao, suc: PartialFunction[Any, Unit], succMessage: Any, fail: PartialFunction[Any, Unit], failMessage: Any, d: FiniteDuration,caller:ActorRef) = {
    val c = Call('Test, 'Test)
    import scala.concurrent.Await
    import akka.pattern.ask
    import akka.util.Timeout
    import scala.concurrent.duration._
    implicit val timeout = Timeout(d)
    val answering = response ? c

    answering onSuccess {
      case _ => {
        if (succMessage != None)
        	caller ! succMessage
        delay(on)
        become(suc)
      }
    }
    answering onFailure {
      case _ => {
        if (failMessage != None)
        	caller ! failMessage
        delay(off)
        become(fail)
      }
    }
  }

  /**
   * A delay is realized as a future with after pattern
   * Future which should send actor a Trigger message
   * will be start after duration by execution context
   */
  import scala.concurrent.duration._
  def delay(d: FiniteDuration) = {
    import context.dispatcher
    import akka.pattern.after
    after(d, using = context.system.scheduler) {
      import scala.concurrent.Future
      Future {
        self ! Trigger
      }
    }
  }
  
  /**
   * delay of online state, next test message after this delay
   */
  val on = context.system.settings.config.getInt("heartbeatactor.online.delay") millis
  
  /**
   * delay of maybeoffline or offlinestate, next test message after this delay
   */
  val off = context.system.settings.config.getInt("heartbeatactor.online.delay") millis
  
  trace("is started ")
}