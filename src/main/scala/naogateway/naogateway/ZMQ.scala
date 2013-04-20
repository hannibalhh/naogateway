package naogateway

/**
 * Simple trait for actors which want to use ZMQ
 */
trait ZMQ {
  val zMQ = new ZMQAdapter
}

/**
 * zMQ object contains a ZMQ Context with sockets
 * only request type of sockets (request reply pattern
 * have to be strongly observed) is here allowed
 */
class ZMQAdapter {
  import org.zeromq.ZContext
  def context = new ZContext
  import org.zeromq.ZMQ.Socket
  def socket(host:String,port:Int):Socket = socket(url = "tcp://" + host + ":" + port)
  def socket(cont: ZContext = context, url: String) = {
    import org.zeromq.ZMQ._
    val socket = cont.createSocket(REQ)
    socket.connect(url)
    socket
  }
}