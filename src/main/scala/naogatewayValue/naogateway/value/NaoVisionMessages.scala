package naogateway.value

import com.google.protobuf.ByteString

object NaoVisionMessages{
  
  object Resolutions extends Enumeration {
    type Resolution = Value
    val kQQVGA, kQVGA, kVGA, k4VGA = Value
  }
  object ColorSpaces extends Enumeration {
    type ColorSpace = Value
    val kYuv = Value(0)
    val kYUV422 = Value(9)
    val kYUV = Value(10)
    val kRGB = Value(11)
    val kHSY = Value(12)
    val kBGR = Value(13)
  }
  
  object Frames extends Enumeration {
    type Frames = Value
    val _1,_2,_3,_4,_5,_6,_7,_8,_9,_10,
    _11,_12,_13,_14,_15,_16,_17,_18,_19,_20,
    _21,_22,_23,_24,_25,_26,_27,_28,_29,_30 = Value
  }
  trait VisionCalling{
    val resolution:Resolutions.Value
    val colorSpaces:ColorSpaces.Value
    val fps:Frames.Value
  }
  case class VisionCall(resolution:Resolutions.Value,colorSpaces:ColorSpaces.Value,fps:Frames.Value) extends VisionCalling
  case class RawVisionCall(resolution:Resolutions.Value,colorSpaces:ColorSpaces.Value,fps:Frames.Value) extends VisionCalling
  case class Picture(bytes:Array[Byte])
  
  def request(c: VisionCalling) = {
    val param = HawCam.CamRequest.newBuilder.setResolution(c.resolution.id).setColorSpace(c.colorSpaces.id).setFps(c.fps.id+1)
    param.build
  }
  
  
  implicit def CamResponseTOByteArray(r: Array[Byte]) = HawCam.CamResponse.parseFrom(r)
  def picture(p: HawCam.CamResponse) = {
    if (p.hasImageData())
      Picture(p.getImageData().toByteArray())
    else
      Picture(Array())
  }
   
}