package pl.szjug.akka.twitter

import twitter4j.Status

trait TweetsWriter {

  def write(tweets: Seq[Status])

  def close

}
