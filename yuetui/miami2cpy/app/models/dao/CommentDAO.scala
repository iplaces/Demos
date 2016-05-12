package models.dao

import com.google.inject.{Inject, Singleton}
import models.tables.SlickTables
import org.slf4j.LoggerFactory
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by wangchunze on 2016/3/18.
 */

@Singleton
class CommentDAO @Inject()(
                            protected val dbConfigProvider: DatabaseConfigProvider
                            ) extends HasDatabaseConfigProvider[JdbcProfile] {
  import slick.driver.MySQLDriver.api._
  private val log = LoggerFactory.getLogger(this.getClass)

  val Comment = SlickTables.tComments
  val Order=SlickTables.tOrders

  /**
   * 创建评价
    *
    * @param orderId
   * @param transTime 送餐速度打分
   * @param dishGrade  菜品味道打分
   * @param storeId
   * @param createTime
   * @return
   */
  def createComment(orderId:Long,transTime:Int,dishGrade:Int,storeId:Long,createTime:Long)={
    db.run(Comment.map(t=>(t.orderId,t.transTime,t.dishGrade,t.storeId,t.createTime))
      .returning(Comment.map(_.id))
      +=(orderId,transTime,dishGrade,storeId,createTime)).mapTo[Long]
  }

  /**
   * 根据订单id获得订单的评价
    *
    * @param orderId
   * @return
   */
  def getByOrderId(orderId:Long)={
    db.run(Comment.filter(_.orderId===orderId).result.headOption)
  }

  /**
   * 获取餐厅评论数量
   * @param storeId
   * @param leval 1:好评  2:中评  3:差评
   * @return
   */
  def getNumByStoreId(storeId:Long,leval:Int)={
    db.run(Comment.filter(t=>(t.storeId===storeId)&& (leval match{
      case 1=>t.dishGrade >=4
      case 2=>(t.dishGrade>1)&&(t.dishGrade<=3)
      case 3=>t.dishGrade<=1
      case 0=>true
    })).size.result)
  }

  /**
   * 获取餐厅的所有评分
   * @param storeId
   * @return
   */
  def getGardeByStoreId(storeId:Long)={
    db.run(Comment.filter(_.storeId===storeId).map(_.dishGrade).result)
  }

  /**
   * 根据餐厅id获取该餐厅的评价
    *leval 1:好评  2:中评  3:差评
    * 分页返回结果
   */
  def getByStoreId(storeId:Long,leval:Int,curPage:Int,pageSize:Int)={
    db.run(Comment.filter(t=>(t.storeId===storeId)&& (leval match{
      case 1=>t.dishGrade >=4
      case 2=>(t.dishGrade>1)&&(t.dishGrade<=3)
      case 3=>t.dishGrade<=1
      case 0=>true
    })).drop((curPage-1)*pageSize).take(pageSize).result)
  }

  /**
   *获取餐厅的所有的评价
   * @param storeId
   * @return
   */
  def getAllByStoreId(storeId:Long)={
    db.run(Comment.filter(t=>t.storeId===storeId).result)
  }

}
