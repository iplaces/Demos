package actor

import akka.actor._
import play.api.Logger
import play.api.libs.json.Json


/**
 * Created by wangchunze on 2016/3/25.
 */
object WebSocketActor {
  def props(out: ActorRef) = Props(new WebSocketActor(out))
}

class WebSocketActor(out: ActorRef) extends Actor {
  private[this] val logger=Logger(this.getClass)

  @throws[Exception](classOf[Exception])
  override def preStart():Unit={
    logger.info(s"${self.path.name} actor starting...")
    println(s"${self.path.name} actor starting...")
  }

  @throws[Exception](classOf[Exception])
  override def postStop():Unit={
    logger.info(s"${self.path.name} actor stopping...")
  }

  override def receive: Receive = {
    case  msg: String=>
      if (msg == "goodbye") self ! PoisonPill
//      else out ! Json.obj("errCode"->0,"msg"->msg)
  }

}