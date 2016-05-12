package controllers

import _root_.util.CommentClient
import akka.actor.ActorRef
import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}
import models.JsonProtocols
import models.dao.OrderDAO.OrderState
import models.dao.{CommentDAO, OrderDAO}
import org.slf4j.LoggerFactory
import play.api.Logger
import play.api.libs.json.{JsNull, JsValue, JsObject, Json}
import play.api.mvc.{Action, Controller}
import utils.HestiaClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.parsing.json.JSONObject

/**
  * Created by wangchunze on 2016/3/18.
 */
@Singleton
class Comments @Inject()(
                        val hestiaClient: HestiaClient,
                        val commentDAO:CommentDAO,
                        val orderDAO:OrderDAO,
                        actionUtils: ActionUtils,
                        val commentClient:CommentClient,
                        @Named("updateStoreActor") updateStoreActor:ActorRef
                          ) extends Controller with JsonProtocols{
  import actionUtils._
  private val logger = LoggerFactory.getLogger(this.getClass)

  private val userAction = LoggingAction andThen UserAction
  private val storeAction = userAction andThen AdminAction
  private val systemAdminAction = storeAction andThen SystemAdminAction

  val baseUrl = appSettings.terraProtocol + "://" + appSettings.terraDomain
  val appId = appSettings.terraAppId
  private val pageSize=20  //每页显示的数量

  /**
   * 创建评论
 *
    * @return
   */
  def createComment=Action.async { implicit request =>
//    Option("10003") match {
    request.session.get(SessionKey.userId) match {
      case Some(customerId) =>
        val jsonData = Json.obj("orderId" -> 78l, "transTime" -> 40, "dishGrade" -> 3,
          "storeId" -> 100032l, "content" -> "787878asdfghjk是是是",
          "picUrl" -> List("111", "222", "333"), "replyId" -> 0l)
//                Option(jsonData) match {
        request.body.asJson match {
          case Some(comments) =>
            val orderId = (comments \ "orderId").as[Long]
            val transTime = (comments \ "transTime").as[Int]
            val dishGrade = (comments \ "dishGrade").as[Int]
            val storeId = (comments \ "storeId").as[Long]
            val content = (comments \ "content").as[String]
            val picUrl = (comments \ "picUrl").as[List[String]]
            val replyId = (comments \ "replyId").as[Long]
            val createTime = System.currentTimeMillis()

            orderDAO.getOrderById(orderId).flatMap {
              case Some(order) =>
                if (order.state == OrderState.OrderSuccess.id) {
                  orderDAO.getGoodsByOrderId(order.id).flatMap { res =>
                    val goods=res.map{r=>
                      Json.obj(
                        "id"->r._1.goodId,
                        "name"->r._2.name,
                        "num"->r._1.num
                      )
                    }
                    val extendContent=Json.stringify(Json.obj(
                      "goods"->goods,
                      "transTime"->transTime
                    ))
                    val json = Json.obj(
                      "storeId" -> storeId,
                      "grade" -> dishGrade,
                      "content" -> content,
                      "picUrl" -> picUrl.foldLeft("")((res, i) => res + i + "#"),
                      "itemId" -> orderId.toString,
                      "userId" -> customerId.toLong,
                      "extendContent" -> extendContent,
                      "replyId" -> replyId
                    )

                    commentClient.insertComment(json).flatMap { res =>
                      val errCode = (res \ "errCode").as[Int]
                      if (errCode == 0) {
                        commentDAO.createComment(orderId, transTime, dishGrade, storeId, createTime)
                          .map { res =>
                            if (res > 0) {
                              orderDAO.changeState(orderId, OrderState.OrderCommented.id)
                              Ok(success)
                            }
                            else
                              Ok(ErrorCode.commentCreateFailed)
                          }
                      } else {
                        Future.successful(Ok(ErrorCode.commentCreateFailed))
                      }
                    }
                  }
                } else {
                  Future.successful(Ok(ErrorCode.commentCreateFailed))
                }
              case None =>
                Future.successful(Ok(ErrorCode.orderNotExist))
            }

          case None =>
            Future.successful(Ok(ErrorCode.requestAsJsonEmpty))
        }
      case None =>
        Future.successful(Ok(ErrorCode.userNotLogin ++ Json.obj("url" ->
          (baseUrl + "/terra/login?redirect_url=" + java.net.URLEncoder.encode(baseUrl + "/miami/&appid=" + appId, "UTF-8")))))
    }
  }


  /**
   * 删除评论
 *
   * @param commentId
   * @return
   */
  def deleteComment(commentId:Long)=Action.async { implicit request =>
//    Option("10003") match{
    request.session.get(SessionKey.userId) match {
      case Some(customerId) =>
        commentClient.deleteComment(commentId).map{res=>
          val errCode = (res \ "errCode").as[Int]
          if(errCode==0){
            Ok(success)
          }else{
            Ok(res)
          }
        }
      case None =>
        Future.successful(Ok(ErrorCode.userNotLogin ++ Json.obj("url" ->
          (baseUrl + "/terra/login?redirect_url=" + java.net.URLEncoder.encode(baseUrl + "/miami/&appid=" + appId, "UTF-8")))))
    }
  }



  /**
   * 获取评论的数量以及好评中评差评的数量
 *
   * @param storeId
   * @return
   */
  def getCommentNum(storeId:Long)=Action.async{implicit request=>
    commentDAO.getGardeByStoreId(storeId).map{res=>
      val total =res.length
      val good  =res.count(g=>g>=4)
      val bad   =res.count(g=>g<2)
      val middle=total-good-bad
      val data=Json.obj("total"->total,"good"->good,"middle"->middle,"bad"->bad)
      Ok(successResult(Json.obj("data"->data)))
    }
  }


  /**
   * 获取餐厅的评价列表
 *
   * @param storeId 餐厅id
   * @param leval 0:全部  1:好评  2:中评  3:差评
   * @param page  页码
   * @return
   */
  def getCommentsByStore(storeId:Long,leval:Int,page:Option[Int],commentNum:Option[Int])=Action.async{implicit request=>
    val curPage = if (page.getOrElse(1) > 0) page.getOrElse(1) else 1 //当前页码
    val commentNum2=if (commentNum.getOrElse(pageSize) > 0) commentNum.getOrElse(pageSize) else pageSize //当前页码
    commentDAO.getNumByStoreId(storeId, leval).flatMap { cnt => //总记录条数
      val pageCount = cnt / pageSize + (if (cnt % pageSize == 0) 0 else 1) //总页数
      val (gradeB,gradeT)=leval match{
        case 0=>(1,5)
        case 1=>(3,5)
        case 2=>(2,3)
        case 3=>(1,1)
      }
      commentClient.getCommentsByStore(storeId,gradeB,gradeT,curPage,commentNum2).map { res =>
        val errCode = (res \ "errCode").as[Int]
        if (errCode == 0) {
          val result = (res \ "result").as[Seq[JsObject]]
          val data = result.map { msg =>
            val extendContent = (msg \ "extendContent").as[String]

            val (goods,transTime)=scala.util.parsing.json.JSON.parseFull(extendContent) match{
              case Some(e:Map[String,String])=>
                val extendContentJson=Json.parse(extendContent)
                val transTime = (extendContentJson \ "transTime").asOpt[Int].getOrElse(0)
                val goods=(extendContentJson \ "goods").asOpt[Seq[JsObject]].getOrElse(Seq(JsNull))
                (goods,transTime)
              case None=>
                (Seq(JsNull),0)
            }
            val picUrl=(msg \ "picUrl").as[String].split("#")
            (msg ++ Json.obj(
              "transTime" ->transTime,
              "goods" -> goods,
              "picUrl"->picUrl)).-("extendContent")
          }
          Ok(successResult(Json.obj("curPage" -> curPage, "data" -> data)))
        } else {
          Ok(res)
        }
      }
    }
  }

  /**
   * 上传评价图片
 *
   * @return
   */
  def uploadPic()=Action.async{request=>
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

  /*
   *below functions are
   *for super admin and
   *store admin.
  */

  def listComments(storeId: Option[Long], bottom: Option[Int], top: Option[Int], pageNum: Option[Int], pageSize: Option[Int]) = storeAction.async{ implicit request =>

    val storeid = storeId.getOrElse(request.user.uid)
    commentClient.getCommentsByStore(storeid).map{ json =>
      try {
        val errCode = (json \ "errCode").as[Int]
        if(errCode != 0 ) {
          val msg = (json \ "errCode").as[String]
          Ok(jsonResult(errCode, msg))
        } else {
          val num = (json \ "num").as[Int] / pageSize.getOrElse(common.Constants.DEFAULT_SIZE_OF_PAGE)
          val rst = json.as[JsObject] - "num" ++ Json.obj ("num" -> Json.toJson(num + 1))

          Ok(rst)
        }
      } catch {
        case e: Exception =>
          log.warn(s"listComments exception,json format failed.json=$json")
          Ok(jsonResult(ErrorCode.jsonFormatError, "Tcomment listComments json format error"))

      }
    }
  }

  def deleteBadComment(id: Long) = systemAdminAction.async {
    commentClient.deleteComment(id).map { json =>
      try {
        val errCode = (json \ "errCode").as[Int]
        if(errCode != 0 ) {
          val msg = (json \ "errCode").as[String]
          Ok(jsonResult(errCode, msg))
        } else {
          Ok(json)
        }
      } catch {
        case e: Exception =>
          log.warn(s"listComments exception,json format failed.json=$json")
          Ok(jsonResult(ErrorCode.jsonFormatError, "Tcomment listComments json format error"))

      }

    }
  }

  def replyComment(id: Long, orderId: String, content: String) = storeAction.async { implicit  request =>
    val body : JsObject = Json.obj(
      "storeId" -> request.user.uid,
      "grade" -> -1,
      "content" -> content,
      "picUrl" -> "",
      "itemId" -> orderId,
      "userId" -> request.user.uid,
      "extendContent" -> "",
      "replyId" -> id
    )
    commentClient.insertComment(body).map { json =>
      try {
        val errCode = (json \ "errCode").as[Int]
        if(errCode != 0 ) {
          val msg = (json \ "errCode").as[String]
          Ok(jsonResult(errCode, msg))
        } else {
          Ok(json)
        }
      } catch {
        case e: Exception =>
          log.warn(s"replyComment exception,json format failed.json=$json")
          Ok(jsonResult(ErrorCode.jsonFormatError, "Tcomment replyComment json format error"))

      }
    }
  }


}
