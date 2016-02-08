package ru.livetex.base.service.discovery


import com.twitter.util.Future
import consul.v1.health.NodesHealthService
import ru.livetex.base.service.config.DiscoveryConfig
import ru.livetex.base.service.endpoint.Endpoint
import ru.livetex.base.service.util._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.reflect.ClassTag
import scala.util.Random


/**
  * Discovery Service client.
  * @param config Discovery configuration/
  */
class DiscoveryService(config: DiscoveryConfig) {
  private val consul = new Consul(config.host, config.port)
  private val CIRCUIT_TAG_PREFIX = "circuit="

  import consul.v1._

  /**
    * Register endpoint in Discovery Service.
    * @param endpoint Endpoint configuration.
    * @tparam S Which type of service?
    */
  def register[S](endpoint: Endpoint)
                 (implicit ct: ClassTag[S]): Future[Boolean] = {
    val check = agent.service.httpCheck(
      s"http://${endpoint.host}:${endpoint.port}/health", "15s")
    val service = agent.service.LocalService(
      id(serviceName(ct), endpoint),
      serviceType(serviceName(ct), endpoint),
      tags(endpoint),
      Some(endpoint.port),
      Some(check))
    agent.service.register(service)
  }

  /**
    * Search Endpoint by full name.
    * @param serviceName Endpoint full name.
    * @return Sequence of Endpoints.
    */
  def searchByName(serviceName: String): Future[Seq[Endpoint]] = {
    health.service(
      ServiceType(serviceName),
      Some(circuitToTag(config.circuit)),
      passing = false
    ).map(_.map(status => {
      println(status)
      populateEndpoint(status, serviceName)
    }))
  }

  def search[S](implicit ct: ClassTag[S]): Future[Seq[Endpoint]] = {
    searchByName(serviceName(ct))
  }

  private def select(endpoints: Seq[Endpoint]): Option[Endpoint] = {
    Random.shuffle(endpoints).headOption
  }

  def withService[S, R](block: S => Future[R])
                       (implicit factory: ClientFactory[S],
                        ct: ClassTag[S]): Future[R] = {
    searchByName(serviceName(ct)).flatMap(endpoints => {
      block(factory.create(select(endpoints).head))
    })
  }

  def serviceName[S](implicit ct: ClassTag[S]): String = {
    ct.runtimeClass.getCanonicalName.stripSuffix(".FinagledService").stripSuffix(".FinagledClient")
  }

  private def populateEndpoint(status: NodesHealthService, serviceName: String): Endpoint = {
    Endpoint(
      host = status.Node.Address,
      port = status.Service.Port,
      circuits = Seq(config.circuit),
      protocol = protocolFromId(status.Service.ID, serviceName),
      schema = schemaFromId(status.Service.ID, serviceName),
      isPublic = true
    )
  }

  private def id(serviceName: String, endpoint: Endpoint): ServiceId = {
    ServiceId(s"$serviceName-${config.profile}.${endpoint.protocol}-${endpoint.schema}")
  }

  private def schemaFromId(id: String, serviceName: String): String = {
    id.split('.').lastOption match {
      case Some(text) => text.split('-').lift(1) match {
        case Some(schema) => schema
        case _ => Endpoint.DEFAULT_PROTOCOL
      }
      case _ => Endpoint.DEFAULT_PROTOCOL
    }
  }

  private def protocolFromId(id: String, serviceName: String): String = {
    id.split('.').lastOption match {
      case Some(text) => text.split('-').headOption match {
        case Some(schema) => schema
        case _ => Endpoint.DEFAULT_PROTOCOL
      }
      case _ => Endpoint.DEFAULT_PROTOCOL
    }
  }

  private def serviceType(serviceName: String, endpoint: Endpoint): ServiceType = {
    ServiceType(s"$serviceName")
  }

  private def tags(endpoint: Endpoint): Set[ServiceTag] = {
    endpoint.circuits.map(circuitToTag).toSet
  }

  private def circuitToTag(circuit: String): ServiceTag = {
    ServiceTag(s"$CIRCUIT_TAG_PREFIX$circuit")
  }

  def getConfig(key: String): Future[String] = {
    kv.get(key, false).map(kvs => kvs.head.Value)
  }
}

