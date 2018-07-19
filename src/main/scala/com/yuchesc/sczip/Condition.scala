package com.yuchesc.sczip

import java.nio.file._

trait Condition {
  def hit(file: Path): Boolean
}

class PathMatchCondition(pattern: String) extends Condition {
  val matcher: PathMatcher = FileSystems.getDefault.getPathMatcher(s"glob:$pattern")

  override def hit(file: Path): Boolean = matcher.matches(file)
}

class Not(condition: Condition) extends Condition {
  override def hit(file: Path): Boolean = !condition.hit(file)
}

object Condition {
  def pathMatch(pattern: String) = new PathMatchCondition(pattern)
  def notPathMatch(pattern: String) = new Not(pathMatch(pattern))
}
