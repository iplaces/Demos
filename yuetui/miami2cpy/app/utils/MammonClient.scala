package utils

import com.google.inject.{Inject, Singleton}
import common.AppSettings
import org.slf4j.LoggerFactory
import play.api.libs.json.JsResultException

/**
 * Created by wangchunze on 2016/4/27.
 */

@Singleton
class MammonClient @Inject()(
                              httpUtil: HttpUtil,
                              appSettings: AppSettings
                             ) {

  private val log = LoggerFactory.getLogger(this.getClass)

  import scala.concurrent.ExecutionContext.Implicits.global

  val baseUrl = appSettings.mammonProtocol + "://" + appSettings.mammonDomain

  val appId = appSettings.mammonAppId
  val secureKey = appSettings.mammonSecureKey

  val miamiUrl = appSettings.miamiProtocol + "://" + appSettings.miamiDomain
  val notifyUrl = miamiUrl + "/miami/api/pay/response"
  val returnUrl = miamiUrl + "/miami/api/pay/callback"

  def preCreate(tradeNo: Long, fee: Int, subject: String) = {
    val url = baseUrl + "/mammon/precreate"

    val sn = System.nanoTime() + ""
    val timestamp = System.currentTimeMillis() + ""
    val params = List(appId, tradeNo.toString, sn, returnUrl, notifyUrl, fee.toString, subject, timestamp)
    val signature = SecureUtil.generateSignature(params, secureKey)

    val parameters = List(
      "appId" -> appId,
      "tradeNo" -> tradeNo.toString,
      "sn" -> sn,
      "timestamp" -> timestamp.toString,
      "sign" -> signature,
      "fee" -> fee.toString,
      "subject" -> subject,
      "returnUrl" -> returnUrl,
      "notifyUrl" -> notifyUrl
    )
    log.info(s"preCreate url:$url")
    httpUtil.getJsonRequestSend("Mammon preCreate", url, parameters).map{ json =>
      log.debug(s"Mammon upload preCreate: $json")
      try {
        val errorCode = (json \ "errCode").as[Int]
        val msg = (json \ "msg").as[String]
        if (errorCode != 0) {
          log.error("Mammon server error:" + msg)
          Left(json)
        } else {
          Right(json)
        }
      } catch {
        case ex: JsResultException =>
          log.error("Mammon preCreate json parse error:" + json)
          Left(json)
      }
    }
  }


  def pay(tradeNo: Long) = {
    val url = baseUrl + "/mammon/pay"
    s"$url?appId=$appId&tradeNo=$tradeNo"
  }





}
