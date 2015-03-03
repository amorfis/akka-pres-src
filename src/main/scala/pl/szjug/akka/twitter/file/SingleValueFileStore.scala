package pl.szjug.akka.twitter.file

import java.io.{FileReader, Reader, PrintWriter, File}
import java.nio.charset.StandardCharsets
import java.nio.file.{Path, Paths, Files}

class SingleValueFileStore(val path: Path) {

  def store(value: Long) = {
    Files.write(path, value.toString.getBytes(StandardCharsets.UTF_8))
  }

  def load() = {
    Files.readAllBytes(path) match {
      case a if a.length > 0 => Some(new String(a, StandardCharsets.UTF_8).toLong)
      case _ => None
    }
  }

}
