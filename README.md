# ScZip

Simple zip archive (files compresses recursively) for Scala.

## How to use

Using Java NIO.

```scala
    // target path
    val zip = ScZip(Paths.get("./project"))
    // Pass output file path and run.
    zip.zipToFile(Paths.get("./out.zip"))
```

### Set exclude condition.
 
```scala
    val zip = ScZip(Paths.get("./project"), Exclude("**/*.{class,cache}"))

    // Can get byte array instead of saving a zip file.
    val bytes: Array[Byte] = zip.zipToFileToBytes()
```

That's all.