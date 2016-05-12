package controllers

import actor.OrderHandler.ApplyRefund
import actor.OrderManager.{UpdateStateFailed, OperationSucceed}
import akka.actor.ActorRef
import akka.util.Timeout
import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}
import models.JsonProtocols
import models.dao.{OrderDAO, RefundDAO}
import org.slf4j.LoggerFactory
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import akka.pattern.ask
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
/**
 * Created by 王春泽 on 2016/4/6.
 */

@Singleton
class RefundCustomer @Inject()(
                              val orderDAO:OrderDAO,
                              val refundDAO:RefundDAO,
                                @Named("configured-OrderManager") orderManager: ActorRef
                                )extends Controller with JsonProtocols{
  private val log=LoggerFactory.getLogger(this.getClass)
  implicit val timeout = Timeout(15.seconds)
  /**
   * 创建退款
   * @return
   */
  def createRefund = Action.async{implicit request=>
//    Option(10004) match{
    request.session.get(SessionKey.userId) match {
      case Some(customerId)=>
//        Option(Json.obj("orderId"->59,"customerDesc"->"我要退款！！")) match{
        request.body.asJson match {
          case Some(jsonData) =>
            val orderId = (jsonData \ "orderId").as[Long]
            val customerDesc = (jsonData \ "customerDesc").as[String]
            (orderManager ? ApplyRefund(orderId, customerId.toLong, customerDesc)).map{
              case OperationSucceed  =>
                Ok(success)
              case UpdateStateFailed =>
                Ok(ErrorCode.refundCreateFailed)
            }

          case None =>
            Future.successful(Ok(ErrorCode.requestAsJsonEmpty))
          }
      case None=>
        Future.successful(Ok(ErrorCode.userNotLogin))
    }
  }


//  def getRefundInfo(orderId:Long)=Action.async{implicit request=>
//    request.session.get(SessionKey.userId) match{
//      case Some(customerId)=>
//        refundDAO.getRefundByOrderId()
//      case None=>
//
//    }
//  }




}
