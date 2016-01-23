#@namespace scala ru.livetex.base.interface

exception Error {
  1: required i32 code;
  2: required string message;
}

/**
 * Base service
 **/
service BaseService {

  /**
   * Endpoint availability check method.
   **/
  void ping()

  /**
   * Returns some value.
   **/
  string start()

}