package naogateway.value
 
object NaoMessages {
  import naogateway.value.Hawactormsg._	

  /**
   * Connecting information of nao with name, host and port
   */
  case class Nao(name: String, host: String, port: Int)

  /**
   * For ResponseActor and NoResponseActor could be send a call
   * to communicate with Nao
   * module, method and params like Naoqi API
   * Please note implicit conversion (import NaoMessages.Conversions._)
   */
  case class Call(module: Symbol, method: Symbol, parameters: List[MixedValue] = Nil) {
    override def toString = "Call(" + module.name + "." + method.name + "(" + params + "))"

    /**
     * generate string of parameters 
     */
    def stringParameters = parameters.map(x => NaoMessages.toString(x))

    /**
     * generate string of parameters for toString 
     */
    private def params = {
      if (parameters.isEmpty)
        ""
      else
        parameters.map(x => NaoMessages.toString(x)).reduceLeft((r, x) => (r + "," + x))
    }
  }

  /**
   * If a ResponseActor or NoResponseActor answer a call, it comes a instance of type Answering
   * Answering is a trait for Answer and InvalidAnswer
   */
  trait Answering {
    val call: Call
  }
  
  /**
   * 
   * Answer contains a MixedValue and the call to identify it
   */
  case class Answer(override val call: Call, value: MixedValue) extends Answering {
    override def toString = "Answer(" + call + ": " + NaoMessages.toString(value) + ")"
  }

  /**
   * Call on wrong messages, so it comes a 
   * invalid answer with call to identify
   */
  case class InvalidAnswer(override val call: Call) extends Answering


  /**
   * HeartBeatActor have different states
   * NaoActor get state information with methods of type HeartBeatState
   * like online or offline
   */
  trait HeartBeatState
  case object Online extends HeartBeatState
  case object Offline extends HeartBeatState
  case object MaybeOffline extends HeartBeatState
  
  /**
   * Trigger is a message to realize a timer
   */
  case object Trigger
  
  /**
   * Connect is a message to get ActorRef if response,noresponse,visionactor
   * from NaoActor
   */
  case object Connect

  /**
   * Conversions for MixedValue
   * please import it (import NaoMessages.Conversions._)
   * to write like Call('Test,'Test,Liste("",1,true))
   */
  object Conversions {

    implicit def string2Mixed(s: String) = MixedValue.newBuilder().setString(s).build()
    implicit def int2Mixed(i: Int) = MixedValue.newBuilder().setInt(i).build()
    implicit def float2Mixed(f: Float) = MixedValue.newBuilder().setFloat(f).build()
    implicit def bool2Mixed(b: Boolean) = MixedValue.newBuilder().setBool(b).build()

    implicit def stringArrToMixedVal(valueArr: Iterable[String]) = {
      val mixedVal = MixedValue.newBuilder()
      for (value <- valueArr)
        mixedVal.addArray(value)
      mixedVal.build()
    }

    implicit def floatArrToMixedVal(valueArr: Iterable[Float]) = {
      val mixedVal = MixedValue.newBuilder()
      for (value <- valueArr)
        mixedVal.addArray(value)
      mixedVal.build()
    }

    implicit def boolArrToMixedVal(valueArr: Iterable[Boolean]) = {
      val mixedVal = MixedValue.newBuilder()
      for (value <- valueArr)
        mixedVal.addArray(value)
      mixedVal.build()
    }

    implicit def byteArrToMixedVal(valueArr: Iterable[Byte]) = {
      val mixedVal = MixedValue.newBuilder()
      for (value <- valueArr)
        mixedVal.addArray(value)
      mixedVal.build()
    }

    implicit def arrayToMixedVal(array: Iterable[AnyVal]) = {
      val mixedVal = MixedValue.newBuilder()
      for (value <- array)
        value match {
          case x: Int => mixedVal.addArray(x)
          case x: Float => mixedVal.addArray(x)
          case x: Boolean => mixedVal.addArray(x)
          case x: Byte => mixedVal.addArray(x)
          case x => throw new UnsupportedOperationException(x.getClass.toString + " is not allowed")
        }
      mixedVal.build()
    }

    implicit def anyToMixedVal(array: Iterable[Any]) = {
      val mixedVal = MixedValue.newBuilder()
      for (value <- array)
        value match {
          case x: Int => mixedVal.addArray(x)
          case x: Float => mixedVal.addArray(x)
          case x: Boolean => mixedVal.addArray(x)
          case x: Byte => mixedVal.addArray(x)
          case x: String => mixedVal.addArray(x)
          case x => {
            println("invalid value: " + value)
            throw new UnsupportedOperationException(x.getClass.toString + " is not allowed")
          }
        }
      mixedVal.build()

    }

  }

  /**
   * implicit conversion of protobufmessage HAWActorRPCResponse to bytearray
   */
  implicit def HAWActorRPCResponseTOByteArray(r: Array[Byte]) = HAWActorRPCResponse.parseFrom(r)
  
  /**
   * Generate a Answer of HAWActorRPCResponse and Call
   */
  def answer(protoResponse: HAWActorRPCResponse, c: Call) = {
    if (protoResponse.hasError) {
      InvalidAnswer(c)
    } else if (protoResponse.hasReturnval) {
      Answer(c, protoResponse.getReturnval)
    } else {
      Answer(c, empty.getReturnval())
    }
  }

  def request(c: Call) = {
    val param = HAWActorRPCRequest.newBuilder.setModule(c.module.name).setMethod(c.method.name);
    for (mixed <- c.parameters)
      param.addParams(mixed)
    param.build
  }

  /**
   * standard empty value of HAWActorRPCResponse
   */
  val empty = HAWActorRPCResponse.parseFrom(com.google.protobuf.ByteString.EMPTY.toByteArray())

  /**
   * toString for MixedValue from David converted to scala
   */
  def toString(mixed: MixedValue): String = {
    if (mixed.hasInt) String.valueOf(mixed.getInt)
    else if (mixed.hasFloat()) String.valueOf(mixed.getFloat)
    else if (mixed.hasBool()) String.valueOf(mixed.getBool)
    else if (mixed.hasString()) mixed.getString;
    else if (mixed.hasBinary()) "Binary Data";
    else if (mixed.getArrayCount() > 0) {
      var s = "[";
      for (i <- 0 until mixed.getArrayCount) {
        s = s + toString(mixed.getArray(i))
        if (i < mixed.getArrayCount - 1)
          s = s + ","
        else
          ""
      }
      s + "]"      
    } else "Empty"
  }

}