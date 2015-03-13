package pl.szjug.fractals

import com.mkrcah.fractals.{Palette, Region2i, Size2i}

case class JobWithId(id: Long, imgSize: Size2i, imgRegion: Region2i, palette: Palette, quality: Int) {

  def toJob = {
    Job(imgSize, imgRegion, palette, quality)
  }
}
