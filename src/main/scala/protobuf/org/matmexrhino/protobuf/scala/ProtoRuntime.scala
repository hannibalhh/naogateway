package org.matmexrhino.protobuf.scala

class Opt[B, T] (f : (B, T) => Unit) {
  def apply(opt : T) : B => Unit = f(_, opt)
}

class SeqOpt[B, T] (f : (B, T) => Unit) {
  def apply(opts : T*) : B => Unit = { b => opts.foreach { f(b, _) } }
}

abstract class MessageObject[B] {
  def apply(instance : B)(opts : (B => Unit)*) : B = {
    impl(clone(instance))(opts : _*)
  }
  def apply(opts : (B => Unit)*) : B = impl(newInstance)(opts : _*)

  def newInstance : B
  def clone(b : B) : B

  private def impl(instance : B)(opts : (B => Unit)*) : B = {
    for (opt <- opts) opt(instance)
    instance
  }
}

class MessageOpt[B, BI] (f : (B, BI) => Unit)(m : MessageObject[BI]) {
  def apply(opts : (BI => Unit)*) : B => Unit =  f(_, m(opts : _*))

  def apply(instance : BI)(opts : (BI => Unit)*) : B => Unit = {
    f(_, m(instance)(opts : _*))
  }
}