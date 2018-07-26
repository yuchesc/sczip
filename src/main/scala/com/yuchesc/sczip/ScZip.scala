package com.yuchesc.sczip

import java.io._
import java.nio.file._
import java.util.zip.ZipOutputStream

/**
 * Zip executor.
 * 
 * @param targetPath Target zip root path.
 * @param exclude Exclude files from target via glob match pattern.
 * @param normalizeRootPath If true, eliminate root or relative path from starting point.
 */
case class ScZip(targetPath: Path,
                 exclude: Option[Condition] = None,
                 normalizeRootPath: Boolean = true) {

  /**
   * Walk target path and collect each file path.
   *
   * @return Zip target file list.
   */
  def dryRun(): Seq[String] = {
    val visitor = new ListFileVisitor(exclude, normalizeRootPath)
    Files.walkFileTree(targetPath, visitor)
    visitor.getResult
  }

  /**
   * Zip files into out stream.
   *
   * @param out stream to write zip data.
   */
  def zipToOutputStream(out: OutputStream): Unit = {
    val zip = new ZipOutputStream(out)
    Files.walkFileTree(targetPath, new ZipFileVisitor(zip, exclude, normalizeRootPath))
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


  def main(args: Array[String]): Unit = {
    val zip = ScZip(Paths.get("./project"), Option(Exclude("**/*.{class,cache}")))

    val bytes = zip.zipToBytes()
    println(bytes.length)

    zip.zipToFile(Paths.get("./out.zip"))

    //zip.dryRun().foreach(println)
    val zip2 = ScZip(targetPath = Paths.get("./src/test/resource"),
      exclude = Option(Exclude("**/{test.a,test1.b}")),
      normalizeRootPath = true)
    zip2.dryRun().foreach(println)
  }
}