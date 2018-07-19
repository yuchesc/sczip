package com.yuchesc.sczip

import java.io._
import java.nio.file._
import java.util.zip.ZipOutputStream

object ScZip {

  def zipTreeToOutputStream(out: OutputStream, root: Path, condition: Condition): Unit = {
    val zip = new ZipOutputStream(out)
    Files.walkFileTree(root, new ZipFileVisitor(zip, condition))
    zip.close()
  }

  def zipTreeToFile(root: Path, outPath: Path, condition: Condition): Unit = {
    zipTreeToOutputStream(new FileOutputStream(outPath.toFile), root, condition)
  }

  def zipTreeToFileToBytes(root: Path, condition: Condition): Array[Byte] = {
    val out = new ByteArrayOutputStream()
    zipTreeToOutputStream(out, root, condition)
    out.toByteArray
  }

  def main(args: Array[String]): Unit = {
    val cond = Condition.pathMatch("**/*.{class,properties}")
    zipTreeToFile(Paths.get("./project"), Paths.get("./hoge/out.zip"), cond)

    val bytes = zipTreeToFileToBytes(Paths.get("./src"), cond)
    println(bytes.length)
  }
}