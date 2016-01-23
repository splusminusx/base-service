package ru.livetex.base.service


import com.twitter.finagle.builder.ServerBuilder
import com.twitter.util.StorageUnit
import ru.livetex.base.service.config.ApplicationConfig
import com.twitter.finagle.http.Http
import ru.livetex.base.service.discovery.DiscoveryService
import scala.concurrent.ExecutionContext.Implicits.global


object Application extends App {
  val MAX_RESPONSE_SIZE = "100.megabyte"
  val SERVICE_NAME = s"${BuildInfo.name}-app"
  val SERVICE_VERSION = BuildInfo.version

  val conf = ApplicationConfig("/app/etc/config.json")

  ServerBuilder()
    .name("BaseService")
    .bindTo(conf.nativeEndpoint.socket)
    .codec(Http().maxResponseSize(StorageUnit.parse(MAX_RESPONSE_SIZE)))
    .build(new BaseService(conf.data.key))

  val discovery = new DiscoveryService(SERVICE_NAME, conf.discovery)
  discovery.register("native", conf.nativeEndpoint)
    .map(_ => discovery.search("native"))

}
