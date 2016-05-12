package controllers

import javax.inject.{Inject, Singleton}

import models.JsonProtocols
import org.slf4j.LoggerFactory
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import utils.HestiaClient

import scala.concurrent.Future

/**
 * Created by ZYQ on 2016/3/10.
 **/

@Singleton
class Materials @Inject() (
                            hestiaClient: HestiaClient,
                               actionUtils: ActionUtils
                               ) extends Controller with JsonProtocols{
  import actionUtils._

  private val logger = LoggerFactory.getLogger(this.getClass)


  private val userAction = LoggingAction andThen UserAction
  private val storeAction = userAction andThen AdminAction
  private val adminAction = userAction andThen SystemAdminAction


  def uploadImage() = storeAction.async { request =>
    try {
      request.body.asMultipartFormData match {
        case Some(multiForm) =>
          if(multiForm.file("image").isDefined) {
            val file = multiForm.file("image").get.ref.file
            val fileName = multiForm.file("image").get.filename
            hestiaClient.upload(file, fileName).map {
              case Left(jsValue) =>
                logger.info("Image upload failed!")
                Ok(ErrorCode.uploadImageFailed)
              case Right(fileName) =>
                Ok(successResult(Json.obj("fileName" -> hestiaClient.getImageUrl(fileName))))
            }
          } else {
            Future(Ok(ErrorCode.uploadImageEmptyForm))
          }
        case None =>
          Future(Ok(ErrorCode.uploadImageEmptyForm))
      }
    } catch {
      case e: Exception =>
      Future(Ok(ErrorCode.uploadImageFailed))
    }

  }

}