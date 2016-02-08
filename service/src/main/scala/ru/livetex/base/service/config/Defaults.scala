package ru.livetex.base.service.config

import ru.livetex.base.service.BuildInfo

/**
  * Created by splusminusx on 29.01.16.
  */
object Defaults {
  val MAX_RESPONSE_SIZE = "100.megabyte"
  val CONNECTION_LIMIT = 100
  val SERVICE_NAME = BuildInfo.name
  val SERVICE_VERSION = BuildInfo.version
}
