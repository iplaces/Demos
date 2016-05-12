package controllers

import com.google.inject.{Inject, Singleton}
import models.JsonProtocols
import models.dao.CategoryDAO._
import models.dao.{CategoryDAO, GoodDAO}
import models.tables.SlickTables
import org.slf4j.LoggerFactory
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
 * Created by ZYQ on 2016/3/10.
 **/
@Singleton
class Categories @Inject()(
                           categoryDAO: CategoryDAO,
                           goodDAO: GoodDAO,
                           actionUtils: ActionUtils
                             ) extends Controller with JsonProtocols{

  private val logger = LoggerFactory.getLogger(this.getClass)

  import actionUtils._

  private val userAction = LoggingAction andThen UserAction
  private val storeAction = userAction andThen AdminAction
  private val adminAction = userAction andThen SystemAdminAction

  def isAdmin(userType: Int) = userType == 1

  def addCategory() = storeAction.async { implicit request =>
    val userId =  request.user.uid

    val storeId = if(isAdmin(request.user.userType)) adminStoreId else userId

    request.body.asJson match {
      case Some(category) =>
        val name = (category \ "name").as[String]
        val icon = (category \ "icon").as[String]
        val rank = (category \ "rank").as[String].toInt
        val c = SlickTables.rCategories(
          -1l,
          name,
          icon,
          storeId,
          rank
        )
        categoryDAO.add(c).map {
          case Success(_) =>
            logger.info(s"storeId $storeId, category $c created.")
            Ok(success)
          case Failure(e) =>
            logger.error(e.getMessage)
            Ok(ErrorCode.categoryInsertFailed)
        }
      case _ =>
        Future(Ok(ErrorCode.requestAsJsonEmpty))
    }
  }

  def deleteCategory(id: Long, isForce: Boolean = false) = storeAction.async { implicit request =>
    val userId =  request.user.uid


    val storeId = if (isAdmin(request.user.userType)) adminStoreId else userId

    goodDAO.listByCatId(id).flatMap { list =>

      if (isForce) {
        for {
          _ <- goodDAO.updateCatIds(list)
          _ <- categoryDAO.delete(id)
        } yield {
          Ok(success)
        }
      } else if (list.isEmpty) {
        categoryDAO.delete(id).map {
          case Success(_) =>
            logger.info(s"storeId $storeId, category $id deleted.")
            Ok(success)
          case Failure(e) =>
            logger.error(e.getMessage)
            Ok(ErrorCode.categoryDeleteFailed)
        }

      } else
        Future(Ok(ErrorCode.categoryExistGoods))
    }
  }

  def editCategory(id: Long) = storeAction.async { implicit request =>
    val storeId =  request.user.uid

    request.body.asJson match {
      case Some(category) =>
        val name = (category \ "name").as[String]
        val icon = (category \ "icon").as[String]
        val rank = (category \ "rank").as[String].toInt

        categoryDAO.edit(SlickTables.rCategories(
          id,
          name,
          icon,
          storeId,
          rank
        )).map {
          case Success(_) =>
            Ok(success)
          case Failure(e) =>
            logger.error(e.getMessage)
            Ok(ErrorCode.categoryInsertFailed)
        }
      case _ =>
        Future(Ok(ErrorCode.requestAsJsonEmpty))
    }
  }

  def listCategories = storeAction.async { implicit request =>

    val userId =  request.user.uid

    val storeId = if(isAdmin(request.user.userType)) adminStoreId else userId

    categoryDAO.list(storeId).map{ seq =>
      val ls = Json.toJson(seq)
      Ok(successResult(Json.obj("list" -> ls)))
    }
  }

}
