package ru.livetex.base.service.config


import com.typesafe.config.ConfigFactory
import ru.livetex.base.service.endpoint.Endpoint


case class ApplicationConfig(nativeEndpoint: Endpoint,
                             data: DataConfig)


object ApplicationConfig {
  def apply(data: String): ApplicationConfig = {

    val config = ConfigFactory.parseString(data)

    ApplicationConfig(
      EndpointConfig(config.getConfig("endpoints").getConfig("native")),
      DataConfig(config.getConfig("data"))
    )
  }
}
