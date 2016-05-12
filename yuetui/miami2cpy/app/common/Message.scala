package common

import akka.actor.ActorRef

/**
 * Created by wangchunze on 2016/3/24.
 */
sealed trait Message

//消息推送相关actor
case class AddStoreActorRef(out:ActorRef,storeId:Long) extends Message
case class SendNewOrder(storeId:Long,orderId:Long)extends Message
case class SendRefundOrder(storeId:Long,orderId:Long) extends Message
case class SendCancelOrder(storeId:Long,orderId:Long) extends Message