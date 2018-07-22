# ScZip

Simple zip archive (files compresses recursively) library for Scala.

## How to use

Using Java NIO.

```scala
    // target path
    val zip = ScZip(Paths.get("./project"))
    // Pass an output file path and run.
    zip.zipToFile(Paths.get("./out.zip"))
```

### Set exclude condition.

Can set exclude pattern by glob without "glob:".

https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob

```scala
    val zip = ScZip(Paths.get("./project"), Exclude("**/*.{class,cache}"))

    // Can get byte array instead of saving a zip file.
    val bytes: Array[Byte] = zip.zipToBytes()
```

That's all.

## SBT

It is available for 2.11 or 2.12.

Please append it in your libraryDependencies :)

```scala
libraryDependencies ++= Seq(
  "com.yuchesc" %% "sczip" % "0.8"
)
```

---

This software is released under the MIT License, see LICENSE.txt.