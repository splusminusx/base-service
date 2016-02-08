package ru.livetex.base.service.config

import com.typesafe.config.Config
import ru.livetex.base.service.endpoint.Endpoint
import scala.collection.JavaConversions._


object EndpointConfig {
  def apply(config: Config): Endpoint = {

    Endpoint(
      config.getString("host"),
      config.getInt("port"),
      config.getStringList("circuits"),
      config.getString("protocol"),
      config.getString("schema"),
      config.getBoolean("isPublic")
    )
  }
}
