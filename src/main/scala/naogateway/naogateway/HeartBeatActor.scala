package naogateway

import akka.actor.Actor
import akka.actor.Props

/**
 * HeartBeatActor is used to know nao connection status
 * actor use zmq messages to test wheter nao answer before timeout
 * configuration standard
 * 	heartbeatactor {
 * 		online.delay = 1000
 * 		maybeoffline.delay = 300
 * 	}
 * 	heartbeat is very stateful but thats the intention
 * 	if actor must be restarted nao status is unknown, so start state is
 * 	the right state
 */
class HeartBeatActor extends Actor with ZMQ with Log {

  import naogateway.value._
  import naogateway.value.NaoMessages._
  import naogateway.value.NaoMessages.Conversions
  import context._

  /**
   * start state is waiting on nao connecting information
   */
  def receive = {
    case nao: Nao => step(nao, online(nao), Online, maybeOffline(nao), MaybeOffline, on)
  }

  /**
   * maybeoffline is a state which suppose that nao is not online
   * but give the nao up to 3 chances
   */
  def maybeOffline(nao: Nao, count: Int = 0): Receive = {
    case Trigger if count <= 3 => {
      step(nao, online(nao), Online, maybeOffline(nao, count + 1), MaybeOffline, off)
    }
    case _ => {
      step(nao, online(nao), Online, receive, Offline, off)
    }
  }

  /**
   * online state suppose nao is online and wait on next trigger
   */
  def online(nao: Nao): Receive = {
    case Trigger => step(nao, online(nao), Online, maybeOffline(nao), MaybeOffline, on)
  }

  /**
   * In communicating state the actor takes every call and
   * convert to nao proto
   * send it to nao
   * wait non blocking on anwser
   */
  import scala.concurrent.duration._
  import org.zeromq.ZMQ.Socket
  def step(nao: Nao, suc: PartialFunction[Any, Unit], succMessage: Any, fail: PartialFunction[Any, Unit], failMessage: Any, timeout: FiniteDuration) = {
    val c = Call(Module("test"), Method("test"))
    val socket = zMQ.socket(nao.host, nao.port)
    socket.send(request(c).toByteArray, 0)

    import scala.concurrent._
    val caller = sender
    val answering = future {
      Future {
        socket.recv(0)
      }
    }
    try {
      val ts = Await.ready(answering, timeout).fallbackTo {
        Future {
          trace("!!!!")

          sender ! failMessage
          delay(off)
          trace("off")
          become(fail)
        }

      }
      ts onSuccess {
          case _ => {
            //        socket.close
            sender ! succMessage
            delay(on)
            trace("on")
            become(suc)
          }
        }
    }

    //    import context.dispatcher
    //    import akka.pattern.after   
    //    after(timeout, using = context.system.scheduler) {
    //      import scala.concurrent.Future
    //      Future {
    //        if (answering.isCompleted){
    //	        socket.close
    //	        sender ! succMessage
    //	        delay(on)
    //	        trace("on")
    //	        become(suc)
    //        }
    //        else {
    //	         socket.close
    //	        sender ! failMessage
    //	        delay(off)
    //	         trace("off")
    //	        become(fail)         
    //        }
    //      }
    //    }

    //    answering onSuccess {
    //      case _ => {
    ////        socket.close
    //        sender ! succMessage
    //        delay(on)
    //        trace("on")
    //        become(suc)
    //      }
    //    }
    //    answering onFailure {
    //      case _ => {
    //        socket.close
    //        sender ! failMessage
    //        delay(off)
    //        trace("off")
    //        become(fail)
    //      }
    //    }
  }

  /**
   * A delay is realized as a future with after pattern
   * Future which should send actor a Trigger message
   * will be start after duration by execution context
   */
  import scala.concurrent.duration._
  def delay(d: Duration) = {
    import context.dispatcher
    import akka.pattern.after
    after(2000 millis, using = context.system.scheduler) {
      import scala.concurrent.Future
      Future {
        self ! Trigger
      }
    }
  }
  val on = context.system.settings.config.getInt("heartbeatactor.online.delay") millis
  val off = context.system.settings.config.getInt("heartbeatactor.online.delay") millis

  trace("is started ")
}