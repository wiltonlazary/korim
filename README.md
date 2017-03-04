## Korim: Kotlin cORoutines IMaging utilities depending on Korio for JVM, Kotlin-JS, Android, Jtransc+Node.JS and Jtransc+Browser

[![Build Status](https://travis-ci.org/soywiz/korim.svg?branch=master)](https://travis-ci.org/soywiz/korim)
[![Maven Version](https://img.shields.io/github/tag/soywiz/korim.svg?style=flat&label=maven)](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22korim%22)

![](https://raw.githubusercontent.com/soywiz/kor/master/logos/128/korim.png)

[All KOR libraries](https://github.com/soywiz/kor)

Use with gradle:

```
compile "com.soywiz:korim:$korVersion"
```

### Bitmap classes

Bitmap base class + Bitmap8 and Bitmap32

### Image Formats

Korim provides utilities for reading and writing some image formats without any kind of additional dependency.

PNG, JPG, TGA, BMP, ICO and PSD.

### Color Formats

Korim provides color formats to convert easily and fast.

### Vectorial Image Formats

Korim supports loading, rasterizing and drawing vector SVG files.

### Native vectorial rendering

It provides a single interface for vector rendering.
So you can use a single interface and leverage JavaScript Canvas,
AWT's Graphics2D and Android Canvas.

### AWT Utilities

Korim provides AWT utilities to convert bitmaps into AWT BufferedImages, and to display them.
These are just extensions so not referenced from the main code.

### Native Fonts

Korim provides native font rendering. You can rasterize glyph fonts on all targets.

### Korio integration

Korim provides korio integration adding `VfsFile.readBitmap()` that allows Bitmap reading easily
and faster (with native implementations) in some targets like browsers.
