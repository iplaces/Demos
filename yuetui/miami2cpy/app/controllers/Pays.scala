package controllers

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}
import models.JsonProtocols
import models.dao.OrderDAO
import models.dao.OrderDAO.OrderState
import org.slf4j.LoggerFactory
import play.api.mvc.{Action, Controller}
import utils.MammonClient

import scala.concurrent.Future

/**
  * Created by ZYQ on 2016/4/14.
  **/
object Pays {
  object PayState extends Enumeration {
    type PayState = Value
    val WaitingPay = Value(0)
    val PaySuccess = Value(1)
    val Refunded = Value(2)
    val PayCanceled = Value(-1)
  }
}

@Singleton
class Pays @Inject()(
                    orderDAO: OrderDAO,
                    actionUtils: ActionUtils,
                    mammonClient: MammonClient,
                    @Named("configured-OrderManager") orderManager: ActorRef
                    ) extends Controller with JsonProtocols{

  import actionUtils._
  import actor.OrderHandler._
  import play.api.libs.concurrent.Execution.Implicits.defaultContext

  import scala.concurrent.duration._
  val log = LoggerFactory.getLogger(this.getClass)

  private val userAction = LoggingAction andThen UserAction

  private val orderListPage = "/miami/#/order/list"

  implicit val timeout = new Timeout(30 seconds)

  def payResultAsync(
    appId: String,
    signType: String,
    mammonTradeNo: String,
    tradeNo: Long,
    state: Int,
    totalFee: Int,
    sign: String
    ) = checkBasicSignature(
    List(
      appId,
      signType,
      mammonTradeNo,
      tradeNo.toString,
      state.toString,
      totalFee.toString
    ),
    if (mammonClient.appId == appId) sign else "",
    mammonClient.secureKey
  ) {
    Action.async { request =>
      log.debug("I AM IN payResultAsync")

      orderDAO.find(tradeNo).flatMap {
        case Some(order) =>
          if (order.state == OrderState.PayFailure.id
            || order.state == OrderState.WaitingAccept.id
            || order.state == OrderState.OrderCanceled.id) {
            Future.successful(Ok(ErrorCode.operationTimeOut))
          } else if (order.totalFee != totalFee) {
            Future(Ok(ErrorCode.payAmountMismatch))
          } else {
            (orderManager ? PayResponse(tradeNo, state, totalFee, mammonTradeNo))
              .map { _ => Ok(success)}
          }
        case None =>
          Future(Ok(ErrorCode.orderNotExist))
      }

//      (orderManager ? PayResponse(tradeNo, state, totalFee, mammonTradeNo.toLong)).map {
//        case OperationSucceed => Ok(success)
//        case UpdateStateFailed => Ok(ErrorCode.changeStateFailed)
//        case CustomerIdMismatch => Ok(ErrorCode.customerIdMismatch)
//        case OperationTimeout => Ok(ErrorCode.operationTimeOut)
//        case CannotFindOrder(_) => Ok(ErrorCode.orderNotExist)
//        case x@_ => println(s"unknown message: $x"); Ok(ErrorCode.unknownMessage)
//      }

    }
  }


  def payResultSync(
    appId: String,
    signType: String,
    mammonTradeNo: String,
    tradeNo: Long,
    state: Int,
    totalFee: Int,
    sign: String
                   ) =  checkBasicSignature(
    List(
      appId,
      signType,
      mammonTradeNo,
      tradeNo.toString,
      state.toString,
      totalFee.toString
    ),
    if (mammonClient.appId == appId) sign else "",
    mammonClient.secureKey
  ) {
    userAction { request =>

      log.debug("I AM IN payResultSync")
      orderDAO.find(tradeNo).map {
        case Some(order) =>
          if (order.state == OrderState.WaitingPay.id && order.totalFee == totalFee) {
            orderManager ! PayResponse(tradeNo, state, totalFee, mammonTradeNo)
          }
        case None =>
      }
      Redirect(orderListPage)

    }
  }


}
