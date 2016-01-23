package ru.livetex.base.service

import com.twitter.finagle.http.Response
import com.twitter.util.Future
import org.jboss.netty.handler.codec.http._
import com.twitter.finagle.Service


/**
  * Базовый сервис способный возвращать значение.
  * @param value значение.
  */
class BaseService(value: String)
  extends Service[HttpRequest, HttpResponse] {

  def apply(req: HttpRequest): Future[HttpResponse] = {
    val uri = req.getUri
    val response = Response()
    response.setContentString(
      s"uri=$uri  value=$value timestamp=${System.currentTimeMillis.toString}")
    Future.value(response)
  }

}
