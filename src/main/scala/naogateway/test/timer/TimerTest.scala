package test.timer

import akka.actor.Actor
import scala.concurrent.Future
import akka.actor.ActorSystem
import akka.actor.Props


object TimerTest extends App{

  val system = ActorSystem("TimerSystem")
  system.actorOf(Props[TimerActor]) ! "foo"
    
  case object TimeOut
  class TimerActor extends Actor {
  	def receive = {
  	  case TimeOut => println("Timeout")
  	  case _ => 
  	     import context.dispatcher
  	     import akka.pattern.after
  	     import scala.concurrent.duration._
  	     after(2000 millis, using = context.system.scheduler){
  	       import scala.concurrent.Future
  	       Future{
  	         self ! TimeOut
  	       }
  	     }
  	    
  
  	  
  	   
    }
  }
}