package actor

import akka.actor.{Actor, Props, ActorRef}
import common.AppSettings
import models.dao.OrderDAO
import org.slf4j.LoggerFactory

/**
 * Created by wangchunze on 2016/4/27.
 */

object PayActor {
  def props(
             orderId: Long,
             orderDAO: OrderDAO,
             appSettings: AppSettings,
             orderManager: ActorRef
             ) = {
    Props(
      new PayActor(
        orderId,
        orderDAO,
        appSettings,
        orderManager
      )
    )
  }
}

class PayActor(
                orderId: Long,
                orderDAO: OrderDAO,
                appSettings: AppSettings,
                orderManager: ActorRef
                )extends Actor{
  private[this] val log = LoggerFactory.getLogger(this.getClass)

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    log.debug(s"$orderId actor starting...")
  }

  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    log.debug(s"$orderId actor stopping...")
  }

  override def receive: Receive = {

    case s => log.info(s"orderManager received unknown message $s")
  }

}
