package pl.szjug.util

import java.net.NetworkInterface
import scala.collection.convert.WrapAsScala.enumerationAsScalaIterator

trait NetworkUtil {

  def getIpAddress() = {
    val n = NetworkInterface.getNetworkInterfaces
    val sc = enumerationAsScalaIterator(n)
    val addresses = for (
      i <- sc;
      ad <- enumerationAsScalaIterator(i.getInetAddresses) if ad.isSiteLocalAddress
    ) yield ad

    addresses.next().getHostAddress
  }
}
