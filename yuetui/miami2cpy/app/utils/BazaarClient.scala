package utils

import com.google.inject.{Inject, Singleton}
import common.AppSettings
import org.slf4j.LoggerFactory

/**
 * Created by 王春泽 on 2016/4/6.
 */
@Singleton
class BazaarClient @Inject()(
                             httpUtil: HttpUtil,
                             appSettings: AppSettings
                             ) {
  private val log = LoggerFactory.getLogger(this.getClass)

  val baseUrl = appSettings.bazaarProtocol + "://" + appSettings.bazaarDomain
  val appId = appSettings.bazaarAppId
  val secureKey = appSettings.bazaarSecureKey

  log.info(s"init bazaar client: $baseUrl")

  /**
   * 开通外卖服务 调用bazaar的接口
   * @param storeId
   * @return
   */
  def enableMiami(storeId:Long)={
    val sn = System.currentTimeMillis().toString+storeId
    val timeStamp = System.currentTimeMillis()
    val nonce=SecureUtil.nonceStr(6)
    val signature =
      SecureUtil.generateSignature(List(
        appId,sn,timeStamp.toString,nonce,storeId.toString),secureKey)
    val url= baseUrl + "/bazaar/enableMiami"
    val para=List(
      "appId"->appId,
      "sn"->sn,
      "timestamp"->timeStamp.toString,
      "nonce"->nonce,
      "signature"->signature,
      "userId"->storeId.toString)
    httpUtil.getRequestSend("GET",url,para)
  }

  /**
   * 禁用外卖服务 调用bazaar接口
   * @param storeId
   * @return
   */
  def disableMiami(storeId:Long)={
    val sn = System.currentTimeMillis().toString+storeId
    val timeStamp = System.currentTimeMillis()
    val nonce=SecureUtil.nonceStr(6)
    val signature =
      SecureUtil.generateSignature(List(
        appId,sn,timeStamp.toString,nonce,storeId.toString),secureKey)
    val url= baseUrl + "/bazaar/disableMiami"
    val para=List(
      "appId"->appId,
      "sn"->sn,
      "timestamp"->timeStamp.toString,
      "nonce"->nonce,
      "signature"->signature,
      "userId"->storeId.toString)
    httpUtil.getRequestSend("GET",url,para)
  }


}
