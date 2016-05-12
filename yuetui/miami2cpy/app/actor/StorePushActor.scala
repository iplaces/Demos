package actor

import akka.actor.{ActorRef, Actor}
import com.google.inject.{Inject, Singleton}
import common._
import play.api.Logger
import play.api.libs.json.Json
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by wangchunze on 2016/3/24.
 */
@Singleton
class StorePushActor @Inject()() extends Actor{

  private[this] val logger=Logger(this.getClass)

  private[this] val storePushMap=collection.mutable.HashMap[Long,ActorRef]()

  @throws[Exception](classOf[Exception])
  override def preStart():Unit={
    logger.info(s"${self.path.name} actor starting...")
    println(s"${self.path.name} actor starting...")
  }

  @throws[Exception](classOf[Exception])
  override def postStop():Unit={
    logger.info(s"${self.path.name} actor stopping...")
  }

  override def receive:Receive={
    case AddStoreActorRef(out,storeId)=>
      storePushMap.put(storeId,out)

    case SendNewOrder(storeId,orderId)=>    //新订单处理
      if(storePushMap.contains(storeId)){
        val store=storePushMap(storeId)
        store ! Json.obj("errCode"->0,"type"->"newOrder","orderId"->orderId).toString()
      }else{
        logger.info(s"the store does not open: storeId=$storeId")
      }


    case SendRefundOrder(storeId,orderId)=> //退款处理
      if(storePushMap.contains(storeId)){
        val store=storePushMap(storeId)
        store ! Json.obj("errCode"->0,"type"->"refundOrder","orderId"->orderId).toString()
      }else{
        logger.info(s"the store does not open: storeId=$storeId")
      }

    case SendCancelOrder(storeId,orderId)=> //取消订单
      if(storePushMap.contains(storeId)){
        val store=storePushMap(storeId)
        store ! Json.obj("errCode"->0,"type"->"cancelOrder","ordrId"->orderId).toString()
      }else{
        logger.info(s"the store does not open: storeId=$storeId")
      }
  }


}
