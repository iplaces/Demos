package models.dao

import com.google.inject.{Inject, Singleton}
import models.tables.SlickTables
import org.slf4j.LoggerFactory
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

/**
 * Created by ZYQ on 2016/3/10.
 **/
object CategoryDAO {
  val adminStoreId = 100000l
}

@Singleton
class CategoryDAO @Inject() (
                              protected val dbConfigProvider: DatabaseConfigProvider
                              ) extends HasDatabaseConfigProvider[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  private val log = LoggerFactory.getLogger(this.getClass)


  val Categories = SlickTables.tCategories

  def add(category: SlickTables.rCategories) = db.run(
    (Categories += category).asTry
  )

  def edit(category: SlickTables.rCategories) = db.run(
    Categories.insertOrUpdate(category).asTry
  )

  def delete(id: Long) = db.run(
    Categories.filter(_.id === id).delete.asTry
  )

  def deleteByStoreId(storeId: Long) = db.run(
    Categories.filter(_.storeId === storeId).delete.asTry
  )

  def list(storeId: Long) = db.run(
    Categories.filter(_.storeId === storeId).sortBy(_.rank).result
  )

  def information(id: Long) = db.run(
    Categories.filter(_.id === id).result.headOption
  )


  /***********functions for customer***************/

  /**
   * 列出所有餐厅分类 按rank排序
    *
    * @return
   */
  def listStoreCat()={
    db.run(Categories.filter(_.storeId===CategoryDAO.adminStoreId).sortBy(_.rank).result)
  }

  /**
   * 列出菜品分类 按rank排序
    *
    * @param storeId
   * @return
   */
  def listGoodCat(storeId:Long)={
    db.run(Categories.filter(_.storeId===storeId).sortBy(_.rank).result)
  }

  /**
   * 获取分类的信息
    *
    * @param catId
   * @return
   */
  def getCatById(catId:Long)={
    db.run(Categories.filter(_.id===catId).result.headOption)
  }

}
