package utils

import java.io.File

import com.google.inject.{Inject, Singleton}
import common.AppSettings
import org.slf4j.LoggerFactory
import play.api.Logger
import play.api.libs.json.{JsResultException, JsValue}
import play.api.mvc.MultipartFormData.FilePart

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by ZYQ on 2016/3/10.
 **/
@Singleton
class HestiaClient @Inject() (
                               httpUtil: HttpUtil,
                               appSettings: AppSettings
                               ) {

  private val log = LoggerFactory.getLogger(this.getClass)



  val baseUrl = appSettings.hestiaProtocol + "://" + appSettings.hestiaDomain

//  val baseUrl = appSettings.hestiaProtocol + "://" +appSettings.hestiaHost + ":" + appSettings.hestiaPort
  val appId = appSettings.hestiaAppId
  val secureKey = appSettings.hestiaSecureKey
  val partKey = appSettings.hestiaPartKey

  log.info(s"init hestia client: $baseUrl")

  def upload(targetFile: File, fileName: String): Future[Either[JsValue, String]]= {
    val url = baseUrl + "/hestia/files/upload"
    val sn = System.nanoTime() + ""
    val params = List(appId, sn)
    val (timestamp, nonce, signature)= SecureUtil.generateSignatureParameters(params, secureKey)
    val parameters = List(
      "appId" -> appId,
      "sn" -> sn,
      "timestamp" -> timestamp,
      "nonce" -> nonce,
      "signature" -> signature
    )

    log.info(s"upload url:$url")
    //TODO  fileName, partKey
    httpUtil.postFileRequestSend("HestiaUpload", url, parameters, targetFile, fileName, partKey).map{ json =>
      log.debug(s"hestia upload rst: $json")
      try {
        val errorCode = (json \ "errCode").asOpt[Int]
        val errorMessage = (json \ "msg").asOpt[String]
        if (errorCode.getOrElse(0) != 0) {
          log.error("hestia server error:" + errorMessage.getOrElse("unknown."))
          Left(json)
        } else {
          val fileName = (json \ "fileName").as[String]
          Right(fileName)
        }
      } catch {
        case ex: JsResultException =>
          log.error("hestia upload json parse error:" + json)
          Left(json)
      }
    }
  }

  def getImageUrl(fileName: String) = {
    s"$baseUrl/hestia/files/image/$appId/$fileName"
  }


}
