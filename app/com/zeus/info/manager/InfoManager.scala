package com.zeus.info.manager

import javax.inject._
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Class which manages general information of this server
  */
@Singleton
class InfoManager {

  implicit val infoFormatter = Json.format[Info]

  def getInfo(): Future[String] = {
    getFutureMessage().map(Json.toJson(_).toString())
  }

  private def getFutureMessage(): Future[Info] = Future {
    Info("It contains information about environment variables of the system", sys.env)
  }
}

case class Info(message: String, systemEnvs: Map[String, String])
