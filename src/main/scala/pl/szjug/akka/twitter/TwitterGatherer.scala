package pl.szjug.akka.twitter

import twitter4j.{TwitterException, Query, TwitterFactory}
import twitter4j.conf.ConfigurationBuilder
import scala.collection.JavaConversions._

class TwitterGatherer {

  val ConsumerKey = "cx1BOCDEgNat1ySgApliYvP29"
  val ConsumerSecret = "VdCe74FEFzud6DJBamv4FCUlduUcMRvtel8H42S7juE5a5hpk5"
  val AccessToken = "19394025-tKz2ZTGoYxIOxq3pnvojMvuG5pVZ0bOs4IcQvaVWE"
  val AccessSecret = "hloWbSi4tkJEn783GFIf9Qnf6v3QJYAgijN5q2G9rFCZX"

  def getManyTweets(count: Long, queryString: String, lang: String, writer: TweetsWriter) = {
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

    def getBatch(count: Long, minId: Long, writer: TweetsWriter): Unit = {
      count match {
        case c if c > 0 => {
          query.setMaxId(minId)

          val result = twitter.search(query)
          val sResult = result.getTweets.toList

          writer.write(sResult)

          val newMinId = sResult.foldLeft(Long.MaxValue)((min, tweet) => tweet.getId match {
            case id if id < min => id
            case _ => min
          })
          getBatch(count - 100, newMinId - 1, writer)
        }
        case _ =>
      }
    }

    try {
      getBatch(count, Long.MaxValue, writer)
    } finally {
      writer.close
    }
  }
}
