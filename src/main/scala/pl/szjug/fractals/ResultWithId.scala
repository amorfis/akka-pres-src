package pl.szjug.fractals

import com.mkrcah.fractals.{Color, Point2i, Region2i}

case class ResultWithId(jobId: Long, pixels: Iterable[(Point2i, Color)], imgPart: Region2i)
