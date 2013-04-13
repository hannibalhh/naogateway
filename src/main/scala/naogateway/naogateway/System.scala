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

object NaogatewayApp {
  val usage = """
    Usage: naogateway [-name name] [-config absolutepath]
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
            exit(1) 
          nextOption(map,tail)
        }
        case ("--name" | "-n") :: value :: tail => nextOption(map ++ Map('name -> value), tail)
        case ("--config" | "-c") :: value :: tail => nextOption(map ++ Map('config -> value), tail)
        case option :: tail => println("Unknown option "+option) 
                               exit(1) 
      }
    }
    val options = nextOption(Map('name -> "nila",'config -> ""),arglist)

    scaleNaoSystem(options('name).toString,options('config).toString)
  }
}

object LogConf {
  object NaoActor{
    val info = true
    val error = true
    val wrongMessage = true
  }
  object ResponseActor{
    val info = true
    val error = true
    val wrongMessage = true
  }
  object NoResponseActor{
    val info = true
    val error = true
    val wrongMessage = true
  }
  object HeartBeatActor{
    val info = true
    val error = true
    val wrongMessage = true
  }
  object VisionActor{
    val info = true
    val error = true
    val wrongMessage = true
  }  
}