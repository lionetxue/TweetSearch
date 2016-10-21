# twitter search
Build a simple search rest api client using [twitter4s](https://github.com/DanielaSfregola/twitter4s) library.

Usage
-----
Make sure to set up your consumer and access tokens -- see [twitter4s README - Usage Section](https://github.com/DanielaSfregola/twitter4s#usage) for more information.

Run the examples with `sbt run` and choose the main to run.

- [SearchAndSaveTweets](https://github.com/DanielaSfregola/twitter4s-demo/blob/master/src/main/scala/rest/SearchAndSaveTweets.scala) searches all the tweets matching some query and it saves the result as JSON in a file. In this example, the query used is `#scalax` and the generated file is `src/main/resources/tweets/scalax.json`.

