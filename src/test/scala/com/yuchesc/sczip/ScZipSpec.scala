package com.yuchesc.sczip

import org.scalatest._

class ScZipSpec extends FlatSpec with Matchers {

  "ScZip" should "run" in {
    val zip = ScZip("./src/test/resource/", "**/*.{dat,txt}")
    zip.dryRun().length should be(0)
    val zip2 = ScZip("./src/test/resource/", "**/*.dat")
    zip2.dryRun().length should be(2)
    val zip3 = ScZip("./src/test/resource/")
    zip3.dryRun().length should be(3)
  }

  "ScZip" should "run with a file." in {
    val zip = ScZip("./build.sbt")
    zip.dryRun().head should be("build.sbt")
  }
}