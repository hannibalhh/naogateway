package test.simple

import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem
import naogateway.scaleNaoSystem
import akka.actor.ActorRef
import naogateway.value._
import naogateway.value.NaoMessages._
import naogateway.value.NaoMessages.Conversions._
import akka.actor.Props

object NaoActorTest extends App {
  val naoActor = scaleNaoSystem("nila")
  val system = ActorSystem("TestSystem") 
  system.actorOf(Props[ResponseTestActor])	
  
  class ResponseTestActor extends TestActor {
    override def preStart = naoActor ! Connect

    def receive = {
      case (response: ActorRef, noResponse: ActorRef, vision: ActorRef) => {
        trace(response)
        trace(noResponse)
        trace(vision)
//        response ! Call('ALTextToSpeech, 'getVolume)
        response ! Call('ALTextToSpeech, 'say, List("Stehen bleiben"))
      }    
      case x => trace(x)
    }
  }
}