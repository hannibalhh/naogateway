package test.simple

import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem
import naogateway.Gateway
import akka.actor.ActorRef
import naogateway.value._
import naogateway.value.NaoMessages._
import naogateway.value.NaoMessages.Conversions._
import akka.actor.Props

/**
 * A local runnable test of NaoActorTest
 * starts a gateway
 * starts a test actor ResponseTestActor  
 * send Connect mesage to naoactor
 * make a call to response actor
 */
object NaoActorTest extends App {
  val naoActor = Gateway("localnila").naoActor
  val system = ActorSystem("TestSystem") 
  
  system.actorOf(Props[ResponseTestActor])	
    
  class ResponseTestActor extends TestActor {
    override def preStart = naoActor ! Connect
 
    def receive = {
      case (response: ActorRef, noResponse: ActorRef, vision: ActorRef) => {
        trace(response)
        trace(noResponse)
        trace(vision)
        response ! Call('ALTextToSpeech, 'getVolume)
//        noResponse ! Call('ALTextToSpeech, 'say, List("Stehen bleiben"))
      }    
      case x => trace(x)
    }
  }
}