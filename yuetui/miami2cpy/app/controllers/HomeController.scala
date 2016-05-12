package controllers

import javax.inject.{Inject, Singleton}
import common.AppSettings
import models.JsonProtocols
import org.slf4j.LoggerFactory
import play.api.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import models.dao._
import play.api.mvc._
/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(
  actionUtils: ActionUtils,
  appSettings: AppSettings
) extends Controller with JsonProtocols{

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  import actionUtils._

  private val logger = LoggerFactory.getLogger(this.getClass)


  private val userAction = LoggingAction andThen UserAction
  private val adminAction = userAction andThen AdminAction
  private val systemAdminAction = userAction andThen SystemAdminAction

  def index = adminAction { request =>
    val conf = getConfMap(request)
    val url = appSettings.miamiHost + ":" + appSettings.miamiPort
    val confWs = conf ++ Map("url" -> url)
    Ok(views.html.family("miami", confWs))
  }

  def home = Action {
    Ok(views.html.home("miami"))
  }

}
