package test.simple

import akka.actor.ActorSystem
import akka.actor.Actor

/**
 * a simple trait to log in actors
 * you have a trace method for info logging
 * you habe a error method for warnings
 * and a wrongMessage method to log wrong messages
 * logging to context of own actorsystem
 */
trait TestActor extends Actor {	
    def trace(a: Any) = log.info(a.toString)
    def error(a: Any) = log.warning(a.toString)
    def wrongMessage(a: Any, state: String) = log.warning("wrong message: " + a + " in " + state)
    import akka.event.Logging
    val log = Logging(context.system, this)
}