package test.simple

import akka.actor.Actor
import akka.actor.Props
import naogateway.value._
import naogateway.value.NaoMessages._
import naogateway.value.NaoMessages.Conversions._
import naogateway.ResponseActor
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
 
/**
 * A local runnable test of ResponseActor
 * starts one of some test actors   
 * make many say calls to response actor
 * you can test that answers of different calls could be identified
 */
object ResponseTest extends App {

  val config = ConfigFactory.load()
  val system = ActorSystem("test",config.getConfig("naogateway"))

  val nao = Nao("Nila", "192.168.1.10", 5555)
  val responseActor = system.actorOf(Props(new ResponseActor(nao)))
  system.actorOf(Props[ResponseTestActor])
  system.actorOf(Props[ResponseActorTestSayOne])
  system.actorOf(Props[ResponseActorTestSayTwo])
//  system.actorOf(Props[ResponseActorTestSayOne])
//  system.actorOf(Props[ResponseActorTestSayTwo])

  class ResponseTestActor extends TestActor {
    override def preStart = {
        responseActor ! Call('ALTextToSpeech, 'say, List("One"))
    }
    def receive = {
      case x => println("Answer: " + x)
    }
  }

  class ResponseActorTestSayOne extends TestActor {
    override def preStart = {
      for (i <- 0 to 100)
        responseActor ! Call('ALTextToSpeech, 'say, List("One"))
    }
    def receive = {
      case x: Answer => {
        trace(x)
        if (x.call.stringParameters.head != "One") 
          trace("Error:Answer for One: ." + x.call.stringParameters.head +"."+ x)
      }
    }

  }
  class ResponseActorTestSayTwo extends TestActor {
    override def preStart = {
      for (i <- 0 to 100)
        responseActor ! Call('ALTextToSpeech, 'say, List("Two"))
    }
    def receive = {
      case x: Answer => {
        println(x)
        if (x.call.stringParameters.head != "Two") 
          trace("Error:Answer for Two: ." + x.call.stringParameters.head +"."+ x)
      }
    }
  }
}