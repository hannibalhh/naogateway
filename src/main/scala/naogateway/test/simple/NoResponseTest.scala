package test.simple

import akka.actor.Actor
import akka.actor.Props
import naogateway.value._
import naogateway.value.NaoMessages._
import naogateway.value.NaoMessages.Conversions._
import naogateway.NoResponseActor
import akka.dispatch.UnboundedDequeBasedMailbox
import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem

/**
 * A local runnable test of NoResponseActor
 * starts a test actor ResponseTestActor  
 * make many say calls to noresponse actor
 */
object NoResponseTest extends App {

  val system = ActorSystem("scaleNao")

  system.logConfiguration

  val noResponseActor = system.actorOf(Props[NoResponseActor].withDispatcher("akka.actor.default-stash-dispatcher"))
  noResponseActor ! Nao("Nila", "127.0.0.1", 5555)
  for (i <- 0 to 4)
    system.actorOf(Props[NoResponseTestActor])

  class NoResponseTestActor extends TestActor {
    override def preStart = {
      for (i <- 0 to 10000)
        noResponseActor ! Call('ALTextToSpeech, 'say, List("Hello World!" + i))
    }
    def receive = {
      case x => trace("Answer: " + x)
    }
  }
}
  
