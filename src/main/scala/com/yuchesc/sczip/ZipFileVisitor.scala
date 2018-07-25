package com.yuchesc.sczip

import java.io._
import java.nio.file._
import java.nio.file.attribute.BasicFileAttributes
import java.util.zip.{ZipEntry, ZipOutputStream}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

trait Visitor extends SimpleFileVisitor[Path] {
  val normalizeRootPath: Boolean

  def getFileName(file: Path): String = if (normalizeRootPath) {
    file.toString dropWhile ("./\\" contains _)
  } else {
    file.toString
  }
}

class ZipFileVisitor(zip: ZipOutputStream, exclude: Option[Condition], val normalizeRootPath: Boolean) extends Visitor {
  override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
    if (exclude.isEmpty || !exclude.get.hit(file)) {
      val entry = new ZipEntry(getFileName(file))
      zip.putNextEntry(entry)
      val buffer = Array.ofDim[Byte](255)
      var in = None: Option[BufferedInputStream]
      try {
        in = Option(new BufferedInputStream(Files.newInputStream(file)))
        Stream.continually(in.get.read(buffer))
          .takeWhile(_ != -1)
          .foreach(zip.write(buffer, 0, _))
        zip.closeEntry()
      } finally {
        in.foreach(_.close())
      }
    }
    FileVisitResult.CONTINUE
  }
}

class ListFileVisitor(exclude: Option[Condition], val normalizeRootPath: Boolean) extends Visitor {

  private val targetFiles: mutable.ListBuffer[String] = ListBuffer.empty[String]

  def getResult: Seq[String] = targetFiles

  override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
    if (exclude.isEmpty || !exclude.get.hit(file)) {
      targetFiles.append(getFileName(file))
    }
    FileVisitResult.CONTINUE
  }
}
