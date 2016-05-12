package models.dao

import com.google.inject.{Inject, Singleton}
import models.tables.SlickTables
import org.slf4j.LoggerFactory
import play.api.cache.CacheApi
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Failure

/**
 * Created by ZYQ on 2016/3/9.
 **/
object GoodDAO {
  /*COMMENT '状态， 0为上架，1为下架, 2为删除',*/

  object GoodState extends Enumeration {
    type GoodState = Value
    val UpShelf = Value(0)
    val OffShelf = Value(1)
    val Deleted = Value(2)
    val Other = Value(999)
  }


}
@Singleton
class GoodDAO @Inject () (
                           protected val dbConfigProvider: DatabaseConfigProvider,
                           cache: CacheApi
                           ) extends HasDatabaseConfigProvider[JdbcProfile] {

  import slick.driver.MySQLDriver.api._
  import GoodDAO._
  private val log = LoggerFactory.getLogger(this.getClass)
  private val goodsCacheKey = "cache.goods.id."

  val Goods = SlickTables.tGoods

  def add(good: SlickTables.rGoods) = db.run(
    (Goods += good).asTry
  )

  def edit(good: SlickTables.rGoods) = db.run(
    Goods.insertOrUpdate(good).asTry
  )

  def up(id: Long) = db.run(
    Goods.filter(_.id === id).map(_.state).update(GoodState.UpShelf.id).asTry
  )

  def off(id: Long) = db.run(
    Goods.filter(_.id === id).map(_.state).update(GoodState.OffShelf.id).asTry
  )

  def delete(id: Long) = db.run(
    Goods.filter(_.id === id).map(_.state).update(GoodState.Deleted.id).asTry
  )

  def list(id: Long) = db.run(
    Goods.filter(_.id === id).result.head
  )

  def listGoodNames(seq: Seq[Long]) = db.run(
    Goods.filter(_.id inSet seq).map( g => (g.id, g.name)).result
  ).map( t => t.toMap)

  def deleteByStoreId(storeId: Long) = db.run(
    Goods.filter(_.storeId === storeId).delete.asTry
  )

  def listByStoreId(storeId: Long) = db.run(
    Goods.filter(_.storeId === storeId).filter(_.state =!= GoodState.Deleted.id).sortBy(_.catId).result
  )

  def listByCatId(catId: Long) = db.run(
    Goods.filter(_.catId === catId).map(_.id).result
  )

  def updateCatIds( seq: Seq[Long]) = db.run(
    Goods.filter(_.id inSet seq).map(_.catId).update(0).asTry
  )



  /******************用户相关操作***************************/

  /**
   * 获取餐厅的所有商品 筛选state为0 即上架状态
    *
    * @param storeId
   * @return
   */
  def getGoodsFromStore(storeId:Long)={
     db.run(Goods.filter(t=>(t.storeId===storeId)&&(t.state===0)).result)
  }

  def searchGood(searchKey:String)={
    db.run(Goods.filter(t=>((t.name like "%"+searchKey+"%")||(t.description like "%"+searchKey+"%"))&&(t.state===0))
      .result)
  }

  /**
   * 获取菜品信息 使用cache
   * @param goodsId
   * @return
   */
  def getGoodsById(goodsId:Long)={
//    cache.getOrElse(goodsCacheKey+goodsId,30 minutes){
//      val action=
//        Goods.filter(_.id===goodsId).result.headOption
//      db.run(action).andThen{
//        case Failure(e)=>
//          cache.remove(goodsCacheKey+goodsId)
//      }
//    }
    db.run(Goods.filter(_.id===goodsId).result.headOption)
  }

    /**
     *  对销量做相应的改变
     */
  def changeSales(goodsId:Long,num:Int)={
    db.run(Goods.filter(_.id===goodsId).result.headOption).flatMap{
      case Some(goods) =>
        db.run(Goods.filter(_.id===goodsId).map(_.sales).update(goods.sales+num))
      case None =>
        log.info(s"update the sale failed for goodsId=$goodsId")
        Future(0)
    }
  }

  /**
   * 改变菜品的库存
   */
  def changeStock(goodsId:Long,num:Int)={
    db.run(Goods.filter(_.id===goodsId).result.headOption).flatMap{
      case Some(goods) =>
        db.run(Goods.filter(_.id===goodsId).map(_.stock).update(goods.stock-num))
      case None =>
        log.info(s"update the sale failed for goodsId=$goodsId")
        Future(0)
    }
  }







}
