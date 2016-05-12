package actor

import actor.OrderManager.{YEAH, CannotFindRefund, OperationSucceed, UpdateStateFailed}
import akka.actor._
import akka.event.Logging
import common.AppSettings
import models.dao.OrderDAO._
import models.dao.RefundDAO.RefundState
import models.dao.{OrderDAO, RefundDAO}
import models.tables.SlickTables
import org.joda.time.Hours

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/**
  * Created by ZYQ on 2016/4/11.
  **/

object OrderHandler {
  def props(
             orderId: Long,
             orderDAO: OrderDAO,
             refundDAO: RefundDAO,
             appSettings: AppSettings
           ) = {
    Props(
      new OrderHandler(
        orderId,
        orderDAO,
        refundDAO,
        appSettings
      )
    )
  }

  case class InitOrder(order: SlickTables.rOrders)

  case class PaySuccess(oId: Long, tradeNo: String)

  case class PayFailure(oId: Long, tradeNo: String)

  case class PayResponse(oId: Long, state: Int, totalFee: Int, tradeNo: String)

  case class ConfirmAcceptOrder(oId: Long, sId: Long)

  case class ConfirmRefuseOrder(oId: Long, sId: Long)

  case class ConfirmShipOrder(oId: Long, sId: Long)

  case class ConfirmDishArrived(oId: Long, cId: Long)

  case class ApplyRefund(oId: Long, cId: Long, msg: String)

  case class AcceptRefund(oId: Long, rId: Long, sId: Long)

  case class RefuseRefund(oId: Long, rId: Long, sId: Long, msg: String)

  case class FinishRefund(oId: Long, rId: Long)

  object CancelledOrder extends ActorResponse


}

class OrderHandler(
                    orderId: Long,
                    orderDAO: OrderDAO,
                    refundDAO: RefundDAO,
                    appSettings: AppSettings
                  ) extends Actor with Stash {

  val log = Logging(context.system, this)

  import OrderHandler._

  import scala.concurrent.duration._

  private[this] var targetOrder: SlickTables.rOrders = _
  private[this] var targetRefund: SlickTables.rRefunds = _
  private[this] var customerPeer: Option[ActorRef] = None


  val payTimeoutInMinutes = appSettings.payTimeoutInMinutes
  val storeConfirmAcceptTimeInMinutes = appSettings.storeConfirmAcceptTimeInMinutes
  val dishReceiveWaitingTimeInHour = appSettings.dishReceiveWaitingTimeInHour
  val customerConfirmReceivedTimeoutInHour = appSettings.customerConfirmReceivedTimeoutInHour
  // TODO
  val refundApplyTimeoutInHour = appSettings.customerConfirmReceivedTimeoutInHour


  log.debug(s"order handler created: $orderId")


  override def receive: Receive = waitingInit

  //  override def receive: Receive = waitingCustomerConfirmed
  //  override def receive: Receive = refunding


  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    orderDAO.find(orderId).map {
      case Some(order) =>
        self ! InitOrder(order)
      case None =>
        log.error(s"Can not find the order $orderId to init.")
        orderFailed(CancelledOrder, OrderState.OrderCanceled)
        context.stop(self)
    }
  }

  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    log.debug(s"$orderId actor stopping...")
  }

  private[this] def orderFailed(
                                 errorRsp: ActorResponse,
                                 lastOrderState: OrderState.OrderState) = {
    log.warning(s"orderFailed $targetOrder for $errorRsp state to $lastOrderState")
    orderDAO.updateState(orderId, lastOrderState).andThen {
      case _ =>
        context.stop(self)
    }
  }

  private[this] def payFailed(
                                 errorRsp: ActorResponse,
                                 lastOrderState: OrderState.OrderState,
                                 lastTradeNo: String) = {
    log.warning(s"payFailed $targetOrder for $errorRsp state to $lastOrderState")
    orderDAO.updateStateAndTradeNo(orderId, lastOrderState, lastTradeNo).andThen {
      case _ =>
        context.stop(self)
    }
  }

  private[this] def refundFailed(
                                  errorRsp: ActorResponse,
                                  refundId: Long,
                                  lastOrderState: RefundState.RefundState,
                                  msg: String = "") = {
    log.warning(s"orderFailed $targetOrder for $errorRsp state to $lastOrderState")
    refundDAO.processRefund(refundId, lastOrderState, msg).andThen {
      case _ =>
        context.stop(self)
    }
  }


  private[this] def waitingInit: Receive = {

    case InitOrder(order) =>
      log.debug(s"init order $order")
      this.targetOrder = order
      unstashAll()
      import OrderState._
      OrderState(order.state) match {
        case r@WaitingPay =>
          log.debug(s"init order handler in WaitingPay $r.")
          context.become(waitingPay)
          context.setReceiveTimeout(payTimeoutInMinutes minutes)

        //TODO terra check for isPaid if true  self ! ProcessSuccess
        //TODO  if false

        case r@WaitingAccept =>
          log.debug(s"init order handler  in WaitingAccept $r.")
          context.setReceiveTimeout(storeConfirmAcceptTimeInMinutes minutes)
          context.become(waitingAccept)

        case x@WaitingShipping =>
          log.debug(s"init order handler in WaitingShipping $x.")
          //          context.setReceiveTimeout(dishReceiveWaitingTimeInHour hours)
          context.become(waitingShipping)

        case x@ConfirmDelivered =>
          log.debug(s"init order handler in ConfirmDelivered $x.")
          context.setReceiveTimeout(customerConfirmReceivedTimeoutInHour hours)
          context.become(waitingCustomerConfirmed)

        case x@ApplyRefund =>
          //TODO
//          val peer = sender()
          log.debug(s"InitOrder got $x")
          refundDAO.getRefundByOrderId(orderId).map {
            case Some(refund) =>
              this.targetRefund = refund
              context.become(refunding)
            case None =>
              log.error(s"can not find refund by oId: [$orderId]")
//              peer ! CannotFindRefund(orderId)
          }
//          this.targetOrder = order
          log.debug(s"InitOrder init order handler in ApplyRefund $x.")
          context.become(refunding)

        case OrderSuccess | OrderCommented =>
          context.stop(self)

        case PayFailure =>
          context.stop(self)

        case OrderCanceled =>
          context.stop(self)

        case AcceptFailure =>
          context.stop(self)


      }

    case  x => log.info("waitingInit received unknown message $x")

  }


  private[this] def waitingPay: Receive = {
    case x@PaySuccess(oId, tradeNo) =>
      val peer = sender()
      context.setReceiveTimeout(storeConfirmAcceptTimeInMinutes minutes)
      context.become(waitingAccept)
      orderDAO.updateStateAndTradeNo(oId, OrderState.WaitingAccept, tradeNo).onSuccess {
        case true =>
          peer ! OperationSucceed
        case false =>
          peer ! UpdateStateFailed
          log.error(s"update $oId's state  to WaitingAccept failed.")
          payFailed(ActorOperateInternalError, OrderState.WaitingAccept, tradeNo)
      }

    case x@PayFailure(oId, tradeNo) =>
      val peer = sender()
      orderDAO.updateStateAndTradeNo(oId, OrderState.PayFailure, tradeNo).onSuccess {
        case true =>
          peer ! OperationSucceed
          context.stop(self)
        case false =>
          peer ! UpdateStateFailed
          log.error(s"update $oId's state  to PayFailure failed.")
          payFailed(ActorOperateInternalError, OrderState.PayFailure, tradeNo)
      }

    case ReceiveTimeout =>
      orderFailed(CancelledOrder, OrderState.OrderCanceled)
      log.warning(s"waitingPay timeout:[$orderId].")

    case _ =>
  }

  private[this] def waitingAccept: Receive = {

    case x@ConfirmAcceptOrder(oId, cId) =>

      log.info(s"ConfirmAcceptOrder")
      context.setReceiveTimeout(dishReceiveWaitingTimeInHour hour)
      context.become(waitingShipping)
      val peer = sender()
      orderDAO.updateState(oId, OrderState.WaitingShipping).onSuccess {
        case true =>
          peer ! OperationSucceed
        case false =>
          peer ! UpdateStateFailed
          log.error(s"update $oId's state  to waitingShipping failed.")
          orderFailed(ActorOperateInternalError, OrderState.WaitingShipping)
      }

    case x@ConfirmRefuseOrder(oId, cId) =>
      val peer = sender()
      orderDAO.updateState(oId, OrderState.AcceptFailure).onSuccess {
        case true =>
          peer ! OperationSucceed
          context.stop(self)
        case false =>
          peer ! UpdateStateFailed
          log.error(s"update $oId's state  to AcceptFailure failed.")
          orderFailed(ActorOperateInternalError, OrderState.AcceptFailure)
      }

    case x@ApplyRefund(oId, cId, msg) =>
      val peer = sender()
      context.setReceiveTimeout(refundApplyTimeoutInHour hour)
      context.become(refunding)
      self.tell(x, peer)


    case ReceiveTimeout =>
      //TODO 退款

      orderFailed(CancelledOrder, OrderState.OrderCanceled)
      log.warning(s"waitingAccept timeout:[$orderId].")

    case _ =>
      val peer = sender()
      peer ! YEAH
  }

  private[this] def waitingShipping: Receive = {

    case x@ConfirmShipOrder(oId, sId) =>
      context.setReceiveTimeout(customerConfirmReceivedTimeoutInHour hours)
      context.become(waitingCustomerConfirmed)
      val peer = sender()

      orderDAO.updateState(oId, OrderState.ConfirmDelivered).onSuccess {
        case true =>
          //TODO 定时任务，如果时间长没确认收货，就自动订单完成
          peer ! OperationSucceed
        case false =>
          peer ! UpdateStateFailed
          log.error(s"update $oId's state  to ConfirmDelivered failed.")
          //          sender() ! Failed(s"update $oId's state  to ConfirmDelivered failed.")
          orderFailed(ActorOperateInternalError, OrderState.ConfirmDelivered)
      }


    case x@ApplyRefund(oId, cId, msg) =>
      val peer = sender()
      context.setReceiveTimeout(refundApplyTimeoutInHour hour)
      context.become(refunding)
      self.tell(x, peer)


    case ReceiveTimeout =>
      //TODO 退款
      orderFailed(CancelledOrder, OrderState.OrderCanceled)
      log.warning(s"waitingShipping timeout:[$orderId].")

    case _ =>
  }

  private[this] def waitingCustomerConfirmed: Receive = {
    case x@ConfirmDishArrived(oId, sId) =>
      val peer = sender()
      orderDAO.updateState(oId, OrderState.OrderSuccess).onSuccess {
        case true =>
          peer ! OperationSucceed
        case false =>
          peer ! UpdateStateFailed
          log.error(s"update $oId's state  to OrderSuccess failed.")
          //          sender() ! Failed(s"update $oId's state  to OrderSuccess failed.")
          orderFailed(ActorOperateInternalError, OrderState.OrderSuccess)
      }

    case x@ApplyRefund(oId, cId, msg) =>
      val peer = sender()
      context.setReceiveTimeout(refundApplyTimeoutInHour hour)
      context.become(refunding)
      self.tell(x, peer)


    case ReceiveTimeout =>
      //TODO 退款
      log.warning(s"WaitingConfirmDishArrived timeout:[$orderId].")

      orderDAO.updateState(orderId, OrderState.OrderSuccess).onSuccess {
        case false =>
          log.error(s"update $orderId's state to OrderSuccess failed.")
          orderFailed(ActorOperateInternalError, OrderState.OrderSuccess)
      }


    case _ =>
  }

  private[this] def refunding: Receive = {
    case x@ApplyRefund(oId, cId, msg) =>
      val peer = sender()
      orderDAO.getOrderById(oId).map {
        case Some(order) =>
          val refund = SlickTables.rRefunds(
            -1l,
            oId,
            order.totalFee,
            msg,
            storeDesp = "",
            state = RefundState.RefundApply.id,
            timestamp = System.currentTimeMillis()
          )
          refundDAO.createRefunds(refund).map {
            case Success(_) =>
              orderDAO.updateState(oId, OrderState.ApplyRefund).onSuccess {
                case true =>
                  log.info(s"create refund successful...orderid=$oId")
                  peer ! OperationSucceed
                case false =>
                  peer ! UpdateStateFailed
              }
            case Failure(e) =>
              peer ! UpdateStateFailed
          }
        case None =>
          peer ! UpdateStateFailed
      }

    case x@AcceptRefund(oId, rId, sId) =>
      val peer = sender()
      log.debug(s"refunding got $x")
      refundDAO.processRefund(rId, RefundState.RefundAccept).onSuccess {
        case true =>
          peer ! OperationSucceed
        case false =>
          peer ! UpdateStateFailed
          log.error(s"update $oId's refund $rId state to RefundAccept failed.")
          refundFailed(ActorOperateInternalError, rId, RefundState.RefundAccept)
      }
    //TODO 开始退款 退款成功后记录退款完成

    case RefuseRefund(oId, rId, sId, msg) =>

      val peer = sender()

      refundDAO.processRefund(rId, RefundState.RefundRefuse).onSuccess {
        case true =>
          peer ! OperationSucceed
        case false =>
          peer ! UpdateStateFailed
          log.error(s"update $oId's refund state to RefundAccept failed.")
          refundFailed(ActorOperateInternalError, rId, RefundState.RefundFinish, msg)
      }

    case FinishRefund(oId, rId) =>
      context.stop(self)
    //TODO 退款完成

    case ReceiveTimeout =>

    case _ =>
  }


}
