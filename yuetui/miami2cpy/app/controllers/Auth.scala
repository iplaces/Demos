package controllers

import com.google.inject.{Inject, Singleton}
import common.AppSettings
import models.JsonProtocols
import org.slf4j.LoggerFactory
import play.api.mvc.Controller
import utils.TerraClient

/**
 * Created by ZYQ on 2016/3/11.
 **/
@Singleton
class Auth @Inject()(
                      val actionUtils: ActionUtils,
                      appSettings: AppSettings,
                      terraClient: TerraClient
                      ) extends Controller with JsonProtocols {

  import actionUtils._

  private val logger = LoggerFactory.getLogger(this.getClass)

  private val userAction = LoggingAction andThen UserAction
  private val storeAction = userAction andThen AdminAction
  private val adminAction = userAction andThen SystemAdminAction




  def loginPage = LoggingAction {
/*
* URLEncoder.encode(value, "UTF8")
* */
      Redirect(terraClient.loginUrl)
  }

}
