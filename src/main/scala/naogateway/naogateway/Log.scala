package naogateway

import akka.actor.ActorContext
import akka.actor.Actor

/**
 * Easy Logging for Actor combined with configuration like
 * 	log {
	   naoactor{
		info = true
		error = true
		wrongMessage = true
	  }
	}
 */
trait Log extends Actor{
  def trace(a: Any) = if (context.system.settings.config.getBoolean("log.responseactor.info")) log.info(a.toString)
  def error(a: Any) = if (context.system.settings.config.getBoolean("log.responseactor.error")) log.warning(a.toString)
  def wrongMessage(a: Any, state: String) = if (context.system.settings.config.getBoolean("log.responseactor.wrongMessage")) log.warning("wrong message: " + a + " in " + state)
  import akka.event.Logging
  val log = Logging(context.system, this)
}