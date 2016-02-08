package ru.livetex.base.service.discovery

import ru.livetex.base.service.endpoint.Endpoint

/**
  * Client factory Type class.
  * @tparam S Which type instance to create?
  */
trait ClientFactory[S] {
  /**
    * Creates new instance of service.
    * @param endpoint endpoint of service.
    * @return Client.
    */
  def create(endpoint: Endpoint): S

}
