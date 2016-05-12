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
 * Created by wangchunze on 2016/3/10.
 **/
object  StoreDAO {

  object StoreState extends Enumeration {
    type StoreState = Value
    val Deleted = Value(-2)
    val Disabled = Value(-1)
    val Rested = Value(0)
    val Opening = Value(1)
    val Other = Value(999)
  }

}

@Singleton
class StoreDAO @Inject()(
                          protected val dbConfigProvider: DatabaseConfigProvider,
                          cache: CacheApi
                          ) extends HasDatabaseConfigProvider[JdbcProfile] {

  import StoreDAO._
  import slick.driver.MySQLDriver.api._

  private val log = LoggerFactory.getLogger(this.getClass)
  val Stores = SlickTables.tStores

  private val storeCacheKey = "cache.store.id."


  def edit(store: SlickTables.rStores) = db.run(
    Stores.insertOrUpdate(store).asTry
  )

  def add(store: SlickTables.rStores) = db.run(
    (Stores += store).asTry
  )

  def delete(id: Long) = db.run(
    Stores.filter(_.id === id).delete.asTry
  )

  def list = db.run(
    Stores.result
  )

  def listNames = db.run(
    Stores.map(s => (s.id, s.name)).result
  )

  def list(storeId: Long) = db.run(
    Stores.filter(_.id === storeId).result.headOption
  )

  def exist(id: Long) = db.run(
    Stores.filter(_.id === id).exists.result
  ).mapTo[Boolean]

  def enable(id: Long) = db.run(
    Stores.filter(_.id === id).map(_.state).update(StoreState.Rested.id).asTry
  )

  def disable(id: Long) = db.run(
    Stores.filter(_.id === id).map(_.state).update(StoreState.Disabled.id).asTry
  )

  def open(id: Long) = db.run(
    Stores.filter(_.id === id).map(_.state).update(StoreState.Opening.id).asTry
  )

  def close(id: Long) = db.run(
    Stores.filter(_.id === id).map(_.state).update(StoreState.Rested.id).asTry
  )


  def editCategory(storeId: Long, cateId: Long) = db.run(
    Stores.filter(_.id === storeId).map(_.catId).update(cateId).asTry
  )



  /******************用户相关操作***************************/

  /**
   * 某分类下餐厅数量
    *
    * @param catId
   * @return
   */
  def getStoreSumByCat(catId:Long)={
    db.run(Stores.filter(t=>(t.catId===catId)&&(t.state===1)).size.result)
  }

  /**
   * 根据分类列出餐厅 state为1
    *
    * @param catId
   * @param sort
   * @param curPage
   * @param pageSize
   * @return
   */
  def listStoreByCat(catId:Long,sort:Int,curPage:Int,pageSize:Int)={
    db.run(Stores.filter(t=>(t.id>0L)&&(t.state===1) && (if(catId>0) t.catId===catId else true)).
      sortBy(store=>
      if(sort==1)
        store.sales.desc  //销量
      else if(sort==2)
        store.grades.desc  //评分
      else if(sort==3)
        store.costTime.asc //配送速度
      else if(sort==4)
        store.basePrice.asc //起送价
      else
        store.id.asc
    ).drop((curPage-1)*pageSize).take(pageSize).result)
  }

  /**
   * 列出所有餐厅
    *
    * @param sort
   * @return
   */
  def listStoreAll(sort:Int)={
    db.run(Stores.filter(t=>(t.state===1)).sortBy(store=>
      if(sort==1)
        store.sales.desc  //销量
      else if(sort==2)
        store.grades.desc  //评分
      else if(sort==3)
        store.costTime.asc //配送速度
      else if(sort==4)
        store.basePrice.asc //起送价
      else
        store.id.asc
    ).result)
  }

  /**
   * 根据id获取餐厅信息
    * 使用cache
    *
    * @param storeId
   * @return
   */
  def getStoreById(storeId:Long)={
//    cache.getOrElse(storeCacheKey+storeId,30 minutes){
//      val action=
//        Stores.filter(_.id===storeId).result.headOption
//      db.run(action).andThen{
//        case Failure(e)=>
//          cache.remove(storeCacheKey+storeId)
//      }
//    }
    db.run(Stores.filter(_.id===storeId).result.headOption)
  }



  /**
   * 搜索餐厅
    *
    * @param searchKey
   * @return
   */
  def searchStore(searchKey:String)={
    db.run(Stores.filter(t=>((t.name like "%"+searchKey+"%") || (t.description like "%"+searchKey+"%"))&&(t.state===1))
      .result)
  }

  /**
   * 更新餐厅的美食评分 在actor中操作
    *
    * @param storeId
   * @param grade
   * @return
   */
  def updateGrade(storeId:Long,grade:Float)={
    db.run(Stores.filter(_.id===storeId).map(_.grades).update(grade))
  }

  /**
   * 更新餐厅的评论数 可以在定时任务执行时调用
    *
    * @param storeId
   * @param commentNum
   * @return
   */
  def updateComments(storeId:Long,commentNum:Int)={
    db.run(Stores.filter(_.id===storeId).map(_.comments).update(commentNum))
  }

  /**
   * 增加餐厅的评论数量
    *
    * @param storeId
   * @param num
   * @return
   */
  def increaseComments(storeId:Long,num:Int)={
    db.run(Stores.filter(_.id===storeId).result.headOption).flatMap{
      case Some(store)=>
        db.run(Stores.filter(_.id===storeId).map(_.comments).update(store.comments+num))
      case None =>
        log.info(s"increase the comments failed for storeid=$storeId")
        Future(0)
    }
  }

  //增加餐厅销量
  def increaseSales(storeId:Long)={
    db.run(Stores.filter(_.id===storeId).result.headOption).flatMap{
      case Some(store)=>
        db.run(Stores.filter(_.id===storeId).map(_.sales).update(store.sales+1))
      case None=>
        log.info(s"increase the sales failed for storeId=$storeId")
        Future(0)
    }
  }

}
