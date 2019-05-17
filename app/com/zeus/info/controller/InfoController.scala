package com.zeus.info.controller

import com.zeus.info.manager.InfoManager
import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._
import play.api.routing.Router

import scala.concurrent.ExecutionContext

/**
 * This controller creates an `Action` that demonstrates how to write
 * simple asynchronous code in a controller. It uses a timer to
 * asynchronously delay sending a response for 1 second.
 *
 * @param cc standard controller components
 * @param infoManager Information manager
 */
@Singleton
class InfoController @Inject()(cc: ControllerComponents, routesProvider: Provider[Router], infoManager: InfoManager)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  lazy val routeDocumentation: Seq[(String, String, String)] = routesProvider.get.documentation

  def index: Action[AnyContent] = Action {

    Ok {
      val routes = routeDocumentation.zipWithIndex map {
        case (doc, index) => (s"route-$index", doc)
      }
      Json.toJson(routes).toString()
    }
  }
  /**
   * Creates an Action that returns a plain text message after a delay
   * of 1 second.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/message`.
   */
  def getInfo: Action[AnyContent] = Action.async {
    infoManager.getInfo() map { msg => Ok(msg) }
  }

  def healthCheck(): Action[AnyContent] = Action(Ok("it works!"))
}
