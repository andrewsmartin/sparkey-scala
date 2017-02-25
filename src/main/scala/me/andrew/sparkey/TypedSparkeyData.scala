package me.andrew.sparkey

import scala.collection.JavaConverters._

import com.spotify.sparkey.{SparkeyReader, SparkeyWriter}

/** Convenience method for creating TypedSparkeyData. */
object TypedSparkeyData {
  def apply[K: ByteArrayCodec, V: ByteArrayCodec](uri: SparkeyUri) =
    new TypedSparkeyData[K, V](uri)
}

/** Abstraction for a typed set of Sparkey data. */
class TypedSparkeyData[K: ByteArrayCodec, V: ByteArrayCodec](val uri: SparkeyUri) {

  /** Get a [[TypedSparkeyReader]] for the URI. */
  def getReader: TypedSparkeyReader[K, V] = new TypedSparkeyReader(uri.getReader)

  /** Get a [[TypedSparkeyWriter]] for the URI. */
  def getWriter: TypedSparkeyWriter[K, V] = new TypedSparkeyWriter(uri.getWriter)
}

/** Typed wrapper around a SparkeyReader. */
class TypedSparkeyReader[K, V](val wrapped: SparkeyReader)
                              (implicit ev1: ByteArrayCodec[K], ev2: ByteArrayCodec[V]) {

  /**
   * Get the value associated with the given key as an [[Option]], which will be [[None]] if the
   * key does not exist.
   */
  def apply(key: K): Option[V] =
    Option(wrapped.getAsByteArray(ev1.encode(key))).map(ev2.decode)

  /** Get an [[Iterator]] of all the key/value pairs in the file. */
  def iterator: Iterator[(K, V)] =
    wrapped.iterator.asScala.map(kv => (ev1.decode(kv.getKey), ev2.decode(kv.getValue)))

  /** Close the reader. */
  def close(): Unit = wrapped.close()
}

/** Typed wrapper around SparkeyWriter. */
class TypedSparkeyWriter[K, V](val wrapped: SparkeyWriter)
                              (implicit ev1: ByteArrayCodec[K], ev2: ByteArrayCodec[V]) {

  /** Deletes the key from the writer. */
  def delete(key: K): Unit = wrapped.delete(ev1.encode(key))

  /** Append the key, value pair to the writer. */
  def put(key: K, value: V): Unit = wrapped.put(ev1.encode(key), ev2.encode(value))

  /** Append all key, value pairs in the sequence to the writer. */
  def putAll(items: Seq[(K, V)]): Unit = {
    val it = items.iterator
    while (it.hasNext) {
      val kv = it.next
      put(kv._1, kv._2)
    }
  }

  /** Flush all pending writes to the file, write the index and close the writer. */
  def close(): Unit = {
    wrapped.flush()
    wrapped.writeHash()
    wrapped.close()
  }
}
