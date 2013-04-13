package naogateway

import akka.actor.ActorSystem
import akka.actor.Props
import com.typesafe.config.ConfigFactory
import java.io.File

//object scaleNaoSystem{
//  val config = ConfigFactory.load()
//  val name = "naogateway"
//  def apply(nao:String = "nila") = {
//    val system = ActorSystem(name,config.getConfig(name+"."+nao).withFallback(config.getConfig(name)))
//    system.actorOf(Props[NaoActor].withDispatcher("akka.actor.default-stash-dispatcher"),nao)	
//  }
//}
case class scaleNaoSystem(nao:String = "nila",configPath:String = ""){
   val config = {
     configPath match {
       case "" => ConfigFactory.load()
       case x => ConfigFactory.parseFile(new File(x))
     }    
   }
   val name = "naogateway"
   val system = ActorSystem(name,config.getConfig(name+"."+nao).withFallback(config.getConfig(name)))
   val naoActor = system.actorOf(Props[NaoActor].withDispatcher("akka.actor.default-stash-dispatcher"),nao)
}

class SimpleRemoteTest(nao:String,host:String,port:String) {  
  import akka.actor.ActorSystem
  import akka.actor.Actor
  import com.typesafe.config.ConfigFactory
  import akka.actor.ActorRef
  import akka.actor.Props
  import naogateway.value.NaoMessages._
  import naogateway.value.NaoMessages.Conversions._
  import akka.actor.Address
  import naogateway.value.NaoVisionMessages._
  val config = ConfigFactory.load()
  val system = ActorSystem("remoting",config.getConfig("remoting").withFallback(config)) 
  val naoActor = system.actorFor("akka://naogateway@"+host+":"+port+"/user/"+nao+"/response")
   naoActor ! Call('ALTextToSpeech, 'say, List("Stehen bleiben!"))
 
}

object NaogatewayApp {
  val usage = """
    Usage: naogateway 
		  [-n | --name naoname = nila] 
		  [-c | --config absolutepath = naogateway/src/main/resources/application.conf]
		  [-t | --test host port]
		  [-h | --help]
  """
  def main(args: Array[String]) {

    val arglist = args.toList
    type OptionMap = Map[Symbol, Any]

    def nextOption(map : OptionMap, list: List[String]) : OptionMap = {
      def isSwitch(s : String) = (s(0) == '-')
      list match {
        case Nil => map
        case ("-h" | "--help") :: tail  => {
          println(usage)
          if(list.size == 1) 
            exit(0) 
          nextOption(map,tail)
        }
        case ("--name" | "-n") :: value :: tail => nextOption(map ++ Map('name -> value), tail)
        case ("--config" | "-c") :: value :: tail => nextOption(map ++ Map('config -> value), tail)
        case ("--test" | "-t") :: host :: port :: tail => nextOption(map ++ Map('testhost -> host,'testport -> port), tail)
        case ("--test" | "-t") :: host :: tail => nextOption(map ++ Map('testhost -> host,'testport -> "2552"), tail)
        case ("--test" | "-t") :: tail => nextOption(map ++ Map('testhost -> "127.0.0.1",'testport -> "2552"), tail)

        case option :: tail => println("Unknown option "+option) 
                               exit(1) 
      }
    }
    val options = nextOption(Map('name -> "nila",'config -> ""),arglist)
    implicit  def any2String(a:Any) = a.toString
    if(options.contains('testhost) && options.contains('testport))
       new SimpleRemoteTest(options('name),options('testhost),options('testport))
    else  
    	scaleNaoSystem(options('name),options('config))
  }
}