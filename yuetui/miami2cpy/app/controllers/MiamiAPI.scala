package controllers

import com.google.inject.{Inject, Singleton}
import common.AppSettings
import models.JsonProtocols
import models.dao.StoreDAO
import models.tables.SlickTables
import org.slf4j.LoggerFactory
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, Controller}
import utils.BazaarClient
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
 * Created by 王春泽 on 2016/4/6.
 */

@Singleton
class MiamiAPI @Inject()(
                          appSettings: AppSettings,
                          actionUtils: ActionUtils,
                          bazaarClient:BazaarClient,
                          storeDAO:StoreDAO
                          )extends Controller with JsonProtocols{

  import actionUtils._
  private val userAction = LoggingAction andThen UserAction
  actionUtils.cacheReducer

  private val log = LoggerFactory.getLogger(this.getClass)

  val appId = appSettings.terraAppId

  def openMiami(
                 appid: String,
                 sn: String,
                 timestamp: Long,
                 nonce: String,
                 signature: String,
                 storeId: Long
                 )={
    actionUtils.checkSignature(
    appid,sn,List(appid,sn,timestamp.toString,nonce,storeId.toString),signature
    ){
      def openMiamiAction =
      LoggingAction.async{implicit request=>
        bazaarClient.enableMiami(storeId).flatMap{res=>
          val errCode = (res \ "errCode").asOpt[Int].getOrElse(-1)
          if(errCode==0){ //成功
            val shopInfo = (res \ "shopInfo").as[JsValue]
            val shopName=(shopInfo \ "shopName").as[String]
            val phone = (shopInfo \ "phone").as[String]
            val desc = (shopInfo \ "description").as[String]
            val address = (shopInfo \ "location").as[String]
            val currentTime = System.currentTimeMillis()
            storeDAO.getStoreById(storeId).flatMap {
              case Some(store)=>  //已经存在 则启用店铺
                  //启用店铺
                  storeDAO.enable(storeId).map {
                    case Success(_) =>
                      log.info(s"store $storeId, enabled.")
                      Ok(success)
                    case Failure(e) =>
                      log.error(e.getMessage)
                      Ok(ErrorCode.changeStateFailed)
                  }
              case None=> {
                //不存在 创建店铺
                val s = SlickTables.rStores(
                  storeId,
                  shopName,
                  desc,
                  contact = "",
                  address,
                  icon = "",
                  openFrom = 0l,
                  openTo = 1440l,
                  basePrice = 0,
                  packFee = 0,
                  catId = 0,
                  costTime = 60,
                  createTime = currentTime,
                  modifiedTime = currentTime,
                  state = 0
                )
                storeDAO.add(s).map {
                  case Success(_) =>
                    log.info(s"admin store $s created.")
                    Ok(success)
                  case Failure(e) =>
                    log.error(e.getMessage)
                    Ok(ErrorCode.storeInsertFailed)
                }
              }
            }
          }else{ //失败
            Future.successful(Ok(ErrorCode.openMiamiFailed))
          }
        }
      }
      openMiamiAction
    }

  }

  /**
   * 禁用外卖接口
    *
    * @param appid
   * @param sn
   * @param timestamp
   * @param nonce
   * @param signature
   * @param storeId
   * @return
   */
  def disableMiami(
                    appid: String,
                    sn: String,
                    timestamp: Long,
                    nonce: String,
                    signature: String,
                    storeId: Long
                    )={
    actionUtils.checkSignature(
      appid,sn,List(appid,sn,timestamp.toString,nonce,storeId.toString),signature
    ){
      def disableMiamiAction=
      LoggingAction.async{implicit request=>
        bazaarClient.disableMiami(storeId).flatMap{res=>
          val errCode = (res \ "errCode").asOpt[Int].getOrElse(-1)
          if(errCode==0){ //调用bazaar接口成功
            //禁用店铺
            storeDAO.disable(storeId).map {
              case Success(_) =>
                log.info(s"store $storeId, disabled.")
                Ok(success)
              case Failure(e) =>
                log.error(e.getMessage)
                Ok(ErrorCode.changeStateFailed)
            }
          }else{  //失败
            Future.successful(Ok(ErrorCode.disableMiamiFailed))
          }
        }
      }
      disableMiamiAction
    }
  }

  /**
   * 开通外卖接口for bazaar
   * @param appid
   * @param sn
   * @param timestamp
   * @param nonce
   * @param signature
   * @return
   */
  def openMiami4Bazaar(
                      appid: String,
                      sn: String,
                      timestamp: Long,
                      nonce: String,
                      signature: String
                        )={
    actionUtils.checkSignature(
      appid,sn,List(appid,sn,timestamp.toString,nonce),signature
    ){
      def openMiamiAction =
        LoggingAction.async { implicit request =>

          request.body.asJson match {
            case Some(jsonData)=>
              val storeId=(jsonData \ "storeId").as[Long]
              val shopName=(jsonData \ "shopName").as[String]
              val desc=(jsonData \ "description").as[String]
              val phone = (jsonData \ "phone").as[String]
              val address=(jsonData \ "address").as[String]
              val createTime=System.currentTimeMillis()
          storeDAO.getStoreById (storeId).flatMap {
            case Some(store) => //已经存在 则启用店铺
              //启用店铺
              storeDAO.enable(storeId).map {
                case Success(_) =>
                  log.info(s"store $storeId, enabled.")
                  Ok(success)
                case Failure(e) =>
                  log.error(e.getMessage)
                  Ok(ErrorCode.changeStateFailed)
              }
            case None => {
              //不存在 创建店铺
              val s = SlickTables.rStores(
                storeId,
                shopName,
                desc,
                phone,
                address,
                icon = "",
                openFrom = 0l,
                openTo = 1440l,
                basePrice = 0,
                packFee = 0,
                catId = 0,
                costTime = 60,
                createTime = createTime,
                modifiedTime = createTime,
                state = 0
              )
              storeDAO.add(s).map {
                case Success(_) =>
                  log.info(s"admin store $s created.")
                  Ok(success)
                case Failure(e) =>
                  log.error(e.getMessage)
                  Ok(ErrorCode.storeInsertFailed)
              }
            }
          }
            case None=>
              Future.successful (Ok (ErrorCode.openMiamiFailed))
          }
        }
      openMiamiAction
    }

  }


  /**
   * 禁用外卖接口 for bazaar
   * @param appid
   * @param sn
   * @param timestamp
   * @param nonce
   * @param signature
   * @param storeId
   * @return
   */
  def disableMiami4Bazaar(
                    appid: String,
                    sn: String,
                    timestamp: Long,
                    nonce: String,
                    signature: String,
                    storeId: Long
                    )={
    actionUtils.checkSignature(
      appid,sn,List(appid,sn,timestamp.toString,nonce,storeId.toString),signature
    ){
      def disableMiamiAction=
        LoggingAction.async{implicit request=>
              //禁用店铺
          storeDAO.disable(storeId).map {
            case Success(_) =>
              log.info(s"store $storeId, disabled.")
              Ok(success)
            case Failure(e) =>
              log.error(e.getMessage)
              Ok(ErrorCode.changeStateFailed)
          }
        }
      disableMiamiAction
    }
  }


}
