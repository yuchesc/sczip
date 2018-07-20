package com.yuchesc.sczip

import java.nio.file._

trait Condition {
  def hit(file: Path): Boolean
}

class PathMatchCondition(pattern: String) extends Condition {
  val matcher: PathMatcher = FileSystems.getDefault.getPathMatcher(s"glob:$pattern")

  override def hit(file: Path): Boolean = matcher.matches(file)
}

object Exclude {
  def apply(pattern: String): Condition = new PathMatchCondition(pattern)
}
