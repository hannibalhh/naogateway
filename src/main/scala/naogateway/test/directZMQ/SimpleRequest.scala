package test.directZMQ

import naogateway.value.Hawactormsg._

/**
 * SimpleReq is a example to communicate with nao without actorsystem
 * parameter address like tcp://127.0.0.1:5555
 * parameter tracing logging enabled or not
 */
class SimpleReq(address: String = "tcp://127.0.0.1:5555", tracing: Boolean = true) {

  import naogateway.value._
  import naogateway.value.NaoMessages.Conversions._
  val socket = zMQ.socket(url = address)

  /**
   * connect with request socket
   */
  object zMQ {
    import org.zeromq.ZContext
    val context = new ZContext
    def socket(url: String) = {
      import org.zeromq.ZMQ
      val sock = context.createSocket(ZMQ.REQ)
      sock.connect(url)
      sock
    } 
  } 

  trace("Socket binded with " + address)

  /**
   * receive answers and build protobuf datastructure HAWActorRPCResponse
   * and then it will be logged
   */
  def answer = {
    val protoResponse = HAWActorRPCResponse.parseFrom(socket.recv(0))
    if (protoResponse.hasError) {
      trace("Error: " + protoResponse.getError)
    } else if (protoResponse.hasReturnval) {
      trace("-> " + NaoMessages.toString(protoResponse.getReturnval))
    } else {
      trace("-> Empty \n");
    }
  }  

  /**
   * simple toString for List[MixedValue]
   */
  def toString(params: List[MixedValue]): String = {
    if (params.isEmpty)
      ""
    else
      "(" + params.head.getString() + ")" + toString(params.tail)
  }

  /**
   * build request with module, method and params of MixedValue
   * will be send to socket
   * and call answer
   */
  def request(module: String, method: String, params: List[MixedValue] = Nil) {
    trace("request: " + module + "." + method + "" + toString(params))

    val param = HAWActorRPCRequest.newBuilder().setModule(module).setMethod(method);
    for (mixed <- params) {
      param.addParams(mixed)
    }

    val rpcReq = param.build
    socket.send(rpcReq.toByteArray, 0)
    answer
  }

  /**
   * small sequence of hand opening and closing
   */
  def sequence = {
    openHandL
    openHandR
    closeHandR
    closeHandL
  }
  
  /**
   * example calls
   */
  def test = request("test", "test") // undefined method -> Error
  def com = request("ALMotion", "getCOM", List("HeadYaw",1,true))
  def getVolume = request("ALTextToSpeech", "getVolume")
  def say(s: String) = request("ALTextToSpeech", "say", List(s))
  def closeHandL = request("ALMotion", "closeHand", List("LHand"))
  def openHandL = request("ALMotion", "openHand", List("LHand"))
  def closeHandR = request("ALMotion", "closeHand", List("RHand"))
  def openHandR = request("ALMotion", "openHand", List("RHand"))

  def trace(a: Any) = if (tracing) println("SimpleRequestTest: " + a)
}