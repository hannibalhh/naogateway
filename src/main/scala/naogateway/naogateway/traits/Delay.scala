package naogateway.traits

import akka.actor.ActorContext

/**
 * Delay communication for actors by configuration
 */
trait Delay {
  /**
   * need be conform with Actor trait (must be protected)
   */
  protected val context: ActorContext

  /**
   * Configured delays like
   * responseactor.delay {
   * module.method = intvalue
   * }
   * will be provided as a map String -> Int
   */
  val delays = context.system.settings.config.getConfig("responseactor.delay")

  /**
   * delay in milli seconds, if it is not declared in config: result 0
   */
  def delay(s:String) = if (delays.hasPath(s.toLowerCase)) delays.getInt(s.toLowerCase) else 0
  implicit def symbolToString(s:Symbol) = s.name
 
}