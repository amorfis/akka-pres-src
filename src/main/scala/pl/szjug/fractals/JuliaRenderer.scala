package pl.szjug.fractals

import com.mkrcah.fractals._
import com.typesafe.scalalogging.LazyLogging

class JuliaRenderer(imageSize: Size2i = Size2i(1000, 1000), palette: Palette, quality: Int = 300, partToRender: Region2i) extends LazyLogging {

  def this(job: Job) = this(job.imgSize, job.palette, 300, job.imgRegion)

//  val region = Region2c(Complex(-2, 1.2), Complex(2, -1.2))
  val region = Region2c(Complex(-1, 0.7), Complex(1, -0.7))
  assertWholeRegionInPicture(imageSize, partToRender)

  private def assertWholeRegionInPicture(picture: Size2i, region: Region2i): Unit = {
    if (region.tl.x < 0
      || region.tl.y < 0
      || region.br.x > imageSize.width - 1
      || region.br.y > imageSize.height - 1) {
      throw new RuntimeException(s"Won't render. Not whole region in picture. Region: $region, picture: $picture")
    }
  }

  private def imgPointToComplex(pixel: Point2i) = Complex(
    pixel.x.toFloat / imageSize.width * region.width + region.tl.re,
    pixel.y.toFloat / imageSize.height * region.height + region.tl.im
  )

  private def getPixels: Iterable[Point2i] = {
    for (
      y <- partToRender.tl.y to partToRender.br.y;
      x <- partToRender.tl.x to partToRender.br.x)
    yield Point2i(x, y)
  }

  def render(): Result = {
    val j = new Julia(Complex(-0.835, -0.2321), quality)
    val pixels = for(pixel <- getPixels) yield {
      val escapeTime = j.getEscapeTimeFor(imgPointToComplex(pixel))
      val colorIdx =
        if (escapeTime < quality)
          (escapeTime.toFloat / quality * palette.maxIndex).toInt
        else 0
      (pixel, palette.get(colorIdx))
    }

    Result(pixels)
  }
}
