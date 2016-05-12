package util

import com.google.inject.Inject
import common.AppSettings
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.LoggerFactory
import play.api.Logger
import play.api.libs.json.JsObject
import play.api.libs.ws.WSClient
import utils.{SecureUtil, HttpUtil}
import scala.concurrent.duration.Duration
import scala.util.Random
import scala.concurrent.ExecutionContext.Implicits.global
/**
 * Auther: ball 
 * Date:   2016/4/14.
 * Sign:  Defensor
 *
 *
 *
 * I can because I think I can !
 */
class CommentClient @Inject()(
                               httpUtil: HttpUtil,
                               appSettings: AppSettings
                              ) {

  private val log = LoggerFactory.getLogger(this.getClass)

  val baseUrl = appSettings.tcommentProtocol + "://" + appSettings.tcommentDomain
  val appId = appSettings.tcommentAppId
  val secureKey = appSettings.tcommentSecureKey



  def insertComment(body:JsObject) ={
    val timeStamp = System.currentTimeMillis().toString
    val nonce = SecureUtil.nonceStr(6)
    val sign = SecureUtil.generateSignature(List(appId,timeStamp,nonce),secureKey)

    val commentUrl = baseUrl+"/tcomment/insertComment?appId="+appId+"&timeStamp="+timeStamp+
      "&nonce="+nonce+"&signature="+sign

    httpUtil.postJsonRequestSend("testComment",commentUrl,List(),body)
  }


  def getCommentsByItem(itemId:String,page:Int = 1,contentNum:Int = 10,gradeB:Int = 0, gradeT:Int = 10) ={
    val timeStamp = System.currentTimeMillis().toString
    val nonce = SecureUtil.nonceStr(6)
    val sign = SecureUtil.generateSignature(List(appId,timeStamp,nonce),secureKey)

    val commentUrl = baseUrl+"/tcomment/getCommentByItem?itemId="+itemId+"&appId="+appId+"&timeStamp="+timeStamp+
      "&nonce="+nonce+"&signature="+sign+"&gradeB="+gradeB+"&gradeT="+gradeT+"&page="+page+"&contentNum="+contentNum

    httpUtil.getJsonRequestSend("commentGet",commentUrl,List())
  }


  def getCommentsByStore(storeId:Long,gradeB:Int = 0, gradeT:Int = 10,  page:Int = 1,contentNum:Int = 10) ={
    val timeStamp = System.currentTimeMillis().toString
    val nonce = SecureUtil.nonceStr(6)
    val sign = SecureUtil.generateSignature(List(appId,timeStamp,nonce),secureKey)

    val commentUrl = baseUrl+"/tcomment/getCommentByStore?storeId="+storeId+"&appId="+appId+"&timeStamp="+timeStamp+
      "&nonce="+nonce+"&signature="+sign+"&gradeB="+gradeB+"&gradeT="+gradeT +"&page="+page+"&contentNum="+contentNum
    httpUtil.getJsonRequestSend("commentGet",commentUrl,List())
  }

  def getCommentsByUser(userId:Long,page:Int,contentNum:Int,gradeB:Int = 0, gradeT:Int = 10) ={
    val timeStamp = System.currentTimeMillis().toString
    val nonce = SecureUtil.nonceStr(6)
    val sign = SecureUtil.generateSignature(List(appId,timeStamp,nonce),secureKey)

    val commentUrl = baseUrl+"/tcomment/getCommentByUser?userId="+userId+"&appId="+appId+"&timeStamp="+timeStamp+
      "&nonce="+nonce+"&signature="+sign+"&gradeB="+gradeB+"&gradeT="+gradeT+"&page="+page+"&contentNum="+contentNum

    httpUtil.getJsonRequestSend("commentGet",commentUrl,List())
  }

  def deleteComment(commentId:Long) ={
    val timeStamp = System.currentTimeMillis().toString
    val nonce = SecureUtil.nonceStr(6)
    val sign = SecureUtil.generateSignature(List(appId,timeStamp,nonce),secureKey)

    val commentUrl = baseUrl+"/tcomment/deleteComment?commentId="+commentId+"&appId="+appId+"&timeStamp="+timeStamp+
      "&nonce="+nonce+"&signature="+sign

    httpUtil.getJsonRequestSend("commentGet",commentUrl,List())
  }




}
