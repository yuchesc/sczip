package com.yuchesc.sczip

import java.io._
import java.nio.file._
import java.util.zip.ZipOutputStream

class ScZip(targetPath: Path, exclude: Option[Condition]) {

  def zipToOutputStream(out: OutputStream): Unit = {
    val zip = new ZipOutputStream(out)
    Files.walkFileTree(targetPath, new ZipFileVisitor(zip, exclude))
    zip.close()
  }

  def zipToFile(outPath: Path): Unit = {
    zipToOutputStream(new FileOutputStream(outPath.toFile))
  }

  def zipToBytes(): Array[Byte] = {
    val out = new ByteArrayOutputStream()
    zipToOutputStream(out)
    out.toByteArray
  }
}

object ScZip {

  def apply(targetPath: Path): ScZip = new ScZip(targetPath, None)

  def apply(targetPath: Path, exclude: Condition): ScZip = new ScZip(targetPath, Option(exclude))

  def main(args: Array[String]): Unit = {
    val zip = ScZip(Paths.get("./project"), Exclude("**/*.{class,cache}"))

    val bytes = zip.zipToBytes()
    println(bytes.length)

    zip.zipToFile(Paths.get("./out.zip"))
  }
}