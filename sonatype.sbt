import xerial.sbt.Sonatype._
sonatypeProjectHosting := Some(GitHubHosting("yuchesc", "sczip", "yuchesc@gmail.com"))

// workaround for avoiding gpg exceptions...
updateOptions := updateOptions.value.withGigahorse(false)

publishMavenStyle := true
publishArtifact in Test := false
publishTo := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)
licenses := Seq("MIT" -> url("http://www.opensource.org/licenses/mit-license.php"))
homepage := Some(url("https://github.com/yuchesc/sczip"))

scmInfo := Some(
  ScmInfo(
    url("https://github.com/yuchesc/sczip"),
    "scm:git@github.com:yuchesc/sczip.git"
  )
)

developers := List(
  Developer(
    id    = "Yuchesc",
    name  = "Yuichiro Hoshi",
    email = "yuchesc@gmail.com",
    url   = url("http://yuchesc.com")
  )
)