package common

import com.google.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.{Configuration, Environment}
import slick.driver.JdbcProfile

/**
 * Created by ZYQ on 2016/3/7.
 **/
@Singleton
class AppSettings @Inject()(
                             protected val dbConfigProvider: DatabaseConfigProvider,
                             environment: Environment,
                             configuration: Configuration
                             ) extends HasDatabaseConfigProvider[JdbcProfile] {


  dbConfig.db//init DatabaseConfigProvider

  private[this] val config = configuration.underlying

  val appConfig = config.getConfig("app")

  val miamiConfig = appConfig.getConfig("miami")
  val miamiProtocol = miamiConfig.getString("protocol")
  val miamiHost = miamiConfig.getString("host")
  val miamiPort = miamiConfig.getString("port")
  val miamiDomain = miamiConfig.getString("domain")

  val orderRecoverTimeRangeInMinutes = miamiConfig.getDuration("order.recoverTimeRange").toMinutes
  val payTimeoutInMinutes = miamiConfig.getConfig("order").getInt("payTimeoutInMinutes")
  val storeConfirmAcceptTimeInMinutes = miamiConfig.getConfig("order").getInt("storeConfirmAcceptTimeInMinutes")
  val dishReceiveWaitingTimeInHour = miamiConfig.getConfig("order").getDouble("dishReceiveWaitingTimeInHour")
  val customerConfirmReceivedTimeoutInHour = miamiConfig.getConfig("order").getDouble("customerConfirmReceivedTimeoutInHour")


  val hestiaConfig = appConfig.getConfig("hestia")
  val hestiaProtocol = hestiaConfig.getString("protocol")
  val hestiaHost = hestiaConfig.getString("host")
  val hestiaPort = hestiaConfig.getString("port")
  val hestiaDomain = hestiaConfig.getString("domain")
  val hestiaAppId = hestiaConfig.getString("appId")
  val hestiaSecureKey = hestiaConfig.getString("secureKey")
  val hestiaPartKey = hestiaConfig.getString("partKey")

  val terraConfig = appConfig.getConfig("terra")
  val terraProtocol = terraConfig.getString("protocol")
  val terraHost = terraConfig.getString("host")
  val terraPort = terraConfig.getString("port")
  val terraDomain = terraConfig.getString("domain")
  val terraAppId = terraConfig.getString("appId")
  val terraSecureKey = terraConfig.getString("secureKey")
  val terraRedirectUrl = terraConfig.getString("redirectUrl")

  val bazaarConfig = appConfig.getConfig("bazaar")
  val bazaarProtocol = bazaarConfig.getString("protocol")
  val bazaarHost = bazaarConfig.getString("host")
  val bazaarPort = bazaarConfig.getString("port")
  val bazaarDomain = bazaarConfig.getString("domain")
  val bazaarAppId = bazaarConfig.getString("appId")
  val bazaarSecureKey = bazaarConfig.getString("secureKey")

  val tcommentConfig = appConfig.getConfig("tcomment")
  val tcommentProtocol = tcommentConfig.getString("protocol")
  val tcommentDomain = tcommentConfig.getString("domain")
  val tcommentAppId = tcommentConfig.getString("appId")
  val tcommentSecureKey = tcommentConfig.getString("secureKey")


  val mammonConfig=appConfig.getConfig("mammon")
  val mammonProtocol = mammonConfig.getString("protocol")
  val mammonDomain = mammonConfig.getString("domain")
  val mammonAppId = mammonConfig.getString("appId")
  val mammonSecureKey = mammonConfig.getString("secureKey")

}

