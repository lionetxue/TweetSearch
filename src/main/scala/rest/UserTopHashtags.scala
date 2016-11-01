package rest

import com.danielasfregola.twitter4s.TwitterRestClient
import com.danielasfregola.twitter4s.entities.{HashTag, Tweet}

import scala.concurrent.ExecutionContext.Implicits.global

object UserTopHashtags extends App {


  def getTopHashtags(tweets: Seq[Tweet], n: Int = 10): Seq[(String, Int)] = {
    val hashtags: Seq[Seq[HashTag]] = tweets.map { tweet =>
      tweet.entities.map(_.hashtags).getOrElse(Seq.empty)
    }
    val hashtagTexts: Seq[String] = hashtags.flatten.map(_.text.toLowerCase)
    val hashtagFrequencies: Map[String, Int] = hashtagTexts.groupBy(identity).mapValues(_.size)
    hashtagFrequencies.toSeq.sortBy { case (entity, frequency) => -frequency }.take(n)
  }

  def topRetweets(tweets: Seq[Tweet], n: Int = 10) = {
    val result = tweets.filterNot(_.text.startsWith("RT ")).sortBy(t => -t.retweet_count).distinct.take(n)
    toPrettyString("TOP RETWEETS", result) { tweet =>
      s"${tweet.text.replaceAll("\n"," ")} (by @${tweet.user.map(_.screen_name).getOrElse("unknown")}, retweeted ${tweet.retweet_count} times)"
    }
  }

  def topFavoriteTweets(tweets: Seq[Tweet], n: Int = 10) = {
    val result = tweets.sortBy(t => - t.favorite_count).distinct.take(n)
    toPrettyString("TOP FAVORITE TWEETS", result) { tweet =>
      s"${tweet.text.replaceAll("\n"," ")} (by @${tweet.user.map(_.screen_name).getOrElse("unknown")}, liked ${tweet.favorite_count} times)"
    }
  }

  def toPrettyString[T](title: String, data: Seq[T])(f: T => String): String = {
    s"$title\n" +
    data.zipWithIndex.map {case (x, idx) => s"[${idx+1}] ${f(x)}\n"}.mkString
  }

  // TODO - Make sure to define your consumer and access tokens!
  val client = TwitterRestClient()

  val user = "CornellAlumni"

  client.getUserTimelineForUser(screen_name = user, count = 200).map { tweets =>
    val topHashtags: Seq[((String, Int), Int)] = getTopHashtags(tweets).zipWithIndex
    val rankings = topHashtags.map { case ((entity, frequency), idx) => s"[${idx + 1}] $entity (found $frequency times)"}
    println(s"${user.toUpperCase}'S TOP HASHTAGS:")
    println(rankings.mkString("\n"))
  	println(topRetweets(tweets))
  	println(topFavoriteTweets(tweets))
  }
}

