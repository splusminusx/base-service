package ru.livetex.base.service.endpoint

import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.http.{ Http, Request, RequestBuilder, Response }
import com.twitter.finagle.{ Filter, Service, SimpleFilter }
import com.twitter.io.Buf
import com.twitter.util.{ Future, StorageUnit }
import org.jboss.netty.handler.codec.http.HttpHeaders

/**
  * Client Settings
  */
object HTTPClient {
  val CONNECTION_LIMIT = 100
  val MAX_RESPONSE_SIZE = 100 * 1024 * 1024
  val RETRIES = 2
}

/**
  * Client
  *
  * @param endpoint Service worker endpoint
  * @param filter Http filter
  */
class HTTPClient(endpoint: Endpoint,
                 filter: SimpleFilter[Request, Response] = Filter.identity,
                 connectionLimit: Int = HTTPClient.CONNECTION_LIMIT,
                 maxResponseSize: Int = HTTPClient.MAX_RESPONSE_SIZE,
                 retries: Int = HTTPClient.RETRIES,
                 useSsl: Boolean = false) {
  type HTTPClient = Service[Request, Response]

  /**
    * HTTP Client to Service worker
    */
  private val httpClient: HTTPClient = {
    val builder = ClientBuilder()
      .codec(Http().maxResponseSize(new StorageUnit(maxResponseSize)))
      .hosts(f"${endpoint.host}:${endpoint.port}")
      .hostConnectionLimit(connectionLimit)
      .retries(retries)
    if (useSsl) {
      filter andThen builder.tlsWithoutValidation().build()
    } else {
      filter andThen builder.build()
    }
  }

  /**
    * @param data Request data
    * @return HTTP Response
    */
  def post(data: Array[Byte]): Future[Response] = {
    val headers: Map[String, String] = Map(
      (HttpHeaders.Names.USER_AGENT, "discovery/scala"),
      (HttpHeaders.Names.CONTENT_LENGTH, f"${data.length}")
    )

    val request = RequestBuilder()
      .url(f"${endpoint.uri}")
      .addHeaders(headers)
      .buildPost(Buf.ByteArray.Shared(data))

    httpClient(request)
  }

  /**
    * Close client.
    * @return
    */
  def close(): Future[Unit] = httpClient.close()

}