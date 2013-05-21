package naogateway.value


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
   * VisionCall requests a CamResponse (which contains a picture as a bytearray)
   * with resolution,colorspace and fps 
   */
  case class VisionCall(resolution:Resolutions.Value,colorSpaces:ColorSpaces.Value,fps:Frames.Value)
 
  /**
   * Build a CamRequest from VisionCalling
   */
  def request(c: VisionCall) = {
    val param = HAWCamserverMessages.CamRequest.newBuilder.setResolution(c.resolution.id).setColorSpace(c.colorSpaces.id).setFps(c.fps.id+1)
    param.build
  }
  
  /**
   *  convert of a CamResponse to  CamResponse
   */
   def picture(r: Array[Byte]) = HAWCamserverMessages.CamResponse.parseFrom(r)
  
   
}