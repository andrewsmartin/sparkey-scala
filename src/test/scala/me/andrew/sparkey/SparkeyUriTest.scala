package me.andrew.sparkey

import java.io.File

import com.google.common.io.Files
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

class SparkeyUriTest extends FlatSpec with Matchers with BeforeAndAfterEach {
  private val tmpDir = Files.createTempDir()
  private val sparkeyUri = LocalSparkeyUri(tmpDir.toString + "/sparkey")

  override def beforeEach(): Unit = sparkeyUri.delete()
  override def afterEach(): Unit = sparkeyUri.delete()

  "LocalSparkeyUri" should "support .delete" in {
    Files.touch(new File(sparkeyUri.index))
    Files.touch(new File(sparkeyUri.log))
    sparkeyUri.delete()
    new File(sparkeyUri.index).exists shouldBe false
    new File(sparkeyUri.log).exists shouldBe false
  }

  it should "allow .delete when URI does not exist" in {
    sparkeyUri.delete()
    sparkeyUri.delete()
  }

  it should "support .exists" in {
    Files.touch(new File(sparkeyUri.index))
    sparkeyUri.exists shouldBe true
    sparkeyUri.delete()
    Files.touch(new File(sparkeyUri.log))
    sparkeyUri.exists shouldBe true
  }

  it should "fail getting reader for non-existent URI" in {
    the[IllegalArgumentException] thrownBy {
      sparkeyUri.getReader
    } should have message s"requirement failed: URI ${sparkeyUri.basePath} does not exist."
  }

}
