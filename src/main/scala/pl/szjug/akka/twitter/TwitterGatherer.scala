package pl.szjug.akka.twitter

import twitter4j.{TwitterException, Query, TwitterFactory}
import twitter4j.conf.ConfigurationBuilder
import scala.collection.JavaConversions._

class TwitterGatherer {

  val ConsumerKey = "cx1BOCDEgNat1ySgApliYvP29"
  val ConsumerSecret = "VdCe74FEFzud6DJBamv4FCUlduUcMRvtel8H42S7juE5a5hpk5"
  val AccessToken = "19394025-tKz2ZTGoYxIOxq3pnvojMvuG5pVZ0bOs4IcQvaVWE"
  val AccessSecret = "hloWbSi4tkJEn783GFIf9Qnf6v3QJYAgijN5q2G9rFCZX"

  def getManyTweets(count: Long, queryString: String, oldestId: Long, lang: String, writer: TweetsWriter): Long = {
    val cb = new ConfigurationBuilder()
    cb.setDebugEnabled(true)
      .setOAuthConsumerKey(ConsumerKey)
      .setOAuthConsumerSecret(ConsumerSecret)
      .setOAuthAccessToken(AccessToken)
      .setOAuthAccessTokenSecret(AccessSecret)
    val twitter = new TwitterFactory(cb.build()).getInstance()

    val query = new Query(queryString)
    query.setLang(lang)
    query.setCount(100)

    def getBatch(count: Long, oldestId: Long, writer: TweetsWriter): Long = {
      count match {
        case c if c > 0 => {
          query.setMaxId(oldestId)

          try {
            println(s"Querying for tweets. Oldest id is $oldestId")
            val result = twitter.search(query)
            val sResult = result.getTweets.toList.sortBy(- _.getId)

            if (sResult.size > 0) {
              writer.write(sResult)

              val newOldestId = sResult.last.getId

              getBatch(count - 100, newOldestId - 1, writer)
            } else {
              println("Request returned no tweets")
              oldestId
            }
          } catch {
            case e: Exception =>
              e.printStackTrace()
              oldestId
          }
        }
        case _ => oldestId
      }
    }

    try {
      getBatch(count, oldestId, writer)
    } finally {
      writer.close
    }
  }
}
