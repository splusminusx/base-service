package ru.livetex.base.service.util

import org.slf4j.{Logger, LoggerFactory}


/**
  * Миксин для добавления возможности логировать что-либо
  *
  * class MyClass extends Logging {
  *   def doSomething: Unit = {
  *     logger.log("log")
  *   }
  * }
  */
trait Logging {
  protected val logger: Logger = LoggerFactory.getLogger(getNames)

  private def getNames: String = getClass.getName.stripSuffix("$")
}
