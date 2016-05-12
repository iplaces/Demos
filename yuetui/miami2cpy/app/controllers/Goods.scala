package controllers

import com.google.inject.{Inject, Singleton}
import models.JsonProtocols
import models.dao.{CategoryDAO, GoodDAO}
import models.tables.SlickTables
import org.slf4j.LoggerFactory
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * Created by ZYQ on 2016/3/10.
  **/
@Singleton
class Goods @Inject()(
                       goodDAO: GoodDAO,
                       categoryDAO: CategoryDAO,
                       actionUtils: ActionUtils
                     ) extends Controller with JsonProtocols {

  private val logger = LoggerFactory.getLogger(this.getClass)


  import actionUtils._

  private val userAction = LoggingAction andThen UserAction
  private val storeAction = userAction andThen AdminAction
  private val adminAction = userAction andThen SystemAdminAction


  def addGood() = storeAction.async { implicit request =>
    val storeId = request.user.uid
//    val storeId =  200l

    println(request.body)
    request.body.asJson match {
      case Some(good) =>
        val catId = (good \ "cat_id").as[String].toLong
        val name = (good \ "name").as[String]
        val price = (good \ "price").as[String].toInt
        val salePrice = (good \ "sale_price").as[String].toInt
        val description = (good \ "description").as[String]
        val icon = (good \ "icon").as[String]
        val stock = (good \ "stock").as[String].toInt
        val g = SlickTables.rGoods(
          -1l,
          storeId,
          catId,
          name,
          price,
          salePrice,
          description,
          icon,
          stock,
          createTime = System.currentTimeMillis()
        )
        goodDAO.add(g).map {
          case Success(_) =>
            logger.info(s"store $storeId, good $g created.")
            Ok(success)
          case Failure(e) =>
            logger.error(e.getMessage)
            Ok(ErrorCode.goodInsertFailed)
        }
      case _ =>
        Future(Ok(ErrorCode.requestAsJsonEmpty))
    }
  }


  def deleteGood(id: Long) = storeAction.async { implicit request =>
    val storeId = request.user.uid
//    val storeId =  200l

    goodDAO.delete(id).map {
      case Success(_) =>
        logger.info(s"store $storeId, good $id deleted.")
        Ok(success)
      case Failure(e) =>
        logger.error(e.getMessage)
        Ok(ErrorCode.goodDeleteFailed)
    }
  }

  /*上架商品*/
  def upGood(id: Long) = storeAction.async { implicit request =>
    val storeId = request.user.uid
//    val storeId =  200l

    goodDAO.up(id).map {
      case Success(_) =>
        logger.info(s"store $storeId, good $id uped.")
        Ok(success)
      case Failure(e) =>
        logger.error(e.getMessage)
        Ok(ErrorCode.goodUpFailed)
    }
  }

  /*下架商品*/
  def offGood(id: Long) = storeAction.async { implicit request =>
    val storeId = request.user.uid
//    val storeId =  200l

    goodDAO.off(id).map {
      case Success(_) =>
        logger.info(s"store $storeId, good $id offed.")
        Ok(success)
      case Failure(e) =>
        logger.error(e.getMessage)
        Ok(ErrorCode.goodOffFailed)
    }
  }

  def editGood(goodId: Long) = storeAction.async { implicit request =>
    val storeId = request.user.uid
//    val storeId =  200l
    try {
      request.body.asJson match {
        case Some(good) =>
          val catId = (good \ "cat_id").as[String].toLong
          val name = (good \ "name").as[String]
          val price = (good \ "price").as[String].toInt
          val salePrice = (good \ "sale_price").as[String].toInt
          val description = (good \ "description").as[String]
          val icon = (good \ "icon").as[String]
          val stock = (good \ "stock").as[String].toInt
          val g = SlickTables.rGoods(
            -1l,
            storeId,
            catId,
            name,
            price,
            salePrice,
            description,
            icon,
            stock,
            createTime = System.currentTimeMillis()
          )
          for {
            _ <- goodDAO.delete(goodId)
            _ <- goodDAO.add(g)
          } yield {
            logger.info(s"store $storeId, good $g edited.")
            Ok(success)
          }
        case _ =>
          Future(Ok(ErrorCode.requestAsJsonEmpty))
      }
    } catch {
      case e: Exception =>
        logger.error(e.getMessage)
        Future(Ok(ErrorCode.goodEditFailed))

    }

  }


  def listGoods = storeAction.async { implicit request =>
    val storeId = request.user.uid
    for {
      seq <- goodDAO.listByStoreId(storeId)
      cats <- categoryDAO.list(storeId)
      cs = cats.map( c => (c.id, c.name)).toMap
    } yield {
      val ls = seq.map { obj =>
        Json.obj(
          "id" -> obj.id,
          "store_id" -> obj.storeId,
          "cat_id" -> obj.catId,
          "cat_name" -> cs.get(obj.catId),
          "name" -> obj.name,
          "price" -> obj.price,
          "sale_price" -> obj.salePrice,
          "description" -> obj.description,
          "icon" -> obj.icon,
          "stock" -> obj.stock,
          "sales" -> obj.sales,
          "state" -> obj.state,
          "create_time" -> obj.createTime
        )
      }
      val l = Json.toJson(ls)
      Ok(successResult(Json.obj("list" -> l)))
    }
  }

  def addStock(id: Long, num: Int) = storeAction.async { implicit request =>

    for {
      good <- goodDAO.list(id)
      n = good.stock + num
      _ <- goodDAO.edit(good.copy(stock = n))
    } yield {
      Ok(success)
    }

  }

}
