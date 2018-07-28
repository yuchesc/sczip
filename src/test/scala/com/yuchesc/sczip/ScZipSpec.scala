package com.yuchesc.sczip

import java.io.ByteArrayOutputStream
import java.nio.file.Paths

import org.scalatest._

class ScZipSpec extends FlatSpec with Matchers {

  "ScZip" should "zipTree" in {
    ScZip.zipTree(Paths.get("./src/test/resource/"), new ByteArrayOutputStream()).length should be(3)
    ScZip.zipTree(Paths.get("./src/test/resource/"), new ByteArrayOutputStream(), ScZip.makeExclude("**/*.{dat,txt}")).length should be(0)
    ScZip.zipTree(Paths.get("./src/test/resource/"), new ByteArrayOutputStream(), ScZip.makeExclude("**/*.dat")).length should be(2)
    ScZip.zipTree(Paths.get("./build.sbt"), new ByteArrayOutputStream()).head should be("build.sbt")
  }

  "ScZip" should "zipFiles" in {
    ScZip.zipFiles(Seq(Paths.get("./build.sbt")), new ByteArrayOutputStream()).head should be("build.sbt")
  }
}