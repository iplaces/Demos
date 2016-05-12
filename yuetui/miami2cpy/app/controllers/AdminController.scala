package controllers

import com.google.inject.{Inject, Singleton}
import models.JsonProtocols
import models.dao.{CategoryDAO, GoodDAO, StoreDAO}
import models.tables.SlickTables
import org.slf4j.LoggerFactory
import play.api.libs.json.{JsResultException, Json}
import play.api.mvc.{Action, Controller}
import utils.TerraClient
import CategoryDAO._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}
/**
 * Created by ZYQ on 2016/3/10.
 **/
@Singleton
class AdminController @Inject()(
                                 storeDAO: StoreDAO,
                                 goodDAO: GoodDAO,
                                 categoryDAO: CategoryDAO,
                                 terraClient: TerraClient,
                                  actionUtils: ActionUtils
                                 ) extends Controller with JsonProtocols {

  import actionUtils._

  private val logger = LoggerFactory.getLogger(this.getClass)


  private val userAction = LoggingAction andThen UserAction
  private val adminAction = userAction andThen AdminAction
  private val systemAdminAction = userAction andThen SystemAdminAction


  def parseOpenTime(time: String) = {
    val s = time.split(" : ")
    val hour = s(0).toLong
    val minute = s(1).toLong
    val stamp = s(2).toString
    val tag = (hour, stamp) match {
      case (12, "PM") => 0L
      case (12, "AM") => -720L
      case (_, "PM") => 720L
      case (_, "AM") => 0L
    }
    hour * 60 + minute + tag
  }

  def parseMinutes(minutes: Long) = {
    val (mins, tag) = if(minutes > 720l) (minutes - 720l, "PM") else (minutes, "AM")
    mins / 60 + " : " + mins % 60 + " : " + tag
  }

  def createStore() = systemAdminAction.async { implicit request =>

    request.body.asJson match {
      case Some(store) =>
        val account = (store \ "account").as[String]
        val name = (store \ "name").as[String]
//        val description = (store \ "description").as[String]
//        val contact = (store \ "contact").as[String]
//        val address = (store \ "address").as[String]
//        val icon = (store \ "icon").as[String]
//        val openFrom = (store \ "open_from").as[String]
//        val openTo = (store \ "open_to").as[String]
//        val basePrice = (store \ "base_price").as[String].toInt
//        val packFee = (store \ "pack_fee").as[String].toInt
        val catId = (store \ "cat_id").as[String].toLong
//        val cost = (store \ "cost_time").as[String].toInt

        val currentTime = System.currentTimeMillis()

        terraClient.create(account).flatMap { json =>
          try {
            val errorCode = (json \ "errCode").asOpt[Int].getOrElse(-1)
            if (errorCode != 0) {
              val msg = (json \ "msg").asOpt[String].getOrElse("")
              logger.error(s"createStore terra server error: $json")
              Future(Ok(jsonResult(errorCode, s"terra:$msg")))
            } else {
              val uid = (json \ "uid").as[Long]
              val isExist = (json \ "isExist").as[Int]

//             storeDAO.exist(uid).flatMap{ exist =>
                if(isExist == 0) {
                  val s = SlickTables.rStores(
                    uid,
                    name,
                    description = "",
                    contact = "",
                    address = "",
                    icon = "",
                    openFrom = 0l,
                    openTo = 1440l,
                    basePrice = 0,
                    packFee = 0,
                    catId,
                    costTime = 60,
                    createTime = currentTime,
                    modifiedTime = currentTime,
                    state = 0
                  )
                  storeDAO.add(s).map {
                    case Success(_) =>
                      logger.info(s"admin store $s created.")
                      Ok(success)
                    case Failure(e) =>
                      logger.error(e.getMessage)
                      Ok(ErrorCode.storeInsertFailed)
                  }
                } else {
                  Future(Ok(ErrorCode.storeExistFailed))
                }
//              }

            }
          } catch {
            case ex: JsResultException =>
              logger.warn("TerraCreateStore return json parse error:" + json)
              Future(Ok(jsonResult(ErrorCode.jsonFormatError, "TerraCreateStore return json parse error")))
          }

        }

      case _ =>
        Future(Ok(ErrorCode.requestAsJsonEmpty))
    }
  }


  def deleteStore(storeId: Long) = systemAdminAction.async {
    for {
      _ <- storeDAO.delete(storeId)
      _ <- categoryDAO.deleteByStoreId(storeId)
      _ <- goodDAO.delete(storeId)
    } yield {
      Ok(success)
    }
  }



  def listStores = systemAdminAction.async { implicit request =>

    for {
      seq <- storeDAO.list
      cats <- categoryDAO.list(adminStoreId)
      cs = cats.map( c => (c.id, c.name)).toMap
    } yield {
      val ls = seq.map { obj =>
        Json.obj(
          "id" -> obj.id,
          "name" -> obj.name,
          "description" -> obj.description,
          "contact" -> obj.contact,
          "address" -> obj.address,
          "icon" -> obj.icon,
          "open_from" -> parseMinutes(obj.openFrom),
          "open_to" -> parseMinutes(obj.openTo),
          "base_price" -> obj.basePrice,
          "pack_fee" -> obj.packFee,
          "cat_id" -> cs.get(obj.catId),
          "sales" -> obj.sales,
          "comments" -> obj.comments,
          "grades" -> obj.grades,
          "cost_time" -> obj.costTime,
          "state" -> obj.state,
          "create_time" -> obj.createTime,
          "modified_time" -> obj.modifiedTime
        )
      }
      val l = Json.toJson(ls)
      Ok(successResult(Json.obj("list" -> l)))
    }
  }

  def listStoreNames = systemAdminAction.async { implicit request =>

    for {
      seq <- storeDAO.listNames
    } yield {
      val l = seq.map  { s => Json.obj("id" -> s._1, "name" -> s._2 )}
      Ok(successResult(Json.obj("list" -> Json.toJson(l))))
    }
  }


  def editStore() = adminAction.async { implicit request =>

    val storeId = request.user.uid
    request.body.asJson match {
      case Some(store) =>

//        val name = (store \ "name").as[String]
        val description = (store \ "description").as[String]
        val contact = (store \ "contact").as[String]

//        val newIcon = (store \ "new_icon").as[String]
        val icon = (store \ "icon").as[String]
        val openFrom = (store \ "open_from").as[String]
        val openTo = (store \ "open_to").as[String]
        val basePrice = (store \ "base_price").as[String].toInt
        val packFee = (store \ "pack_fee").as[String].toInt
//        val catId = (store \ "cat_id").as[String].toLong
        val cost = (store \ "cost_time").as[String].toInt

        storeDAO.getStoreById(storeId).flatMap {
          case Some(st) =>
            val s = SlickTables.rStores(
              storeId,
              st.name,
              description,
              contact,
              st.address,
              icon,
              parseOpenTime(openFrom),
              parseOpenTime(openTo),
              basePrice,
              packFee,
              st.catId,
              st.sales,
              st.comments,
              st.grades,
              cost,
              st.state,
              st.createTime,
              System.currentTimeMillis()
            )
            storeDAO.edit(s).map {
              case Success(_) =>
                logger.info(s"admin store $s edited.")
                Ok(success)
              case Failure(e) =>
                logger.error(e.getMessage)
                Ok(ErrorCode.storeInsertFailed)
            }
          case _ =>
            Future(Ok(ErrorCode.storeNonExisted))
        }
      case _ =>
        Future(Ok(ErrorCode.requestAsJsonEmpty))
    }
  }

  /*超级管理员启用店铺*/
  def enableStore(id: Long) = systemAdminAction.async { implicit request =>
    storeDAO.enable(id).map {
      case Success(_) =>
        logger.info(s"store $id, enabled.")

        Ok(success)
      case Failure(e) =>
        logger.error(e.getMessage)
        Ok(ErrorCode.changeStateFailed)
    }
  }

  /*超级管理员禁用店铺*/
  def disableStore(id: Long) = systemAdminAction.async { implicit request =>
    //        val id = request.user.uid
    storeDAO.disable(id).map {
      case Success(_) =>
        logger.info(s"store $id, disabled.")
        Ok(success)
      case Failure(e) =>
        logger.error(e.getMessage)
        Ok(ErrorCode.changeStateFailed)
    }
  }

  /*超级管理员编辑店铺分类*/
  def editCategory(storeId: Long, cateId: Long) = systemAdminAction.async { implicit request =>
    storeDAO.editCategory(storeId, cateId).map {
      case Success(_) =>
        logger.info(s"store $storeId, edit cateId $cateId.")
        Ok(success)
      case Failure(e) =>
        logger.error(e.getMessage)
        Ok(ErrorCode.storeEditedFailed)
    }
  }


  /*商户停止营业*/
  def closeStore() = adminAction.async { implicit request =>
    val id = request.user.uid
    storeDAO.close(id).map {
      case Success(_) =>
        logger.info(s"store $id, closed.")
        Ok(success)
      case Failure(e) =>
        logger.error(e.getMessage)
        Ok(ErrorCode.changeStateFailed)
    }
  }

  /*商户开始营业*/
  def openStore() = adminAction.async { implicit request =>
    val id = request.user.uid
    storeDAO.open(id).map {
      case Success(_) =>
        logger.info(s"store $id, opened.")
        Ok(success)
      case Failure(e) =>
        logger.error(e.getMessage)
        Ok(ErrorCode.changeStateFailed)
    }
  }



  def information = adminAction.async { implicit  request =>
    for {
      storeOpt <- storeDAO.list(request.user.uid)
      obj = storeOpt.get
      catOpt <- categoryDAO.information(obj.catId)
    } yield {

      val catName = catOpt match {
        case Some(cat) => cat.name
        case _ => ""
      }
      Ok(successResult(Json.obj("data" ->
          Json.obj(
            "id" -> obj.id,
            "name" -> obj.name,
            "description" -> obj.description,
            "contact" -> obj.contact,
            "address" -> obj.address,
            "icon" -> obj.icon,
            "open_from" -> parseMinutes(obj.openFrom),
            "open_to" -> parseMinutes(obj.openTo),
            "base_price" -> obj.basePrice,
            "pack_fee" -> obj.packFee,
            "cat_name" -> catName,
            "sales" -> obj.sales,
            "comments" -> obj.comments,
            "grades" -> obj.grades,
            "cost_time" -> obj.costTime,
            "state" -> obj.state,
            "create_time" -> obj.createTime,
            "modified_time" -> obj.modifiedTime
          )
      ) ))
    }
  }

}
