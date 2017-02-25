package me.andrew.sparkey

/**
 * Default [[me.andrew.sparkey.ByteArrayCodec]] implementations for common types. To use, bring the
 * implicits into scope by importing everything in this object:
 *
 * {{{
 *   import me.andrew.sparkey.Implicits._
 * }}}
 *
 * Alternatively, you can provide your own implementation by defining a custom implicit object.
 */
object Implicits {

  implicit object IntCodec extends ByteArrayCodec[Int] {
    override def encode(obj: Int): Array[Byte] = Array(obj.toByte)
    override def decode(byteArr: Array[Byte]): Int = byteArr.head.toInt
  }

  implicit object DoubleCodec extends ByteArrayCodec[Double] {
    override def encode(obj: Double): Array[Byte] = Array(obj.toByte)
    override def decode(byteArr: Array[Byte]): Double = byteArr.head.toDouble
  }

  implicit object StringCodec extends ByteArrayCodec[String] {
    override def encode(obj: String): Array[Byte] = obj.map(_.toByte).toArray
    override def decode(byteArr: Array[Byte]): String = byteArr.map(_.toChar).mkString
  }

}
