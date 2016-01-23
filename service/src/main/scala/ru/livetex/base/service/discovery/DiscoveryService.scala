package ru.livetex.base.service.discovery

import ru.livetex.base.service.config.{EndpointConfig, DiscoveryConfig}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


/**
  * Discovery Service client.
  * @param baseServiceName Name of current component.
  * @param config Discovery configuration/
  */
class DiscoveryService(baseServiceName: String, config: DiscoveryConfig) {
  private val consul = new Consul(config.host, config.port)
  import consul.v1._

  /**
    * Register endpoint in Discovery Service.
    * @param endpointName Name of endpoint to be registered.
    * @param endpoint Endpoint configuration.
    */
  def register(endpointName: String, endpoint: EndpointConfig): Future[Boolean] = {
    val check = agent.service.httpCheck(
      s"http://${endpoint.host}:${endpoint.port}/health", "15s")
    val service = agent.service.LocalService(
      id(endpointName),
      serviceType(endpointName),
      tags(endpoint),
      Some(endpoint.port),
      Some(check))
    agent.service.register(service)
  }

  def search(endpointName: String): Unit = {
    health.service(
      serviceType(endpointName),
      Some(ServiceTag(s"circuit=${config.circuit}")))
      .foreach(_.foreach(println))
  }

  private def id(endpointName: String): ServiceId = {
    ServiceId(s"$baseServiceName-${config.profile}.$endpointName")
  }

  private def serviceType(endpointName: String): ServiceType = {
    ServiceType(s"$baseServiceName-${config.profile}.$endpointName")
  }

  private def tags(endpoint: EndpointConfig): Set[ServiceTag] = {
    endpoint.circuits.map(c => ServiceTag(s"circuit=$c")).toSet
  }
}

