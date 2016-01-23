package ru.livetex.base.service.config

import java.io.File

import com.typesafe.config.ConfigFactory



case class ApplicationConfig(discovery: DiscoveryConfig,
                             nativeEndpoint: EndpointConfig,
                             data: DataConfig)


object ApplicationConfig {
  def apply(path: String): ApplicationConfig = {

    val defaultConfig = new File(path).exists()
    val config = defaultConfig match {
      case true => ConfigFactory.parseFile(new File(path))
      case false => ConfigFactory.parseFile(new File("./etc/config.json"))
    }

    ApplicationConfig(
      DiscoveryConfig(config.getConfig("discovery")),
      EndpointConfig(config.getConfig("endpoints").getConfig("native")),
      DataConfig(config.getConfig("data"))
    )
  }
}
