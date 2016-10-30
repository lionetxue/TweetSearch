package stream

import com.danielasfregola.twitter4s.TwitterStreamingClient
import com.danielasfregola.twitter4s.entities.Tweet
import com.danielasfregola.twitter4s.entities.streaming.StreamingMessage
import com.danielasfregola.twitter4s.entities.enums.ResultType
import com.typesafe.config.ConfigFactory
import utils.FileSupport

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object PrintStream extends App with FileSupport {

	val client = TwitterStreamingClient()

/*
        def printTweetText: PartialFunction[StreamingMessage, Unit] = {
	    case tweet: Tweet => println(tweet.text)
	  }
	client.getStatusesSample(stall_warnings = true)(printTweetText)
        */

	val filename = {
	    val config = ConfigFactory.load()
	    config.getString("tweets.scalax")
	  }

	def printTweetText: PartialFunction[StreamingMessage, Unit] = {
	    case tweet: Tweet => toFileAsJson(filename, tweet)
	  }

	client.getStatusesSample(stall_warnings = true)(printTweetText)

}
