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
   * Because of no finished HeartBeatActor implementation at this time
   * NaoActor sends the positive HeartBat Online message
   */		  
  override def preStart = self ! Online

//  /**
//   * HeartBeat actor checks the connection state
//   * HeartBeat need the connecting information for starting
//   */	  
//  val heartbeat = context.actorOf(Props[HeartBeatActor])
//  override def preStart = heartbeat ! nao
		  		
  /**
   * In the start state actor wait for HeartBeat
   * Is Nao online, children will be started
   * Connect messages could only processed after Online message
   */
  def receive = {
    case Online => {
      trace(nao + " is online")
      val response = context.actorOf(Props[ResponseActor],"response")
      val noResponse = context.actorOf(Props[NoResponseActor].withDispatcher("akka.actor.default-stash-dispatcher"),"noresponse")
      val vision = context.actorOf(Props[VisionActor],"vision")     
      response ! nao
      noResponse ! nao
      vision ! naoVision
      unstashAll
      become(communicating(response,noResponse,vision))
    }
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
    case x => wrongMessage(x, "communicating")
  }
  
  trace("is started ")
}

