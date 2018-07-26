package com.yuchesc.sczip

import java.io._
import java.nio.file._
import java.util.zip.ZipOutputStream

/**
  * Zip executor.
  *
  * @param targetPath        Target zip root path.
  * @param exclude           Exclude files from target via glob match pattern.
  * @param normalizeRootPath If true, eliminate root or relative path from starting point.
  */
class ScZip(targetPath: Path,
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


  def zipToFile(outPathName: String): Unit = zipToFile(Paths.get(outPathName))

  def zipToFile(outPath: Path): Unit = {
    zipToOutputStream(new FileOutputStream(outPath.toFile))
  }

  def zipToBytes(): Array[Byte] = {
    val out = new ByteArrayOutputStream()
    zipToOutputStream(out)
    out.toByteArray
  }
}

/**
  * ScZip object creator.
  */
object ScZip {

  /**
    * The simplest way to create an object.
    *
    * @param targetPathName Target zip root path string.
    * @return object
    */
  def apply(targetPathName: String): ScZip = apply(Paths.get(targetPathName))

  /**
    * Make sczip.
    *
    * @param targetPathName Target zip root path string.
    * @param excludePattern Exclude glob match pattern.
    * @return object
    */
  def apply(targetPathName: String, excludePattern: String): ScZip = apply(Paths.get(targetPathName), Exclude(excludePattern))

  /**
    * Make sczip.
    *
    * @param targetPath Target zip root path.
    * @return object
    */
  def apply(targetPath: Path): ScZip = new ScZip(targetPath, None)

  /**
    * Make sczip.
    *
    * @param targetPath Target zip root path.
    * @param exclude    Exclude files from target via glob match pattern.
    */
  def apply(targetPath: Path, exclude: Condition): ScZip = new ScZip(targetPath, Option(exclude))


  def main(args: Array[String]): Unit = {
    val zip = ScZip("./project", "**/*.{class,cache}")

    val bytes = zip.zipToBytes()
    println(bytes.length)

    zip.zipToFile("./out.zip")

    //zip.dryRun().foreach(println)
    val zip2 = new ScZip(targetPath = Paths.get("./src/test/resource"),
      exclude = Option(Exclude("**/{test.a,test1.b}")),
      normalizeRootPath = false)
    zip2.dryRun().foreach(println)
  }
}