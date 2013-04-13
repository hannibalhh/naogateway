package naogateway

import akka.actor.ActorSystem
import akka.actor.Props
import com.typesafe.config.ConfigFactory

object scaleNaoSystem{
  val config = ConfigFactory.load()
  val name = "naogateway"
  def apply(nao:String = "nila") = {
    val system = ActorSystem(name,config.getConfig(name+"."+nao).withFallback(config.getConfig(name)))
    system.actorOf(Props[NaoActor].withDispatcher("akka.actor.default-stash-dispatcher"),nao)	
  }
}

object NaogatewayApp {
  val usage = """
    Usage: naogateway [absoluteconfigpath]
  """
  def main(args: Array[String]) {
    if (args.length == 0) println(usage)
    val arglist = args.toList
    type OptionMap = Map[Symbol, Any]

    def nextOption(map : OptionMap, list: List[String]) : OptionMap = {
      def isSwitch(s : String) = (s(0) == '-')
      list match {
        case Nil => map
        case "--max-size" :: value :: tail =>
                               nextOption(map ++ Map('maxsize -> value.toInt), tail)
        case "--min-size" :: value :: tail =>
                               nextOption(map ++ Map('minsize -> value.toInt), tail)
        case string :: opt2 :: tail if isSwitch(opt2) => 
                               nextOption(map ++ Map('infile -> string), list.tail)
        case string :: Nil =>  nextOption(map ++ Map('infile -> string), list.tail)
        case option :: tail => println("Unknown option "+option) 
                               exit(1) 
      }
    }
    val options = nextOption(Map(),arglist)
    println(options)
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