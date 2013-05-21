package test.simple

import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem
import naogateway.Gateway
import akka.actor.ActorRef
import naogateway.value._
import naogateway.value.NaoMessages._
import naogateway.value.NaoMessages.Conversions._
import akka.actor.Props
import naogateway.value.HAWCamserverMessages.CamResponse
import naogateway.value.NaoVisionMessages._

/**
 * A local runnable test of NaoActorTest
 * starts a gateway
 * starts a test actor ResponseTestActor  
 * send Connect mesage to naoactor
 * make a call to response actor
 */
object NaoActorTest extends App {
  val naoActor = Gateway("nila").naoActor
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
//        response ! Call('ALMotion, 'getCOM,List("HeadYaw",1,true))
//        noResponse ! Call('ALTextToSpeech, 'say, List("Stehen bleiben"))
         vision ! VisionCall(Resolutions.kVGA,ColorSpaces.kRGB,Frames._30)

      } 
      case c:CamResponse => trace("cam: " +  c.getError() + ":" + c.getImageData().size+ " " + c.getImageData().toByteArray().length)

      case x => trace(x)
    }
  }
}