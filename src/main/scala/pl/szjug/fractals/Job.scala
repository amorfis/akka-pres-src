package pl.szjug.fractals

import com.mkrcah.fractals.{Region2i, Palette, Size2i}

case class Job(imgSize: Size2i, imgRegion: Region2i, palette: Palette, quality: Int)
