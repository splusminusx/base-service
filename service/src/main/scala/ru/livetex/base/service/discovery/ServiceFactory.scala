package ru.livetex.base.service.discovery

import ru.livetex.base.service.endpoint.Endpoint


trait ServiceFactory[S] {
  def create(endpoint: Endpoint): S
}
