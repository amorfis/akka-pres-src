package com.mkrcah.fractals

import java.awt.image.BufferedImage

trait DrawableImage extends BufferedImage {

    def getPixels: Iterable[Point2i] = {
        for (y <- 0 to getHeight - 1; x <- 0 to getWidth - 1)
            yield Point2i(x, y)
    }

    def draw(f: Point2i => ColorRGB) = {
        for (pixelCoords <- getPixels) {
            val color = f(pixelCoords)
            setRGB(pixelCoords.x, pixelCoords.y, color.toInt)
        }
        this
    }
}


/** Complex number **/
case class Complex(re: Double, im: Double) {
     def +(y: Complex) = Complex(re + y.re, im + y.im)
     def *(y: Complex) = Complex(re*y.re - im*y.im, im*y.re + re*y.im)
     def absSqr = re*re + im*im
 }


case class Point2i(x: Int, y:Int)

case class Size2i(width: Int, height: Int)

case class Region2c(tl: Complex, br: Complex) {
    val width = br.re - tl.re
    val height = br.im - tl.im
    val hwRatio = Math.abs(height / width)
}

case class Region2i(tl: Point2i, br: Point2i) {
    val width = br.x - tl.x
    val height = br.y - tl.y

  if (br.x <= tl.x || br.y <= tl.y) {
    throw new RuntimeException(s"Trying to create Invalid Region2i: $tl, $br")
  }

  def this(size: Size2i) = this(Point2i(0, 0), Point2i(size.width - 1, size.height - 1))
}

object Region2i {

  def apply(size:Size2i) = new Region2i(size)
}



