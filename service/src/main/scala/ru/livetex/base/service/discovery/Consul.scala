package ru.livetex.base.service.discovery

import consul.v1.acl.AclRequests
import consul.v1.agent.AgentRequests
import consul.v1.catalog.CatalogRequests
import consul.v1.common.Types
import consul.v1.event.EventRequests
import consul.v1.health.HealthRequests
import consul.v1.kv.KvRequests
import consul.v1.session.SessionRequests
import consul.v1.status.StatusRequests
import consul.ConsulApiV1

import scala.concurrent.ExecutionContext


/**
  * Consul client without Play Application Dependencies.
  * @param host Hostname.
  * @param port TCP Port for Consul HTTP API.
  * @param token Optional authentication token.
  * @param executionContext implicit [[ExecutionContext]].
  */
class Consul(host: String, port: Int, token: Option[String] = None)
            (implicit executionContext: ExecutionContext){

  lazy val v1: ConsulApiV1 with Types = new ConsulApiV1 with Types {
    private implicit def requestBasics = new ConsulRequestBasics(token)
    private lazy val basePath = s"http://$host:$port/v1"
    lazy val health:  HealthRequests  = HealthRequests( basePath)
    lazy val agent:   AgentRequests   = AgentRequests(  basePath)
    lazy val catalog: CatalogRequests = CatalogRequests(basePath)
    lazy val kv:      KvRequests      = KvRequests(     basePath)
    lazy val status:  StatusRequests  = StatusRequests( basePath)
    lazy val acl:     AclRequests     = AclRequests(    basePath)
    lazy val session: SessionRequests = SessionRequests(basePath)
    lazy val event:   EventRequests   = EventRequests(  basePath)
  }

}
