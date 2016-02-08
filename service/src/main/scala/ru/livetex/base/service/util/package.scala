package ru.livetex.base.service

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.implicitConversions

import com.twitter.util.Promise


package object util {
  /**
    * Convert scala future to twitter future
    * @param future scala future
    * @tparam T type of future
    * @return
    */
  implicit def futureToTwitterFuture[T](future: scala.concurrent.Future[T]
                                       ): com.twitter.util.Future[T] =
  {
    val promise = Promise[T]()

    future.onSuccess {
      case result => promise.setValue(result)
    }

    future.onFailure {
      case error =>
        promise.setException(error)
    }
    promise
  }
}
