package naogateway.value

 
object NaoMessages {
  import naogateway.value.Hawactormsg._	
  trait Message
  trait InfoMessage extends Message
  trait ErrorMessage extends Message

  trait DataMessage
  trait InMessage
  trait OutMessage

  trait Calling {
    val isDefined = true
  }

  case class Call(module: Module, method: Method, parameters: List[MixedValue] = Nil) extends DataMessage with Calling with OutMessage {
    override def toString = "Call(" + module.title + "." + method.title + "(" + params + "))"
    def actorName = "Call:" + module.title + "." + method.title + ":" + params + ""

    def stringParameters = parameters.map(x => NaoMessages.toString(x))

    private def params = {
      if (parameters.isEmpty)
        ""
      else
        parameters.map(x => NaoMessages.toString(x)).reduceLeft((r, x) => (r + "," + x))
    }
  }

  case class InvalidCall(call: Call) extends OutMessage with Calling with ErrorMessage {
    override val isDefined = false
  }

  trait Answering {
    val call: Call
  }
  case class Answer(override val call: Call, value: MixedValue) extends DataMessage with InMessage with Answering {
    override def toString = "Answer(" + call + ": " + NaoMessages.toString(value) + ")"
  }

  case class InvalidAnswer(override val call: Call) extends InMessage with ErrorMessage with Answering

  case class AnswerTimedOut(override val call: Call) extends InMessage with ErrorMessage with Answering

  trait Event extends DataMessage with InMessage

  case class Module(title: String)
  case class Method(title: String)

  trait HeartBeatState
  case object Online extends HeartBeatState
  case object Offline extends HeartBeatState
  case object MaybeOffline extends HeartBeatState
  case object Trigger
  
  case object Connect

  object Conversions {

    implicit def string2Mixed(s: String) = MixedValue.newBuilder().setString(s).build()
    implicit def int2Mixed(i: Int) = MixedValue.newBuilder().setInt(i).build()
    implicit def float2Mixed(f: Float) = MixedValue.newBuilder().setFloat(f).build()
    implicit def bool2Mixed(b: Boolean) = MixedValue.newBuilder().setBool(b).build()

    implicit def symbol2Module(s: Symbol) = Module(s.name)
    implicit def symbol2Method(s: Symbol) = Method(s.name)
    implicit def string2Module(s: String) = Module(s)
    implicit def string2Method(s: String) = Method(s)

    implicit def symbol2String(s: Symbol) = s.name
    implicit def string2Symbol(s: String) = Symbol(s)

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
          case x: String => mixedVal.addArray(x)
          case x => {
            println("invalid value: " + value)
            throw new UnsupportedOperationException(x.getClass.toString + " is not allowed")
          }
        }
      mixedVal.build()

    }

  }

  implicit def HAWActorRPCResponseTOByteArray(r: Array[Byte]) = HAWActorRPCResponse.parseFrom(r)
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
    val param = HAWActorRPCRequest.newBuilder.setModule(c.module.title).setMethod(c.method.title);
    for (mixed <- c.parameters)
      param.addParams(mixed)
    param.build
  }

  val empty = HAWActorRPCResponse.parseFrom(com.google.protobuf.ByteString.EMPTY.toByteArray())

  def toString(mixed: MixedValue): String = {
    if (mixed.hasInt) String.valueOf(mixed.getInt)
    else if (mixed.hasFloat()) String.valueOf(mixed.getFloat)
    else if (mixed.hasBool()) String.valueOf(mixed.getBool)
    else if (mixed.hasString()) mixed.getString;
    else if (mixed.hasBinary()) "Binary Data";
    else if (mixed.getArrayCount() > 0) {
      val s = new StringBuilder();
      s.append("[");
      for (i <- 0 until mixed.getArrayCount) {
        s.append(toString(mixed.getArray(i)))
        if (i < mixed.getArrayCount - 1)
          s.append(",");
        else
          ""
      }
      s.append("]")
      s.toString
    } else "Empty"
  }

  class InvalidValueException(message: String, cause: Throwable)
    extends RuntimeException(message) {
    if (cause != null)
      initCause(cause)

    def this(message: String) = this(message, null)
  }

}