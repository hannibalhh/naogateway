package test.simple

import akka.actor.ActorSystem
import naogateway.Gateway
import akka.actor.Props
import naogateway.HeartBeatActor
import naogateway.value.Nao

/**
 * A local runnable test of HeartBeatActor
 * starts a gateway
 * starts a heartbeat manually in gateway
 * send heartbeat the connecting information from config
 */
object HeartBeatTest extends App{

  val gateway = Gateway("localnila")
  val heart = gateway.system.actorOf(Props[HeartBeatActor])	
  
  val nao = Nao(gateway.system.settings.config.getString("nao.name"),
		  		gateway.system.settings.config.getString("nao.host"),
		  		gateway.system.settings.config.getInt("nao.port"))
  heart ! nao
		  		
}