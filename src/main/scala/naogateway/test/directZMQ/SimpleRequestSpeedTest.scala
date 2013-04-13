package test.directZMQ

object SimpleRequestSpeedTest extends App{

  import test.directZMQ._
  
  val r = new SimpleRequest()
  littleTimeTest
  
  def littleTimeTest: Unit = {
    littleTimeTest(10, print = false) // heat JVM
    littleTimeTest(1)
    littleTimeTest(10)
    littleTimeTest(100)
    littleTimeTest(1000)
    littleTimeTest(10000)
    littleTimeTest(100000)
    littleTimeTest(1000000)
  }

  def littleTimeTest(f: Int,n:Int=30,print: Boolean = true) = {
    val t0 = System.currentTimeMillis()
    val s = "Synchron"
    for (i <- 0 to n)
      r.say((s + i) * f)
    val tEnd = System.currentTimeMillis() - t0
    if (print)
      println("SimpleRequest SpeedTest: average " + tEnd / n + "ms of " + n + "times " + f * (s.size + 1) + " charakters")
  }
  
}