package test.directZMQ

import test.directZMQ._

/**
 *  SimpleReqTest is a runnable SimpleReq starter, see SimpleReq
 */
object SimpleReqTest extends App {
  
  val nila =  "tcp://192.168.1.148:5555"
  val hanna = "tcp://192.168.1.121:5555"
  val local = "tcp://127.0.0.1:5555"
    
  new SimpleReq(local).com
}

  
