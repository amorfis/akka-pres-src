package pl.szjug.fractals

import com.mkrcah.fractals.{Region2i, Size2i, Point2i, Color}

case class Result(pixels: Iterable[(Point2i, Color)], imgSize: Size2i, imgPart: Region2i, renderQuality: Int)
