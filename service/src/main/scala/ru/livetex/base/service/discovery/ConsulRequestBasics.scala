package ru.livetex.base.service.discovery

import com.ning.http.client.AsyncHttpClientConfig
import consul.v1.common.{ ConsulRequestBasics => AppConsulRequestBasics }
import play.api.libs.json._
import play.api.libs.ws.ning.NingWSClient
import play.api.libs.ws.{WS, WSRequest, WSResponse}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}


/**
  * Consul Requests with [[NingWSClient]].
  * @param token Authentication token.
  */
class ConsulRequestBasics(token: Option[String]) extends AppConsulRequestBasics(token) {

  private implicit def client = new NingWSClient(
    new AsyncHttpClientConfig.Builder().build())

  override def jsonRequestMaker[A](path: String, httpFunc: HttpFunc)
                         (body: JsValue => A)
                         (implicit executionContext: ExecutionContext): Future[A] = {
    genRequestMaker(path,httpFunc)(_.json)(body)
  }

  override def responseStatusRequestMaker[A](path: String, httpFunc: HttpFunc)
                                   (body: Int => A)
                                   (implicit executionContext: ExecutionContext): Future[A] = {
    genRequestMaker(path,httpFunc)(_.status)(body)
  }

  override def stringRequestMaker[A](path: String, httpFunc: HttpFunc)
                           (body: String => A)
                           (implicit executionContext: ExecutionContext): Future[A] = {
    genRequestMaker(path,httpFunc)(_.body)(body)
  }

  private def genRequestMaker[A,B](path: String, httpFunc: HttpFunc)
                                  (responseTransformer: WSResponse => B)
                                  (body: B => A)
                                  (implicit executionContext: ExecutionContext): Future[A] = {
    Try((withToken(token) andThen httpFunc)(WS.clientUrl(path))) match {
      case Failure(exception) => Future.failed(exception)
      case Success(resultF)   => resultF.map( responseTransformer andThen body )
    }
  }

  private def withToken(token: Option[String]): RequestTransformer = {
    token.map(t => (req: WSRequest) => req.withQueryString("token" -> t)).getOrElse(identity)
  }
}
