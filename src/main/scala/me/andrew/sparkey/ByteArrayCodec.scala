package me.andrew.sparkey

/** Typeclass for encoding and decoding objects as Array[Byte]. */
trait ByteArrayCodec[T] {
  def encode(obj: T): Array[Byte]
  def decode(byteArr: Array[Byte]): T
}
