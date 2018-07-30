package com.yuchesc.sczip

import java.io.{BufferedInputStream, OutputStream}
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.{FileVisitResult, Files, Path, SimpleFileVisitor}
import java.util.zip.{ZipEntry, ZipOutputStream}

import com.yuchesc.sczip.lib.Condition

import scala.collection.mutable.ListBuffer

/**
  * Zip executor.
  *
  * @param out               the output stream
  * @param normalizeRootPath if true, eliminate root or relative path from starting point. (Default: true)
  * @param capacity          file reading buffer size. (Default: 4096)
  */
class Zipper(out: OutputStream,
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

object Zipper {
  /**
    * Use zipper object with auto close.
    *
    * @param out the output stream
    * @param f   implement
    * @tparam A return type
    * @return
    */
  def withResource[A](out: OutputStream, f: Zipper => A): A = {
    var zip: Option[Zipper] = None
    try {
      zip = Option(new Zipper(out))
      f(zip.get)
    } finally {
      zip.foreach(_.close())
    }
  }
}