package ru.livetex.base.service


import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.http.Http
import com.twitter.util.StorageUnit
import org.apache.thrift.protocol.TBinaryProtocol
import ru.livetex.base.interface.BaseService.{FinagledClient => BaseClient}
import ru.livetex.base.interface.BaseService.{FinagledService => BaseService}
import ru.livetex.base.service.Dependencies._
import ru.livetex.base.service.config.{DiscoveryConfig, ApplicationConfig, Defaults}
import ru.livetex.base.service.discovery.DiscoveryService
import ru.livetex.base.service.util.Logging
import ru.livetex.finagle.HttpToByteArray


object Application extends App with Logging {
  val discoveryConfig = DiscoveryConfig("/app/etc/config.json")
  val discovery = new DiscoveryService(discoveryConfig)
  discovery.getConfig(discoveryConfig.path).foreach(configData => {
    val applicationConfig = ApplicationConfig(configData)

    val binaryProtocolFactory = new TBinaryProtocol.Factory()
    val service = new BaseService(
      new BaseServiceImpl(applicationConfig.data.key),
      binaryProtocolFactory)
    val httpFilter = new HttpToByteArray(binaryProtocolFactory)

    ServerBuilder()
      .name(discovery.serviceName[BaseService])
      .bindTo(applicationConfig.nativeEndpoint.socket)
      .codec(Http().maxResponseSize(StorageUnit.parse(Defaults.MAX_RESPONSE_SIZE)))
      .build(httpFilter andThen service)

    discovery.register[BaseService](applicationConfig.nativeEndpoint)
      .flatMap(_ => discovery.withService[BaseClient, String](_.start))
      .map(x => logger.debug(s"Result $x"))
      .onFailure(e => logger.error(e.getMessage))
  })
}
