package controllers

import actor.Ok
import actor.OrderHandler._
import actor.OrderManager._
import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}
import common.Constants._
import models.JsonProtocols
import models.dao.{RefundDAO, GoodDAO, OrderDAO}
import org.slf4j.LoggerFactory
import play.api.libs.json.Json
import play.api.mvc.Controller
/**
 * Created by ZYQ on 2016/3/16.
 **/
@Singleton
class Orders @Inject()(
                         orderDAO: OrderDAO,
                         goodDAO: GoodDAO,
                         refundDAO: RefundDAO,
                         @Named("configured-OrderManager") orderManager: ActorRef,
                         actionUtils: ActionUtils
                             ) extends Controller with JsonProtocols{

  private val logger = LoggerFactory.getLogger(this.getClass)

  import actionUtils._

  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.duration._

  private val userAction = LoggingAction andThen UserAction
  private val storeAction = userAction andThen AdminAction
  private val adminAction = userAction andThen SystemAdminAction

  implicit val timeout = new Timeout(30 seconds)


  def listOrders(state: Option[Int],
                 pageNum: Int,
                 pageSize: Int = DEFAULT_SIZE_OF_PAGE
                ) = storeAction.async { implicit request =>


    val storeId = request.user.uid

    for{
      (list, num) <- orderDAO.list(storeId, state, pageNum, pageSize)
      refunds <- refundDAO.listRefunds(list.map(_.id))
      orderGoods <- orderDAO.listOrderGoods(list.map(_.id))
      goodMap <- goodDAO.listGoodNames(orderGoods.map(_.goodId).distinct)
    } yield {

      val orderGoodMap = orderGoods.groupBy(_.orderId)
      val refundMap = refunds.groupBy(_.orderId)


      val ls = list.map{ obj =>
        val goods = orderGoodMap.getOrElse(obj.id, Nil).map{ g =>
          Json.obj(
            "good_name" -> Json.toJson(goodMap.getOrElse(g.goodId, "")),
            "good_num" -> g.num
          )
        }

        val refundJ = refundMap.getOrElse(obj.id, Nil).map{ r =>
          Json.obj(
            "refund_id" -> r.id,
            "status" -> RefundDAO.stateStr(r),
            "customer_desp" -> r.customerDesp,
            "store_desp" -> r.storeDesp,
            "amount" -> r.amount,
            "timestamp" -> r.timestamp
          )
        }

        Json.obj(
          "id" -> obj.id,
          "store_id" -> obj.storeId,
          "customer_id" -> obj.customerId,
          "recipient" -> obj.recipient,
          "address" -> obj.address,
          "contact" -> obj.contact,
          "remark" -> obj.remark,
          "pack_fee" -> obj.packFee,
          "total_fee" -> obj.totalFee,
          "pay_status"->obj.payStatus,
          "state" -> OrderDAO.stateStr(obj),
          "trade_no" -> obj.tradeNo,
          "arrive_time" -> obj.arriveTime,
          "create_time" -> obj.createTime,
          "goods" -> Json.toJson(goods),
          "refunds" -> Json.toJson(refundJ)
        )

      }
      Ok(successResult(Json.obj("list" -> ls, "num" -> Json.toJson(num / pageSize + 1))))
    }

  }

  def acceptOrder(id: Long) = storeAction.async { implicit request =>

    val storeId = request.user.uid
    log.debug("acceptOrder begin asking.")
    (orderManager ? ConfirmAcceptOrder(id, storeId)).map {
      case OperationSucceed => Ok(success)
      case UpdateStateFailed => Ok(ErrorCode.changeStateFailed)
      case CustomerIdMismatch => Ok(ErrorCode.customerIdMismatch)
      case OperationTimeout => Ok(ErrorCode.operationTimeOut)
      case CannotFindOrder(_) => Ok(ErrorCode.orderNotExist)
      case x => logger.info(s"unknown message: $x"); Ok(ErrorCode.unknownMessage)
    }
//    orderDAO.accept(id).map {
//      case Success(_) =>
//        logger.info(s"order $id, accepted.")
//        Ok(success)
//      case Failure(e) =>
//        logger.error(e.getMessage)
//        Ok(ErrorCode.changeStateFailed)
//    }

  }


  def refuseOrder(id: Long) = storeAction.async { implicit request =>

    val storeId = request.user.uid
    (orderManager ? ConfirmRefuseOrder(id, storeId)).map {
      case OperationSucceed => Ok(success)
      case UpdateStateFailed => Ok(ErrorCode.changeStateFailed)
      case CustomerIdMismatch => Ok(ErrorCode.customerIdMismatch)
      case OperationTimeout => Ok(ErrorCode.operationTimeOut)
      case CannotFindOrder(_) => Ok(ErrorCode.orderNotExist)
      case x => println(s"unknown message: $x"); Ok(ErrorCode.unknownMessage)
    }

//    orderDAO.refuse(id).map {
//      case Success(_) =>
//        logger.info(s"order $id, refused.")
//        Ok(success)
//      case Failure(e) =>
//        logger.error(e.getMessage)
//        Ok(ErrorCode.changeStateFailed)
//    }
  }


  def deliverOrder(id: Long) = storeAction.async { implicit request =>
    val storeId = request.user.uid
    (orderManager ? ConfirmShipOrder(id, storeId)).map {
      case OperationSucceed => Ok(success)
      case UpdateStateFailed => Ok(ErrorCode.changeStateFailed)
      case CustomerIdMismatch => Ok(ErrorCode.customerIdMismatch)
      case OperationTimeout => Ok(ErrorCode.operationTimeOut)
      case CannotFindOrder(_) => Ok(ErrorCode.orderNotExist)
      case x => println(s"unknown message: $x"); Ok(ErrorCode.unknownMessage)
    }
//    orderDAO.deliver(id).map {
//      case Success(_) =>
//        logger.info(s"order $id, delivered.")
//        Ok(success)
//      case Failure(e) =>
//        logger.error(e.getMessage)
//        Ok(ErrorCode.changeStateFailed)
//    }
  }


  def listSpans = storeAction.async { implicit request =>

    val storeId = request.user.uid
    for {
      unAccepts <- orderDAO.spans(storeId, 1)
      unArrived <- orderDAO.spans(storeId, 2)
      applyRefund <- orderDAO.spans(storeId,9)
    } yield {
      Ok(successResult(Json.obj("data" ->
        Json.obj(
          "unAccepts" -> unAccepts,
          "unArrived" -> unArrived,
          "applyRefund" -> applyRefund
        ))))
    }

  }


  def acceptRefund(orderId: Long, refundId: Long) = storeAction.async { implicit request =>

    val storeId = request.user.uid
    (orderManager ? AcceptRefund(orderId, refundId, storeId)).map {
      case OperationSucceed => Ok(success)
      case UpdateStateFailed => Ok(ErrorCode.changeStateFailed)
      case CustomerIdMismatch => Ok(ErrorCode.customerIdMismatch)
      case OperationTimeout => Ok(ErrorCode.operationTimeOut)
      case CannotFindOrder(_) => Ok(ErrorCode.orderNotExist)
      case x => logger.info(s"unknown message: $x"); Ok(ErrorCode.unknownMessage)
    }
  }


  def refuseRefund(orderId: Long, refundId: Long, msg: String) = storeAction.async { implicit request =>

    val storeId = request.user.uid
    (orderManager ? RefuseRefund(orderId, refundId, storeId, msg)).map {
      case OperationSucceed => Ok(success)
      case UpdateStateFailed => Ok(ErrorCode.changeStateFailed)
      case CustomerIdMismatch => Ok(ErrorCode.customerIdMismatch)
      case OperationTimeout => Ok(ErrorCode.operationTimeOut)
      case CannotFindOrder(_) => Ok(ErrorCode.orderNotExist)
      case x => println(s"unknown message: $x"); Ok(ErrorCode.unknownMessage)
    }

  }


}
