# ScZip

Simple zip archive (files compresses recursively) library for Scala.

## How to use

Using Java NIO.

```scala
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
```

That's all.

## SBT

It is available for 2.11 or 2.12.

Please append it in your libraryDependencies :)

```scala
libraryDependencies ++= Seq(
  "com.yuchesc" %% "sczip" % "1.0.0"
)
```

---

This software is released under the MIT License, see LICENSE.txt.