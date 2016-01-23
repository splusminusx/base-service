package ru.livetex.base.service.config

import com.typesafe.config.Config


/**
  * Configuration data
  * @param key Some key.
  */
case class DataConfig(key: String)

object DataConfig {
  def apply(config: Config): DataConfig = {

    DataConfig(config.getString("key"))
  }
}
