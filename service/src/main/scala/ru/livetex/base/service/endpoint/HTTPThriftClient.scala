package ru.livetex.base.service.endpoint

import com.twitter.finagle.Service
import com.twitter.finagle.http.Status
import com.twitter.finagle.thrift.ThriftClientRequest
import com.twitter.util.Future


/**
  * Finagled service.
  *
  * @param client HTTP clients to service instances.
  */
class HTTPThriftClient(client: HTTPClient)
  extends Service[ThriftClientRequest, Array[Byte]] {

  /**
    * Finagled Service implementation
    *
    * @param request Thrift request
    * @return Response buffer
    */
  override def apply(request: ThriftClientRequest): Future[Array[Byte]] = {
    val respFuture = client.post(request.message)

    respFuture.map(httpResp => {
      if (httpResp.status != Status.Ok) {
        throw new Exception(f"Not success response: ${httpResp.status}")
      }

      val buf = new Array[Byte](httpResp.content.length)
      httpResp.content.write(buf, 0)
      buf
    })
  }

  /**
    * close client.
    */
  def closeClient(): Future[Unit] = client.close()
}