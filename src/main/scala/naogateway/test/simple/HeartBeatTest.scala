package test.simple

import akka.actor.ActorSystem
import naogateway.Gateway
import akka.actor.Props
import naogateway.HeartBeatActor
import naogateway.value.NaoMessages.Nao
import naogateway.value.NaoMessages.Trigger
import com.typesafe.config.ConfigFactory
/**
 * A local runnable test of HeartBeatActor
 * starts a gateway
 * starts a heartbeat manually in gateway
 * send heartbeat the connecting information from config
 */
object HeartBeatTest extends App{

  val config = ConfigFactory.load()
  val system = ActorSystem("test",config.getConfig("naogateway"))

  val nao = Nao("Nila", "127.0.0.1", 5555)
  
  val heart = system.actorOf(Props(new HeartBeatActor(nao)),"heartbeat")
  
  heart ! Trigger

 
		  		
}