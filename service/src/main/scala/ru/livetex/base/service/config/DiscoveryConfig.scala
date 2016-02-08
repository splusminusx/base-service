package ru.livetex.base.service.config

import java.io.File
import com.typesafe.config.ConfigFactory


/**
  * Discovery Service Configuration
  * @param host Discovery service host.
  * @param port Discovery service port.
  * @param circuit Circuit in which search dependencies.
  * @param profile Application profile.
  * @param path application config path.
  */
case class DiscoveryConfig(host: String,
                           port: Int,
                           circuit: String,
                           profile: String,
                           path: String)

object DiscoveryConfig {
  def apply(path: String): DiscoveryConfig = {

    val defaultConfig = new File(path).exists()
    val config = defaultConfig match {
      case true => ConfigFactory.parseFile(new File(path))
      case false => ConfigFactory.parseFile(new File("./etc/config.json"))
    }

    DiscoveryConfig(
      config.getString("host"),
      config.getInt("port"),
      config.getString("circuit"),
      config.getString("profile"),
      config.getString("path")
    )
  }
}