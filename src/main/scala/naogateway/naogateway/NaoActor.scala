 package naogateway

import akka.actor.Actor
 import akka.actor.ActorRef
 import akka.actor.Props
 import akka.actor.Status.Success
 import akka.util.Timeout
 import scala.concurrent.duration._
 import akka.actor.ActorRef
 import akka.actor.ActorPathExtractor
 import akka.actor.Stash
 import naogateway.traits.Log

 class NaoActor extends Actor with Stash with Log{

  import naogateway.value._
  import naogateway.value.NaoMessages._
  import naogateway.value.NaoMessages.Conversions
  import context._ 
  
  val nao = Nao(context.system.settings.config.getString("nao.name"),
		  		context.system.settings.config.getString("nao.host"),
		  		context.system.settings.config.getInt("nao.port"))
		  		
  val naoVision = Nao(context.system.settings.config.getString("nao.name"),
		  		context.system.settings.config.getString("nao.host"),
		  		context.system.settings.config.getInt("nao.camport"))
  
  /**
   * HeartBeat actor checks the connection state
   * HeartBeat need the connecting information for starting
   */	
  override def preStart = {
    // if heartbeat activated
    if (context.system.settings.config.getBoolean("heartbeatactor.activated"))
    	context.actorOf(Props(new HeartBeatActor(nao)),"HeartBeat")
    // alibi message that nao is offline
    else
      self ! Online
  }
		  		
  /**
   * In the start state actor wait for HeartBeat
   * Is Nao online, children will be started
   * Connect messages could only processed after Online message
   */
  def receive = {
    case Online => {
      trace(nao + " is online")
      val response = context.actorOf(Props(new ResponseActor(nao)),"response")
      val noResponse = context.actorOf(Props(new NoResponseActor(nao)),"noresponse")
      val vision = context.actorOf(Props(new VisionActor(nao)),"vision")     
      unstashAll
      become(communicating(response,noResponse,vision))
    }
    case Offline => throw new RuntimeException("nao is not available")
    case Connect => stash
    case x => wrongMessage(x, "receive")
  }

  /**
   * In communicating state the actor guard 
   * their children with the heartbeat
   */
  def communicating(response:ActorRef,noResponse:ActorRef,vision:ActorRef): Receive = {
    case Connect => {
      sender ! (response,noResponse,vision)
    }
    case Offline => throw new RuntimeException("nao is not available")
    case Online =>
    case x => wrongMessage(x, "communicating")
  }
  
  trace("is started ")
}

