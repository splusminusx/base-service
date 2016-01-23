package ru.livetex.base.service.config

import java.net.InetSocketAddress

import com.typesafe.config.Config
import scala.collection.JavaConversions._


/**
  * Endpoint configuration.
  * @param host Discovery service host.
  * @param port Discovery service port.
  * @param circuits Circuits in which this endpoint will be available.
  * @param isPublic Expose endpoint through service discovery.
  */
case class EndpointConfig(host: String,
                          port: Int,
                          circuits: Seq[String],
                          isPublic: Boolean) {

  /**
    * @return IP Socket.
    */
  def socket: InetSocketAddress = {
    new InetSocketAddress(host, port)
  }

}

object EndpointConfig {
  def apply(config: Config): EndpointConfig = {

    EndpointConfig(
      config.getString("host"),
      config.getInt("port"),
      config.getStringList("circuits"),
      config.getBoolean("isPublic")
    )
  }
}
