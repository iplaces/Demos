package utils

import com.google.inject.{Inject, Singleton}
import common.AppSettings
import org.slf4j.LoggerFactory
import play.api.Logger
import play.api.libs.json.Json


/**
 * Created by ZYQ on 2016/3/11.
 **/
@Singleton
class TerraClient @Inject ()(
                              httpUtil: HttpUtil,
                              appSettings: AppSettings
                              ) {
  private val log = LoggerFactory.getLogger(this.getClass)



  //  val baseUrl = appSettings.hestiaProtocol + "://" +appSettings.hestiaHost + ":" + appSettings.hestiaPort
  val baseUrl = appSettings.terraProtocol + "://" + appSettings.terraDomain
  val appId = appSettings.terraAppId
  val secureKey = appSettings.terraSecureKey
  val redirectUrl = appSettings.terraRedirectUrl

  log.info(s"init terra client: $baseUrl")

  val loginUrl = {
    //TODO add state
    val url = baseUrl + "/terra/api/login"
    val rUrl = java.net.URLEncoder.encode(redirectUrl, "UTF-8")
    s"$url?redirect_url=$rUrl&appid=$appId"
  }

  def create(email: String) =  {

    val sn = s"$email " + System.currentTimeMillis()

    val (time, nonce, signature) =
      SecureUtil.generateSignatureParameters(List(
        appId, email, sn), secureKey)

    val url = baseUrl + "/terra/api/createstoreuser"

    val parameters = List(
      "appid" -> appId,
      "email" -> email,
      "timestamp" -> time,
      "sn" -> sn,
      "nonce" -> nonce,
      "signature" -> signature
    )

    httpUtil.getRequestSend("MiamiCreateStore", url, parameters)
  }

  /**
   * 获取用户的收货地址
    *
    * @param customerId
   * @return
   */
  def getCustomerAddress(customerId:Long)={
    val sn = System.currentTimeMillis().toString+customerId
    val timeStamp = System.currentTimeMillis()
    val nonce=SecureUtil.nonceStr(6)
    val signature =
      SecureUtil.generateSignature(List(
        appId,sn,timeStamp.toString,nonce,customerId.toString), secureKey)
    val url= baseUrl + "/terra/api/address/getaddress"
    val para=List(
      "appid"->appId,
      "sn"->sn,
      "timestamp"->timeStamp.toString,
      "nonce"->nonce,
      "signature"->signature,
      "uid"->customerId.toString)

    httpUtil.getRequestSend("GET",url,para)
  }

  /**
   * 添加用户的收货地址
   * @param customerId
   * @param name
   * @param address
   * @param phone
   * @return
   */
  def setCustomerAddress(customerId:Long,name:String,address:String,phone:String)={
    val sn=System.currentTimeMillis().toString+customerId //序列号
    val timeStamp=System.currentTimeMillis()
    val nonce=SecureUtil.nonceStr(6)
    val signature=
      SecureUtil.generateSignature(List(
         appId,sn,timeStamp.toString,nonce,customerId.toString,name,address,phone),secureKey)
    val url=baseUrl + "/terra/api/address/addaddress"
    val para=List(
      "appid"->appId,
      "sn"->sn,
      "timestamp"->timeStamp.toString,
      "nonce"->nonce,
      "signature"->signature,
      "uid"->customerId.toString,
      "name"->name,
      "address"->address,
      "phone"->phone
    )

    httpUtil.getRequestSend("GET",url,para)
  }

  /**
   * 修改用户地址
   * @param customerId
   * @param name
   * @param address
   * @param phone
   * @return
   */
  def modifyCustomerAddress(addressid:Long,customerId:Long,name:String,address:String,phone:String)={
    val sn=System.currentTimeMillis().toString+customerId  //序列号
    val timeStamp=System.currentTimeMillis()
    val nonce=SecureUtil.nonceStr(6)
    val signature=
      SecureUtil.generateSignature(List(
        appId,sn,timeStamp.toString,nonce,addressid.toString,customerId.toString,name,address,phone),secureKey)
    val url=baseUrl + "/terra/api/address/editaddress"
    val para=List(
      "appid"->appId,
      "sn"->sn,
      "timestamp"->timeStamp.toString,
      "nonce"->nonce,
      "signature"->signature,
      "uid"->customerId.toString,
      "addressid"->addressid.toString,
      "name"->name,
      "address"->address,
      "phone"->phone
    )
    httpUtil.getRequestSend("GET",url,para)
  }


  /**
   * 删除用户地址
   * @param customerId
   * @param addressId
   * @return
   */
  def deleteCustomerAddress(customerId:Long,addressId:Long)={
    val sn=System.currentTimeMillis().toString+customerId  //序列号
    val timeStamp=System.currentTimeMillis()
    val nonce=SecureUtil.nonceStr(6)
    val signature=
      SecureUtil.generateSignature(List(
        appId,sn,timeStamp.toString,nonce,customerId.toString,addressId.toString),secureKey)
    val url=baseUrl + "/terra/api/address/deleteaddress"
    val para=List(
      "appid"->appId,
      "sn"->sn,
      "timestamp"->timeStamp.toString,
      "nonce"->nonce,
      "signature"->signature,
      "uid"->customerId.toString,
      "addressid"->addressId.toString
    )
    httpUtil.getRequestSend("GET",url,para)
  }



}
