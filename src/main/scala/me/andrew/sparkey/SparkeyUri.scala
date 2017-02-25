package me.andrew.sparkey

import java.io.File

import com.spotify.sparkey.{Sparkey, SparkeyReader, SparkeyWriter}

/** Abstraction representing a pair of Sparkey files. */
trait SparkeyUri {

  /**
   * The base path of the Sparkey files, eg. '/path/to/sparkey' for the pair of files
   * '/path/to/sparkey.spi' and '/path/to/sparkey.spl'
   */
  val basePath: String

  /** Delete both files associated with this URI. */
  def delete(): Unit

  /**
   * Whether or not the data exists. It should only be considered false if neither the log
   * or index file exist.
   */
  def exists: Boolean

  /** Get a [[SparkeyReader]] for the URI. */
  def getReader: SparkeyReader

  /** Get a [[SparkeyWriter]] for the URI. */
  def getWriter: SparkeyWriter

  final def index: String = basePath + ".spi"
  final def log: String = basePath + ".spl"

  override def toString: String = basePath
}

/** Convenience method for creating LocalSparkeyUri. */
object LocalSparkeyUri {
  def apply(basePath: String): SparkeyUri = new LocalSparkeyUri(basePath)
}

/** Local file system implementation of [[SparkeyUri]]. */
private class LocalSparkeyUri(val basePath: String) extends SparkeyUri {
  override def delete(): Unit = {
    new File(index).delete()
    new File(log).delete()
  }
  override def getReader: SparkeyReader = {
    require(exists, s"URI $basePath does not exist.")
    Sparkey.open(new File(basePath))
  }
  override def getWriter: SparkeyWriter = Sparkey.createNew(new File(basePath))
  override def exists: Boolean = new File(index).exists || new File(log).exists
}
