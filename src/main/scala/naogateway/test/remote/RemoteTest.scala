package test.remote
import akka.actor.ActorSystem
import akka.actor.Actor
import com.typesafe.config.ConfigFactory
import akka.actor.ActorRef
import akka.actor.Props
import naogateway.value.NaoMessages._
import naogateway.value.NaoMessages.Conversions._
import akka.actor.Address
import naogateway.value.NaoVisionMessages._

object RemoteTest extends App{  
  val config = ConfigFactory.load()
  val system = ActorSystem("remoting",config.getConfig("remoting").withFallback(config))

//  val a = Address("akka", "naogateway", "localhost", 2552)
 
  val naoActor = system.actorFor("akka://naogateway@127.0.0.1:2552/user/nila")
  println(naoActor)
  system.actorOf(Props[MyResponseTestActor])	
  
  class MyResponseTestActor extends Actor  {
    override def preStart = naoActor ! Connect

    def receive = {
      case (response: ActorRef, noResponse: ActorRef, vision: ActorRef) => {
        trace(response)
        trace(noResponse)
        trace(vision)
//         response ! Call('ALTextToSpeech, 'getVolume)
//         response ! Call('ALTextToSpeech, 'getVolume)
        noResponse ! Call('ALTextToSpeech, 'say, List("Stehen bleiben!"))
//        response ! Call('ALTextToSpeech, 'say, List("Stehen bleiben!"))
//        vision ! VisionCall(Resolutions.k4VGA,ColorSpaces.kBGR,Frames._20)
//        vision ! RawVisionCall(Resolutions.k4VGA,ColorSpaces.kBGR,Frames._20)
        context.become(next(response,noResponse,vision))
      }    
      case x => trace(x)
    }
    
    def next(response: ActorRef, noResponse: ActorRef, vision: ActorRef):Receive = {
      case x => {
        trace(x)
        Call('ALTextToSpeech, 'say, List("Danke!"))
      }
    }
    
        def trace(a: Any) = log.info(a.toString)
    def error(a: Any) = log.warning(a.toString)
    def wrongMessage(a: Any, state: String) = log.warning("wrong message: " + a + " in " + state)
    import akka.event.Logging
    val log = Logging(context.system, this)
  }
}