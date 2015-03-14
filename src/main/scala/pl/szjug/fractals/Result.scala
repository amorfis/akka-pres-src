package pl.szjug.fractals

import com.mkrcah.fractals.{Region2i, Point2i, Color}

case class Result(pixels: Iterable[(Point2i, Color)], imgPart: Region2i)
