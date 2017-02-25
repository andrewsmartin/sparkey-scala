package me.andrew.sparkey

import com.google.common.io.Files
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

object SerDe {
  import Implicits._

  case class Key(f: Int)
  case class Val(f: String)

  implicit object KeyCodec extends ByteArrayCodec[Key] {
    override def encode(obj: Key): Array[Byte] =
      implicitly[ByteArrayCodec[Int]].encode(obj.f)
    override def decode(byteArr: Array[Byte]): Key =
      Key(implicitly[ByteArrayCodec[Int]].decode(byteArr))
  }

  implicit object ValueCodec extends ByteArrayCodec[Val] {
    override def encode(obj: Val): Array[Byte] =
      implicitly[ByteArrayCodec[String]].encode(obj.f)
    override def decode(byteArr: Array[Byte]): Val =
      Val(implicitly[ByteArrayCodec[String]].decode(byteArr))
  }
}

class TypedSparkeyTest extends FlatSpec with Matchers with BeforeAndAfterEach {
  override def beforeEach(): Unit = sparkeyUri.delete()
  override def afterEach(): Unit = sparkeyUri.delete()

  private val tmpDir = Files.createTempDir()
  private val sparkeyUri = LocalSparkeyUri(tmpDir.toString + "/sparkey")

  "Typed Sparkey API" should "round trip primitive types" in {
    import Implicits._
    val data = Seq((1.0, 1), (2.0, 2))
    val t = new TypedSparkeyData[Double, Int](sparkeyUri)
    val writer = t.getWriter
    writer.putAll(data)
    writer.close()
    val reader = t.getReader
    reader(1.0) shouldBe Some(1)
    reader(2.0) shouldBe Some(2)
    val in = reader.iterator.toList
    in should contain allElementsOf(data)
  }

  it should "round trip custom objects" in {
    import SerDe._
    val data = Seq((Key(1), Val("one")), (Key(2), Val("two")))
    val t = new TypedSparkeyData[Key, Val](sparkeyUri)
    val writer = t.getWriter
    writer.putAll(data)
    writer.close()
    val reader = t.getReader
    reader(Key(1)) shouldBe Some(Val("one"))
    reader(Key(2)) shouldBe Some(Val("two"))
    val in = reader.iterator.toList
    in should contain allElementsOf(data)
  }
}
