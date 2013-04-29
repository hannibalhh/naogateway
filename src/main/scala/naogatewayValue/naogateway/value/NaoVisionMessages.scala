package naogateway.value

import com.google.protobuf.ByteString

object NaoVisionMessages{
  
  /**
   * to define resolution of nao camera
   * Resolutions contains id you need
   * like Resolutions.k4VGA.id for a request to HAWCamServer
   */
  object Resolutions extends Enumeration {
    type Resolution = Value
    val kQQVGA, kQVGA, kVGA, k4VGA = Value
  }
  
  /**
   * to define color space of nao camera
   * ColorSpaces contains id you need
   * like ColorSpaces.kYuv.id for a request to HAWCamServer
   */  
  object ColorSpaces extends Enumeration {
    type ColorSpace = Value
    val kYuv = Value(0)
    val kYUV422 = Value(9)
    val kYUV = Value(10)
    val kRGB = Value(11)
    val kHSY = Value(12)
    val kBGR = Value(13)
  }

  /**
   * to define frames per second of nao camera
   * Frames contains id you need
   * like Frames._20.id for a request to HAWCamServer
   */ 
  object Frames extends Enumeration {
    type Frames = Value
    val _1,_2,_3,_4,_5,_6,_7,_8,_9,_10,
    _11,_12,_13,_14,_15,_16,_17,_18,_19,_20,
    _21,_22,_23,_24,_25,_26,_27,_28,_29,_30 = Value
  }
  
  /**
   * VisionCalling is trait for VisionCall and RawVisionCall
   * with resolution,colorspace and fps 
   */
  trait VisionCalling{
    val resolution:Resolutions.Value
    val colorSpaces:ColorSpaces.Value
    val fps:Frames.Value
  }
  
  /**
   * VisionCall requests a CamResponse (which contains a picture as a bytearray)
   */
  case class VisionCall(resolution:Resolutions.Value,colorSpaces:ColorSpaces.Value,fps:Frames.Value) extends VisionCalling
 
  /**
   * RawVisionCall requests a Picture (which contains a pure protobuf bytearray)
   * Thats for more speed, it have to be tested
   */
  case class RawVisionCall(resolution:Resolutions.Value,colorSpaces:ColorSpaces.Value,fps:Frames.Value) extends VisionCalling
  case class Picture(bytes:Array[Byte])
 
  /**
   * Build a CamRequest from VisionCalling
   */
  def request(c: VisionCalling) = {
    val param = HawCam.CamRequest.newBuilder.setResolution(c.resolution.id).setColorSpace(c.colorSpaces.id).setFps(c.fps.id+1)
    param.build
  }
  
  /**
   * Implicit convert of a CamResponse to  bytearray
   */
  implicit def CamResponseTOByteArray(r: Array[Byte]) = HawCam.CamResponse.parseFrom(r)
  
  /**
   * make a Picture from CamResponse
   */
  def picture(p: HawCam.CamResponse) = {
    if (p.hasImageData())
      Picture(p.getImageData().toByteArray())
    else
      Picture(Array())
  }
   
}