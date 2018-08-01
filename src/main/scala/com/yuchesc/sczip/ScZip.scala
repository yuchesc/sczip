package com.yuchesc.sczip

import java.io.{BufferedInputStream, OutputStream, _}
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.{FileVisitResult, Files, Path, SimpleFileVisitor, _}
import java.util.zip.{ZipEntry, ZipOutputStream}

import com.yuchesc.sczip.lib.{Condition, _}

import scala.collection.mutable.ListBuffer

/**
  * Zip executor.
  *
  * @param out               the output stream
  * @param normalizeRootPath if true, eliminate root or relative path from starting point. (Default: true)
  * @param capacity          file reading buffer size. (Default: 4096)
  */
class ScZip(out: OutputStream,
            val normalizeRootPath: Boolean = true,
            val capacity: Int = 4096) {

  private val zip: ZipOutputStream = new ZipOutputStream(out)
  private val buffer: Array[Byte] = Array.ofDim[Byte](capacity)

  protected def getEntryName(file: Path): String = if (normalizeRootPath) {
    file.toString dropWhile ("./\\" contains _)
  } else {
    file.toString
  }

  /**
    * Add a file to zip.
    *
    * @param path Target file
    * @return added entry name
    */
  def add(path: Path): String = {
    val entryName = getEntryName(path)
    val entry = new ZipEntry(entryName)
    entry.setTime(Files.getLastModifiedTime(path).toMillis)
    zip.putNextEntry(entry)
    var in = None: Option[BufferedInputStream]
    try {
      in = Option(new BufferedInputStream(Files.newInputStream(path)))
      Stream.continually(in.get.read(buffer))
        .takeWhile(_ != -1)
        .foreach(zip.write(buffer, 0, _))
      zip.closeEntry()
      entryName
    } finally {
      in.foreach(_.close())
    }
  }

  /**
    * Add a file to zip.
    *
    * @param root    Target file path
    * @param exclude Exclude files from target via glob match pattern.
    * @return added entry name list
    */
  def addTree(root: Path, exclude: Option[Condition] = None): Seq[String] = {
    val list = ListBuffer.empty[String]
    Files.walkFileTree(root, new SimpleFileVisitor[Path] {
      override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
        if (exclude.isEmpty || !exclude.get.hit(file)) {
          list.append(add(file))
        }
        FileVisitResult.CONTINUE
      }
    })
    list
  }

  /**
    * Close zip stream.
    */
  def close(): Unit = zip.close()
}

/**
  * Make zip easily.
  */
object ScZip {

  /**
    * Use zipper object with auto close.
    *
    * @param out the output stream
    * @param f   implement
    * @tparam A return type
    * @return
    */
  def withResource[A](out: OutputStream, f: ScZip => A): A = {
    var zip: Option[ScZip] = None
    try {
      zip = Option(new ScZip(out))
      f(zip.get)
    } finally {
      zip.foreach(_.close())
    }
  }

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
    ScZip.withResource(out, { zip =>
      zip.addTree(targetPath, exclude)
    })
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
    ScZip.withResource(out, { zip =>
        files.map(zip.add)
    })
  }

  def main(args: Array[String]): Unit = {
    // Add files recursively and make zip data.
    val list: Seq[String] = ScZip.zipTreeToFile(Paths.get("./project"), Paths.get("out.zip"))
    list.foreach(println) // entry files

    // Can get zip data instead of file
    val data: Array[Byte] = ScZip.zipTreeToBytes(Paths.get("./project"))
    println(data.length)

    // Set exclude condition by glob pattern without "glob:".
    ScZip.zipTreeToFile(Paths.get("./project"), Paths.get("out.zip"), ScZip.makeExclude("**/*.{cache,class}"))
      .foreach(println)
    val data2 = ScZip.zipTreeToBytes(Paths.get("./project"), ScZip.makeExclude("**/*.{cache,class}"))
    println(data2.length)
  }
}