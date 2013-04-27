package test.remote

  import akka.actor.ActorSystem
  import akka.actor.Actor
  import akka.actor.ActorRef
  import akka.actor.Props
  import com.typesafe.config.ConfigFactory

  import naogateway.value.NaoMessages._
  import naogateway.value.NaoMessages.Conversions._
  import naogateway.value.NaoVisionMessages._

/**
 * RemoteTest is a runnable client appliction to send 
 * some calls to naogateway, it uses namespace remoting in configuration
 * You need same network of naogateway
 * This example maximized small and without an actor
 */
object SimpleRemoteTest extends App  {
    val host = "192.168.1.100"
    val port = "2552" 
    val nao = "hanna"
	val config = ConfigFactory.load() 
    val system = ActorSystem("remoting",config.getConfig("remoting").withFallback(config)) 
    val naoActor = system.actorFor("akka://naogateway@"+host+":"+port+"/user/"+nao+"/response")
    
    naoActor ! Call('ALTextToSpeech, 'say, List("Stehen bleiben!"))
}