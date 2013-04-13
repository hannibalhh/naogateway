package test.directZMQ

import naogateway.value.Hawactormsg._

class SimpleRequest(address: String = "tcp://127.0.0.1:5555", tracing: Boolean = true) {

  import naogateway.value._
  val socket = zMQ.socket(url = address)

  object zMQ {
    import org.zeromq.ZContext
    def context = new ZContext
    def socket(cont: ZContext = context, url: String) = {
      import org.zeromq.ZMQ._
      val socket = cont.createSocket(REQ)
      socket.connect(url)
      socket
    }
  }

  trace("Socket binded with " + address)

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

  def toString(params: List[MixedValue]): String = {
    if (params.isEmpty)
      ""
    else
      "(" + params.head.getString() + ")" + toString(params.tail)
  }

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

  implicit def string2Mixed(s: String) = MixedValue.newBuilder().setString(s).build()

  def sequence = {
    openHandL
    openHandR
    closeHandR
    closeHandL
  }

  def getVolume = request("ALTextToSpeech", "getVolume")
  def say(s: String) = request("ALTextToSpeech", "say", List(s))
  def closeHandL = request("ALMotion", "closeHand", List("LHand"))
  def openHandL = request("ALMotion", "openHand", List("LHand"))
  def closeHandR = request("ALMotion", "closeHand", List("RHand"))
  def openHandR = request("ALMotion", "openHand", List("RHand"))

  def trace(a: Any) = if (tracing) println("SimpleRequestTest: " + a)
}