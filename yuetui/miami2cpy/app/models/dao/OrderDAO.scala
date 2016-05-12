package models.dao

import com.google.inject.{Inject, Singleton}
import models.dao.OrderDAO.OrderState
import models.dao.RefundDAO.RefundState
import models.tables.SlickTables
import org.slf4j.LoggerFactory
import play.api.cache.CacheApi
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
 * Created by wangchunze on 2016/3/14.
 */

object OrderDAO {
/* COMMENT '订单状态，0为未付款，1为支付成功、等待接单，2为等待发货，3商家确认送达，4订单成功，5成功并已评价，6为支付失败，
    7为订单撤销，8为商家拒绝接单，9为客户申请退款，999其他。5、4为订单成功状态。','*/

  object OrderState extends Enumeration {
    type OrderState = Value
    val WaitingPay = Value(0)   //可撤销订单，不退款
    val WaitingAccept = Value(1) //可撤销订单，直接退款
    val WaitingShipping = Value(2) //不可撤销，可申请退款
    val ConfirmDelivered = Value(3) //不可撤销，可申请退款

    val OrderSuccess = Value(4)  //不可撤销或者申请退款
    val OrderCommented = Value(5) //不可撤销或者申请退款

    val PayFailure = Value(6)  //不可撤销或者申请退款
    val OrderCanceled = Value(7)  //不可撤销或者申请退款
    val AcceptFailure = Value(8)  //不可撤销或者申请退款
    val ApplyRefund = Value(9)  //不可撤销或者申请退款

    val Other = Value(999)
  }

  def stateStr(order: SlickTables.rOrders) = {
    OrderState(order.state) match {
      case OrderState.WaitingPay => "未付款"
      case OrderState.WaitingAccept => "等待接单"
      case OrderState.WaitingShipping => "等待发货"
      case OrderState.ConfirmDelivered => "确认送达"

      case OrderState.OrderSuccess => "订单成功"
      case OrderState.OrderCommented => "成功并已评价"

      case OrderState.PayFailure => "支付失败"
      case OrderState.OrderCanceled => "订单撤销"
      case OrderState.AcceptFailure => "拒绝接单"
      case OrderState.ApplyRefund => "申请退款"

      case OrderState.Other => ""

    }
  }

  def stateDesp(order: SlickTables.rOrders) = {
    OrderState(order.state) match {
      case OrderState.WaitingPay => ""
      case OrderState.WaitingAccept => ""
      case OrderState.WaitingShipping => ""
      case OrderState.ConfirmDelivered => ""

      case OrderState.OrderSuccess => "完成"
      case OrderState.OrderCommented => "完成"

      case OrderState.PayFailure => "完成"
      case OrderState.OrderCanceled => "完成"
      case OrderState.AcceptFailure => "完成"
      case OrderState.ApplyRefund => "退款处理中"

      case OrderState.Other => ""

    }
  }

}
@Singleton
class OrderDAO @Inject() (
                         goodDAO: GoodDAO,
                         storeDAO: StoreDAO,
                         cache: CacheApi,
protected val dbConfigProvider: DatabaseConfigProvider
                           ) extends HasDatabaseConfigProvider[JdbcProfile] {
  import slick.driver.MySQLDriver.api._
  private val log = LoggerFactory.getLogger(this.getClass)

  val Orders=SlickTables.tOrders
  val OrderGoods=SlickTables.tOrderGoods
  val Goods = SlickTables.tGoods
  val Comments = SlickTables.tComments
  val Refunds = SlickTables.tRefunds

  import concurrent.duration._


  def listByStoreId(storeId: Long) = db.run(
    Orders.filter(_.storeId === storeId).result
  )

  //获取餐厅订单
  def getOrderByStoreId(storeId:Long,state:Int)={
    db.run(Orders.filter(t=>(t.storeId===storeId)&&t.state===state).sortBy(_.createTime.desc).result)
  }



  /***************用户操作部分***********************/

  //创建订单
//  def createOrder(storeId:Long,customerId:Long,recipient:String,address:String,contact:String,remark:String,
//                 packFee:Int,totalFee:Int,payStatus:Int,state:Int,arriveTime:Long,createTime:Long)={
//    db.run(Order.map(t=>(
//      t.storeId,t.customerId,t.recipient,t.address,t.contact,t.remark,t.packFee,
//      t.totalFee,t.payStatus,t.state,t.arriveTime,t.createTime)).returning(Order.map(_.id))+=
//      (storeId,customerId,recipient,address,contact,remark,packFee,totalFee,payStatus,state,arriveTime,createTime)
//    ).mapTo[Long]
//  }
  def createOrder(order:SlickTables.rOrders)={
    db.run(((Orders returning Orders.map(_.id))+=order)).mapTo[Long]
  }

  //创建订单的菜品详情
  def createOrderGoods(orderId:Long,goodsId:Long,num:Int)={
    db.run(OrderGoods.map(t=>(t.orderId,t.goodId,t.num)).returning(
      OrderGoods.map(_.id))+=(orderId,goodsId,num)).mapTo[Long]
  }

  /**获取用户的订单总数*/
  def getOrderNumByCustomer(customerId:Long)={
    db.run(Orders.filter(_.customerId===customerId).size.result)
  }

  /**
   * 获取我的订单列表 right join orderGoods表 返回订单和订单中的菜品信息
    *
    * @param customerId
   * @param curPage
   * @param pageSize
   * @return
   */
  def listOrderByCustomerWithDetail(customerId:Long,curPage:Int,pageSize:Int)={
    db.run(Orders.filter(_.customerId===customerId).sortBy(_.createTime.desc).drop((curPage-1)*pageSize)
      .take(pageSize).joinRight(OrderGoods).on(_.id===_.orderId).filter(_._1.nonEmpty).result)
  }

  //获取订单菜品
  def getOrderGoodsById(orderId:Long)={
    db.run(OrderGoods.filter(_.orderId===orderId).result)
  }

  //根据订单号获取订单菜品详情 join Goods表
  def getGoodsByOrderId(orderId:Long)={
    db.run(OrderGoods.filter(_.orderId===orderId).join(Goods).on(_.goodId===_.id).result)
  }

  //根据订单号获取订单详情
  def getOrderById(orderId:Long)={
    db.run(Orders.filter(_.id===orderId).result.headOption)
  }

  /**
   * 改变订单状态    0为未付款，1为支付成功、等待接单，2为等待发货，3订单成功，4成功并已评价，5为支付失败，
   *                6为订单撤销，7为商家拒绝接单，8为客户申请退款，999其他。 3、4为订单成功状态。
    *
    * @param orderId
   * @param state
   * @return
   */
  def changeState(orderId:Long,state:Int)={
    db.run(Orders.filter(_.id===orderId).map(_.state).update(state))
  }

    /**
     *根据订单号增加菜品销量和餐厅销量
     */
  def increaseSales(orderId:Long)={
    this.getOrderGoodsById(orderId).map{res=>
      res.foreach{goods=>
        goodDAO.changeSales(goods.goodId,goods.num) //改变相应的菜品销量
      }
    }
    this.getOrderById(orderId).map{
      case Some(order)=>
        storeDAO.increaseSales(order.storeId)  //改变餐厅的销量
      case None=>
        log.info(s"order doesnot exist in confirmReceipt for orderId=$orderId")
    }
  }

  /**
   *根据订单号 改变菜品的库存
   */
  def decreaseStock(orderId:Long)={
    this.getOrderGoodsById(orderId).map{res=>
      res.foreach{goods=>
        goodDAO.changeStock(goods.goodId,goods.num)
      }
    }
  }



  /***************Admin操作部分***********************/
  def list(storeId: Long, state: Option[Int], pageNum: Int, pageSize: Int) = {
    val orders = if (state.isDefined) Orders.filter(_.state === state) else Orders
    val q = ( for {
      os <- orders.filter(_.storeId === storeId).sortBy(_.id.desc)
        .drop(pageSize * (pageNum - 1)).take(pageSize).result
      num <- orders.filter(_.storeId === storeId).size.result
    } yield (os, num) ).transactionally
    db.run(q)
  }

  def listOrderGoods( seq : Seq[Long]) =
    db.run(
      OrderGoods.filter(_.orderId inSet seq).result
    )

/*  def listWithdraws(storeId: Long, pageNum: Int, pageSize: Int) = {

    val orders = Orders.filter(_.storeId === storeId).filter(_.state === OrderState.ApplyRefund.id)
    val refunds = Refunds.filter(_.state === RefundState.RefundApply.id)
    val rightOuterJoin = for {
      (o, r) <- orders join refunds on (_.id === _.orderId)
    } yield (o, r)
  }*/

  def accept(id: Long) = db.run(
    Orders.filter(_.id === id).map(_.state).update(OrderState.WaitingShipping.id).asTry
  )

  def refuse(id: Long) = db.run(
    Orders.filter(_.id === id).map(_.state).update(OrderState.AcceptFailure.id).asTry
  )

  def deliver(id: Long) = db.run(
    Orders.filter(_.id === id).map(_.state).update(OrderState.ConfirmDelivered.id).asTry
  )

  def spans(storeId: Long, state: Int) = db.run(
    Orders.filter(_.storeId === storeId).filter(_.state === state).size.result
  )

  def listNeedRecoveryOrderByTime(start: Long, end: Long) = db.run(
    Orders.filter(o =>
       o.createTime >= start && o.createTime <= end
      && (o.state === OrderState.WaitingPay.id
         || o.state === OrderState.WaitingAccept.id
         || o.state === OrderState.WaitingShipping.id
         || o.state === OrderState.ConfirmDelivered.id
         || o.state === OrderState.ApplyRefund.id)
    ).result
  ).andThen{
    case Success(orders) => orders.foreach{ o =>
      cache.set(orderKey(o.id), Future.successful(Some(o)), 15 minutes)
    }
  }

  def find(orderId: Long) = cache.getOrElse(orderKey(orderId), 30 minutes) {
    log.info(s"find order $orderId")
    db.run(
      Orders.filter(_.id === orderId).result.headOption
    ).andThen { case Failure(e) =>
      log.warn(s"find ${e.getMessage}")
      cache.remove(orderKey(orderId))
    }
  }

  def updateState(oId: Long, newState: OrderState.OrderState) = {
    db.run(
      Orders
        .filter(o => o.id === oId)
        .map(_.state)
        .update(newState.id)
    ).map(_ == 1).andThen {
      case _ =>
        cache.remove(orderKey(oId))
    }
  }

  def updateStateAndTradeNo(oId: Long, newState: OrderState.OrderState, tradeNo: String) = {
    db.run(
      Orders
        .filter(o => o.id === oId)
        .map( o => (o.state, o.tradeNo))
        .update(newState.id, tradeNo)
    ).map(_ == 1).andThen {
      case _ =>
        cache.remove(orderKey(oId))
    }
  }

  private[this] def orderKey(orderId: Long) = "ord\u0001o\u0001" + orderId

}
