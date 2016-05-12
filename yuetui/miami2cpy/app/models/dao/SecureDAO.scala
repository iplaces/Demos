package models.dao

import com.google.inject.{Inject, Singleton}
import models.tables.SlickTables
import org.slf4j.LoggerFactory
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import slick.driver.JdbcProfile

/**
 * Created by wangchunze on 2016/4/15.
 */

@Singleton
class SecureDAO@Inject()(
                          protected val dbConfigProvider: DatabaseConfigProvider
                          ) extends HasDatabaseConfigProvider[JdbcProfile] {
  import slick.driver.MySQLDriver.api._
  private val log = LoggerFactory.getLogger(this.getClass)
  val secureKey=SlickTables.tSecureKey

  def getSecureKey(appid:String)={
    db.run(secureKey.filter(_.appid===appid).map(_.secureKey).result.headOption)
  }

}
