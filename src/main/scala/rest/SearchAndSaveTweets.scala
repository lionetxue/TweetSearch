package rest

import java.time.LocalDate
import com.danielasfregola.twitter4s.TwitterRestClient
import com.danielasfregola.twitter4s.entities.Tweet
import com.danielasfregola.twitter4s.entities.enums.ResultType
import com.danielasfregola.twitter4s.entities.enums.Language
import com.danielasfregola.twitter4s.entities.GeoCode
import com.danielasfregola.twitter4s.entities.Accuracy
import com.danielasfregola.twitter4s.entities.enums.Measure
import com.typesafe.config.ConfigFactory
import utils.FileSupport

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object SearchAndSaveTweets extends App with FileSupport {

  // TODO - Make sure to define your consumer and access tokens!
  val client = TwitterRestClient()
  var apiCount = 0
  def searchTweets(query: String, max_id: Option[Long] = None): Future[Seq[Tweet]] = {
    def extractNextMaxId(params: Option[String]): Option[Long] = {
      //example: "?max_id=658200158442790911&q=%23scala&include_entities=1&result_type=mixed"
      params.getOrElse("").split("&").find(_.contains("max_id")).map(_.split("=")(1).toLong)
    }
    apiCount += 1
    //val radius = Accuracy(10, Measure.Meter) 
    //client.searchTweet(query, count = 100, result_type = ResultType.Recent, geocode = Some(GeoCode(42.964793, -75.48706, radius)), language = Some(Language.English), until= Some(ld), since_id = Some(789069905593237505L), max_id = max_id).flatMap { result =>
    //val ld = LocalDate.of(2016,11,8)
    client.searchTweet(query, count = 100, result_type = ResultType.Recent, max_id = max_id).flatMap { result =>
        val nextMaxId = extractNextMaxId(result.search_metadata.next_results)
        val tweets = result.statuses
//        Future(tweets.sortBy(_.created_at))
        if (tweets.nonEmpty && apiCount<15) searchTweets(query, nextMaxId).map(_ ++ tweets)
        else Future(tweets.sortBy(_.created_at))
      } recover { case _ => Seq.empty }
  }

  val filename = {
    val config = ConfigFactory.load()
    config.getString("tweets.scalax")
  }

  var filteredtweets : Seq[Tweet] = Seq.empty
  searchTweets("#CUZincks").map { tweets =>
    println(s"Downloaded ${tweets.size} tweets")
    for (tweet <- tweets) {
      tweet.coordinates match {
        case Some(c) =>
           println("Some coordinates")
           filteredtweets :+= tweet
        case _ => tweet.place match {
          case Some(p) =>
            println("Some place")
            filteredtweets :+= tweet
          case _ =>  None
        }
      }
    }
    println(s"Downloaded ${filteredtweets.size} tweets")
    toFileAsJson(filename, filteredtweets)
    println(s"Tweets saved to file $filename")
  }

}
