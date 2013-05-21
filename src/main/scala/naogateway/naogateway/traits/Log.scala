package naogateway.traits

import akka.actor.Actor
import akka.event.Logging

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
  val logname:String
  def trace(a: Any) = if (context.system.settings.config.getBoolean("log."+logname+".info")) log.info(a.toString)
  def error(a: Any) = if (context.system.settings.config.getBoolean("log."+logname+".error")) log.warning(a.toString)
  def wrongMessage(a: Any, state: String) = if (context.system.settings.config.getBoolean("log."+logname+".wrongMessage")) log.warning("wrong message: " + a + " in " + state)
  import akka.event.Logging
  val log = Logging(context.system, this)
}