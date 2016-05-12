package models.dao

import com.google.inject.{Inject, Singleton}
import models.dao.RefundDAO.RefundState.RefundState
import models.tables.SlickTables
import org.slf4j.LoggerFactory
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import slick.driver.JdbcProfile
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

/**
 * Created by 王春泽 on 2016/4/6.
 */

object RefundDAO {

  /**
   * 退款状态: 0为客户申请退款，1为商家同意，退款处理中，2为商家拒绝退款，3为退款完成
   */
  object RefundState extends Enumeration {
    type RefundState = Value
    val RefundApply = Value(0)
    val RefundAccept = Value(1)
    val RefundRefuse = Value(2)
    val RefundFinish = Value(3)
  }

  def stateStr(refund: SlickTables.rRefunds) = {
    RefundState(refund.state) match {
      case RefundState.RefundApply => "申请退款"
      case RefundState.RefundAccept => "同意退款、处理中"
      case RefundState.RefundRefuse => "拒绝退款"
      case RefundState.RefundFinish => "退款完成"
      case _ => ""
    }

  }
}


@Singleton
class RefundDAO @Inject()(
                         protected val dbConfigProvider:DatabaseConfigProvider
                           )extends HasDatabaseConfigProvider[JdbcProfile]{

  import slick.driver.MySQLDriver.api._
  private val log=LoggerFactory.getLogger(this.getClass)
  val refunds = SlickTables.tRefunds

  /**
   * 创建退款
   *
   * @param refund
   * @return
   */
  def createRefunds(refund:SlickTables.rRefunds)={
    db.run((refunds += refund).asTry)
  }

  /**
   * 获取退款
   *
   * @param refundId
   * @return
   */
  def getRefundById(refundId:Long)={
    db.run(refunds.filter(_.id===refundId).result.headOption)
  }

  /**
   * 根据订单号获取退款信息
   *
   * @param orderId
   * @return
   */
  def getRefundByOrderId(orderId:Long)={
    db.run(refunds.filter(_.orderId===orderId).result.headOption)
  }


  /*
  * for ADMIN: 商家接处理退款请求
  * */
  def processRefund(rId: Long, state: RefundState, msg: String = "") = db.run(
    refunds.filter(_.id === rId).
      map( t => (t.state, t.storeDesp)).
      update(state.id, msg)
  ).map(_ == 1)

  /*
* for ADMIN: 商家接处理退款请求
* */
  def listRefunds(seq: Seq[Long]) = db.run(
    refunds.filter(_.orderId inSet seq).result
  )

}
