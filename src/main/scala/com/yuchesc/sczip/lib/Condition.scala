package com.yuchesc.sczip.lib

import java.nio.file._

trait Condition {
  def hit(file: Path): Boolean
}

class PathMatchCondition(pattern: String) extends Condition {
  val matcher: PathMatcher = FileSystems.getDefault.getPathMatcher(s"glob:$pattern")

  override def hit(file: Path): Boolean = matcher.matches(file)
}
