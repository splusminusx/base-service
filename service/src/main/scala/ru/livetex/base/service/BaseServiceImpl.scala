package ru.livetex.base.service

import com.twitter.util.Future
import ru.livetex.base.interface.BaseService.FutureIface


/**
  * Base Service.
  * @param value initial value.
  */
class BaseServiceImpl(value: String) extends FutureIface {

  /**
    * @inheritdoc
    */
  override def ping(): Future[Unit] = {
    Future.Unit
  }

  /**
    * @inheritdoc
    */
  override def start(): Future[String] = {
    Future.value(value)
  }
}
