package com.yuchesc.sczip

import java.io._
import java.nio.file._
import java.nio.file.attribute.BasicFileAttributes
import java.util.zip.{ZipEntry, ZipOutputStream}

protected class ZipFileVisitor(zip: ZipOutputStream, exclude: Option[Condition]) extends SimpleFileVisitor[Path] {
  override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
    if (exclude.isEmpty || !exclude.get.hit(file)) {
      val entry = new ZipEntry(file.toString)
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
