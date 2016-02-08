package ru.livetex.base.service.endpoint

import java.net.InetSocketAddress


/**
  * Endpoint configuration.
  * @param host Discovery service host.
  * @param port Discovery service port.
  * @param circuits Circuits in which this endpoint will be available.
  * @param protocol Type of protocol: binary, json.
  * @param schema Type of transport schema: tcp, http, https.
  * @param isPublic Expose endpoint through service discovery.
  */
case class Endpoint(host: String,
                    port: Int,
                    circuits: Seq[String],
                    protocol: String = Endpoint.DEFAULT_PROTOCOL,
                    schema: String = Endpoint.DEFAULT_SCHEMA,
                    isPublic: Boolean = false) {

  /**
    * @return IP Socket.
    */
  def socket: InetSocketAddress = {
    new InetSocketAddress(host, port)
  }

  /**
    * @return URI.
    */
  def uri: String = s"$schema://$host:$port"

}


object Endpoint {
  val DEFAULT_PROTOCOL = "binary"
  val DEFAULT_SCHEMA = "http"
}
