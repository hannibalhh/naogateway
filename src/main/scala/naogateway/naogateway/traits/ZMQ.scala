package naogateway.traits

import akka.actor.Actor
import org.zeromq.ZContext
import org.zeromq.ZMQ.REQ
import org.zeromq.ZMQ.Socket

/**
 * Simple trait for actors which want to use ZMQ
 */
trait ZMQ extends Actor{
  /**
   * Initiate a ZMQ Context one time which is manage sockets for every 
   * ZMQ Actor
   */
  import org.zeromq.ZContext
  val zmqcontext = new ZContext
  
  /**
   * create a zmq socket with type REQ (request) and connect to url 
   */
  import org.zeromq.ZMQ.Socket
  def zmqsocket(host:String,port:Int):Socket = {
    import org.zeromq.ZMQ._
    val socket = zmqcontext.createSocket(REQ)
    socket.connect("tcp://" + host + ":" + port)
    socket
  }

  /**
   * destroy all open sockets in zmqcontext
   */
  def destroyAll = {
    import scala.collection.JavaConversions._
    for (s <- zmqcontext.getSockets)
      zmqcontext.destroySocket(s)
  }

  /**
   * on restart destroy all sockets
   */
  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {	  
    destroyAll
    context.children foreach { child =>
      context.unwatch(child)
      context.stop(child)
    }
    postStop()
  }
}
