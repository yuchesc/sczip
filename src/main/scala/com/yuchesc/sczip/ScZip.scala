package com.yuchesc.sczip

import java.io._
import java.nio.file._

import com.yuchesc.sczip.lib._

/**
  * Make zip easily.
  */
object ScZip {

  /**
    * Make exclude object covered with Option.
    *
    * @param pattern glob match pattern
    * @return exclude object covered with Option
    */
  def makeExclude(pattern: String): Option[Condition] = Option(new PathMatchCondition(pattern))

  /**
    * Add files recursively and make zip data.
    *
    * @param targetPath target zip root path
    * @param exclude    exclude object
    * @return zip data
    */
  def zipTreeToBytes(targetPath: Path, exclude: Option[Condition] = None): Array[Byte] = {
    val out = new ByteArrayOutputStream()
    zipTree(targetPath, out, exclude)
    out.toByteArray
  }

  /**
    * Add files recursively and make and write zip to file.
    *
    * @param targetPath target zip root path
    * @param outPath    output path
    * @param exclude    exclude object
    * @return entry name list
    */
  def zipTreeToFile(targetPath: Path, outPath: Path, exclude: Option[Condition] = None): Seq[String] =
    zipTree(targetPath, new BufferedOutputStream(new FileOutputStream(outPath.toFile)), exclude)

  /**
    * Add files recursively and make and write zip to out.
    *
    * @param targetPath Target zip root path
    * @param out        output stream to write zip data
    * @param exclude    exclude object
    * @return entry name list
    */
  def zipTree(targetPath: Path, out: OutputStream, exclude: Option[Condition] = None): Seq[String] = {
    var zip: Option[Zipper] = None
    try {
      zip = Option(new Zipper(out))
      zip.get.addTree(targetPath, exclude)
    } finally {
      zip.foreach(_.close())
    }
  }


  /**
    * Make zip data.
    *
    * @param files zip target files
    * @return zip data
    */
  def zipFilesToBytes(files: Seq[Path]): Array[Byte] = {
    val out = new ByteArrayOutputStream()
    zipFiles(files, out)
    out.toByteArray
  }

  /**
    * Make and write zip to file.
    *
    * @param files   zip target files
    * @param outPath output path
    * @return entry name list
    */
  def zipFilesToFile(files: Seq[Path], outPath: Path): Seq[String] = {
    zipFiles(files, new BufferedOutputStream(new FileOutputStream(outPath.toFile)))
  }

  /**
    * Make and write zip to out.
    *
    * @param files zip target files
    * @param out   output stream to write zip data
    * @return entry name list
    */
  def zipFiles(files: Seq[Path], out: OutputStream): Seq[String] = {
    var zip: Option[Zipper] = None
    try {
      zip = Option(new Zipper(out))
      files.map(zip.get.add)
    } finally {
      zip.foreach(_.close())
    }
  }

  def main(args: Array[String]): Unit = {
    // Add files recursively and make zip data.
    ScZip.zipTreeToFile(Paths.get("./project"), Paths.get("out.zip"))
      .foreach(println) // print entry files

    // Can get zip data instead of file
    val data = ScZip.zipTreeToBytes(Paths.get("./project"))
    println(data.length)

    // Set exclude condition by glob pattern without "glob:".
    ScZip.zipTreeToFile(Paths.get("./project"), Paths.get("out.zip"), ScZip.makeExclude("**/*.{cache,class}"))
      .foreach(println)
    val data2 = ScZip.zipTreeToBytes(Paths.get("./project"), ScZip.makeExclude("**/*.{cache,class}"))
    println(data2.length)
  }
}