package actor

import actor.UpdateOrderActor._
import akka.actor.Actor
import com.google.inject.{Singleton, Inject}
import models.dao.OrderDAO
import models.dao.OrderDAO.OrderState
import org.slf4j.LoggerFactory
import play.api.Logger
import scala.concurrent.ExecutionContext.Implicits.global
import common.Constants._

/**
 * Created by wangchunze on 2016/3/17.
 * 用于自动改变订单状态，包括未付款自动取消订单，接单后自动确认收货等
 */

@Singleton
class UpdateOrderActor @Inject()(
                                orderDAO:OrderDAO
                                  ) extends Actor{
  import concurrent.duration._

  private val log = LoggerFactory.getLogger(this.getClass)


  @throws[Exception](classOf[Exception])
  override def preStart():Unit={
    log.info(s"${self.path.name} actor starting...... ")
  }

  override def postStop():Unit={
    log.info(s"${self.path.name} actor stopping......")
  }

  override def receive : Receive = {
    case CreateOrder(orderId)=> //下单后10分钟未支付则自动取消 创建订单并在线支付时需发送该消息
      context.system.scheduler.scheduleOnce(60 minutes,self,CancelOrder(orderId))

    case AcceptOrder(orderId)=> //接单后2小时自动确认收货
      context.system.scheduler.scheduleOnce(2 hours,self,FinishOrder(orderId))

    case WaitingAccept(orderId)=> //待接单的状态下 20分钟没有接单自动取消 支付成功或货到付款需发送此消息
      context.system.scheduler.scheduleOnce(20 minutes,self,CancelOrder(orderId))

    case CancelOrder(orderId)=>   //取消订单
      orderDAO.getOrderById(orderId).map{
        case Some(order)=>
          if(order.payStatus==1){ //在线支付
            if(order.state==OrderState.WaitingPay.id){ //当前是待付款状态 则改为支付失败状态
              orderDAO.changeState(orderId,OrderState.PayFailure.id)
            }else if(order.state==OrderState.WaitingAccept.id){ //当前是待接单状态 则改为拒绝接单
              orderDAO.changeState(orderId,OrderState.AcceptFailure.id)
            }
          }else if(order.payStatus==0){ //货到付款
            if(order.state==OrderState.WaitingAccept.id){ //当前是待接单状态 则改为拒绝接单
              orderDAO.changeState(orderId,OrderState.AcceptFailure.id)
            }
          }

        case None=>
      }

    case FinishOrder(orderId)=>  //结束订单
      orderDAO.getOrderById(orderId).map{
        case Some(order)=>
          if(order.state==OrderState.ConfirmDelivered.id){  //如果当前商家确认送达 则订单完成
            orderDAO.changeState(orderId,OrderState.OrderSuccess.id)
            //增加相应的菜品和餐厅销量
            orderDAO.increaseSales(orderId)
          }
        case None=>
      }

  }

}

object UpdateOrderActor{
  case class CreateOrder(orderId:Long)
  case class AcceptOrder(orderId:Long)
  case class WaitingAccept(orderId:Long)
  case class CancelOrder(orderId:Long)
  case class FinishOrder(orderId:Long)
}
