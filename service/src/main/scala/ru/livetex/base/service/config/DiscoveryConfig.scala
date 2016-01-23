package ru.livetex.base.service.config


import com.typesafe.config.Config


/**
  * Discovery Service Configuration
  * @param host Discovery service host.
  * @param port Discovery service port.
  * @param circuit Circuit in which search dependencies.
  * @param profile Service profile.
  */
case class DiscoveryConfig(host: String,
                           port: Int,
                           circuit: String,
                           profile: String)

object DiscoveryConfig {
  def apply(config: Config): DiscoveryConfig = {

    DiscoveryConfig(
      config.getString("host"),
      config.getInt("port"),
      config.getString("circuit"),
      config.getString("profile")
    )
  }
}
