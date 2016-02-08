package ru.livetex.base.service

import org.apache.thrift.protocol.TBinaryProtocol
import ru.livetex.base.interface.BaseService.FinagledClient
import ru.livetex.base.service.discovery.ClientFactory
import ru.livetex.base.service.endpoint.{HTTPClient, HTTPThriftClient, Endpoint}

/**
  * Created by splusminusx on 29.01.16.
  */
object Dependencies {
  implicit object BaseServiceClient extends ClientFactory[FinagledClient] {
    /**
      * @inheritdoc
      */
    def create(endpoint: Endpoint): FinagledClient = {
      new FinagledClient(
        new HTTPThriftClient(new HTTPClient(endpoint)),
        new TBinaryProtocol.Factory()
      )
    }
  }
}
