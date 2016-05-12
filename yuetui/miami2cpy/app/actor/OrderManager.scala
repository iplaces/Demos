package actor

import akka.actor.Actor
import akka.event.Logging
import com.google.inject.{Inject, Singleton}
import common.AppSettings
import controllers.Pays._
import models.dao.OrderDAO._
import models.dao.{OrderDAO, RefundDAO}
import models.tables.SlickTables
import play.api.libs.concurrent.InjectedActorSupport

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by ZYQ on 2016/4/11.
  **/
object OrderManager {

  trait OrderRequest

  case class RecoverOrder(order: SlickTables.rOrders) extends OrderRequest

  case class PlaceOrder(order: SlickTables.rOrders, goodsList: List[(Long, Int)]) extends OrderRequest


  trait OrderActorResponse extends ActorResponse

  case object OperationSucceed extends OrderActorResponse

  case class OrderCreated(oId: Long) extends OrderActorResponse

  case object OrderCreateFailed extends OrderActorResponse

  case object CustomerIdMismatch extends OrderActorResponse

  case object StoreIdMismatch extends OrderActorResponse

  case object OperationTimeout extends OrderActorResponse

  case object UpdateStateFailed extends OrderActorResponse

  case object PayAmountMismatch extends OrderActorResponse

  case object YEAH extends OrderActorResponse

  case class CannotFindCustomer(cId: Long) extends OrderActorResponse

  case class CannotFindOrder(oId: Long) extends OrderActorResponse

  case class CannotFindRefund(oId: Long) extends OrderActorResponse

}

@Singleton
class OrderManager @Inject()(
                              orderDAO: OrderDAO,
                              refundDAO: RefundDAO,
                              appSettings: AppSettings
                            ) extends Actor with InjectedActorSupport {

  val log = Logging(context.system, this)

  import OrderHandler._
  import OrderManager._

  override def preStart(): Unit = {
    // Initialize children here
    log.debug(s"OrderManager actor starting...")

    startRecoveringOrders()
  }

  private[this] def startRecoveringOrders() = {
    import java.util.Date
    val recoverTimeRangeInMinutes = appSettings.orderRecoverTimeRangeInMinutes
    val end = System.currentTimeMillis()
    val begin = end - (recoverTimeRangeInMinutes * 60 * 1000)
    orderDAO.listNeedRecoveryOrderByTime(begin, end).map { orders =>
      val msg = orders.map(o =>
        s"order[${o.id}] cId=${o.customerId} state=${OrderState(o.state)} time=${new Date(o.createTime)}"
      ).mkString("\n")
      log.info(s"${orders.length} orders needed to recovered: \n $msg")
      orders.foreach(o => self ! RecoverOrder(o))
    }
  }

  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    log.debug(s"OrderManager actor stopping...")
  }


  override def receive: Receive = {
    case r@RecoverOrder(order) =>
      log.debug(s"got $r")
      getOrderHandler(order.id)

    case x@PlaceOrder(order, goodsList) =>
      //      val orderId=order.id
      val peer = sender()
      orderDAO.createOrder(order).map { orderId =>
        if (orderId > 0) {
          Future.sequence(goodsList.map { goods => //创建订单中的菜品详情
            orderDAO.createOrderGoods(orderId, goods._1, goods._2)
          }).map { res =>
            if (res.contains(0L)) {
              //创建菜品详情存在失败
              peer ! OrderCreateFailed
            } else {
              //创建成功
              orderDAO.decreaseStock(orderId) //减少库存
              peer ! OrderCreated(orderId)
              getOrderHandler(orderId)
            }
          }
        } else {
          peer ! OrderCreateFailed
        }
      }

    case x@PayResponse(oId, state, totalFee, tradeNo) =>
      log.debug(s"got $x")
      val peer = sender()
      try {

        PayState(state) match {
          case PayState.PayCanceled =>
            log.debug(s"$x Pay Failure")
            getOrderHandler(oId).tell(PayFailure(oId, tradeNo), peer)
          case PayState.PaySuccess =>
            log.debug(s"$x Pay success")
            getOrderHandler(oId).tell(PaySuccess(oId, tradeNo), peer)
          case r@_ =>
            log.debug(s"got $x, peer ! YEAH ")
            peer ! YEAH
        }

        log.debug(s"out order confirm.$oId")
      } catch {
        case e: Exception =>
          e.printStackTrace()
          throw e
      }

    case x@ConfirmAcceptOrder(oId, sId) =>
      log.debug(s"got $x")
      val peer = sender()
      try {
        orderDAO.find(oId).map {
          case Some(order) =>
            log.debug(s"got order $order")
            if (order.storeId != sId) {
              log.error(s"order owner ${order.storeId} but submit is $sId")
              peer ! StoreIdMismatch
            } else if (order.state == OrderState.OrderCanceled.id) {
              peer ! OperationTimeout
            } else {
              log.debug(s"$x ConfirmAcceptOrder success")
              getOrderHandler(oId).tell(ConfirmAcceptOrder(oId, sId), peer)
            }
          case None =>
            log.warning(s"can not find order: [$oId]")
            peer ! CannotFindOrder(oId)
        }
        log.debug(s"out order confirm.$oId")
      } catch {
        case e: Exception =>
          e.printStackTrace()
          throw e
      }

    case x@ConfirmRefuseOrder(oId, sId) =>
      log.debug(s"got $x")
      val peer = sender()
      try {
        orderDAO.find(oId).map {
          case Some(order) =>
            log.debug(s"got order $order")
            if (order.storeId != sId) {
              log.error(s"order owner ${order.storeId} but submit is $sId")
              peer ! StoreIdMismatch
            } else if (order.state == OrderState.OrderCanceled.id) {
              peer ! OperationTimeout
            } else {
              log.debug(s"$x ConfirmAcceptOrder success")
              //              getOrderHandler(oId).forward(ConfirmRefuseOrder(oId, sId))
              getOrderHandler(oId).tell(ConfirmRefuseOrder(oId, sId), peer)
            }
          case None =>
            log.warning(s"can not find order: [$oId]")
            peer ! CannotFindOrder(oId)
        }
        log.debug(s"out order confirm.$oId")
      } catch {
        case e: Exception =>
          e.printStackTrace()
          throw e
      }


    case x@ConfirmShipOrder(oId, sId) =>
      val peer = sender()
      getOrderHandler(oId).tell(x, peer)

    case x@ConfirmDishArrived(oId, sId) =>
      val peer = sender()
      getOrderHandler(oId).tell(x, peer)

    case x@ApplyRefund(oId, cId, msg) =>
      val peer = sender()
      getOrderHandler(oId).tell(x, peer)

    case x@AcceptRefund(oId, rId, sId) =>
      val peer = sender()
      getOrderHandler(oId).tell(x, peer)

    case x@RefuseRefund(oId, rId, sId, msg) =>
      val peer = sender()
      getOrderHandler(oId).tell(x, peer)

    case x@FinishRefund(oId, rId) =>
      val peer = sender()
      getOrderHandler(oId).tell(x, peer)

    case s => log.info(s"orderManager received unknown message $s")


  }

  private[this] def getOrderHandler(
                                     orderId: Long
                                   ) = {
    context.child(orderId.toString).getOrElse {
      val prop = OrderHandler.props(
        orderId,
        orderDAO,
        refundDAO,
        appSettings
      )
      val actor = context.actorOf(prop, orderId.toString)
      context.watch(actor)
      actor
    }

  }


}
