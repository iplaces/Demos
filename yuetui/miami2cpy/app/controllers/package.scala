import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import common.AppSettings
import models.dao.SecureDAO
import org.slf4j.LoggerFactory
import play.api.Logger
import play.api.libs.json.Json
import play.api.libs.streams.Accumulator
import play.api.mvc._
import utils.SecureUtil

import scala.collection.concurrent.TrieMap
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
 * User: Taoz
 * Date: 11/30/2015
 * Time: 9:14 PM
 */
package object controllers {



  import play.api.libs.concurrent.Execution.Implicits.defaultContext

  case class Logging[A](action: Action[A]) extends Action[A] {

    def apply(request: Request[A]): Future[Result] = {
      action(request)
    }

    lazy val parser = action.parser
  }

  //#actions-class-wrapping


  @Singleton
  class LoggingAction @Inject() extends ActionBuilder[Request] {
    private val log = LoggerFactory.getLogger(this.getClass)


    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
      log.info(s"access log: ${request.uri}")
      block(request)
    }
  }

  object SessionKey {
    val userId = "terra_uid"
    val uType = "terra_utype"
    val timestamp = "terra_timestamp"
    val nickName = "terra_nickname"
    val loginType = "terra_logintype"
    val headImg = "terra_headimg"
  }

  object UserType{
    val admin = 1
    val store = 2
    val user = 3
  }

  def getConfMap(request: Request[AnyContent]): Map[String, String] = {
    lazy val baseInfo = Map(
      "id" -> request.session.get(SessionKey.userId).getOrElse(""),
      "userType" -> request.session.get(SessionKey.uType).getOrElse(""),
      "nickName" -> request.session.get(SessionKey.nickName).getOrElse("")
    )
    baseInfo
  }


  case class rUser(uid: Long, userType: Int, nickName: String, headImg: String)
//  case class rStore(uid: Long, userType: Int, nickName: String, headImg: String)

  class UserRequest[A](val user: rUser, request: Request[A]) extends WrappedRequest[A](request)


  @Singleton
  class UserAction @Inject()() extends ActionRefiner[Request, UserRequest] {

    val SessionTimeOut = 24 * 60 * 60 * 1000 //ms
    val logger = LoggerFactory.getLogger(getClass)

    protected def authUser(request: RequestHeader): Future[Option[rUser]] = {

      val session = request.session

      try {
        val ts = session.get(SessionKey.timestamp).get.toLong
        val uid = session.get(SessionKey.userId).get.toLong
        val userType = session.get(SessionKey.uType).get.toInt
        val nickName = session.get(SessionKey.nickName).get
        val headImg = session.get(SessionKey.headImg).get


        if (System.currentTimeMillis() - ts > SessionTimeOut) {
          Future.successful(None)
        } else {
          Future.successful(Some(rUser(uid, userType, nickName, headImg)))
        }
      } catch {
        case ex: Throwable =>
          logger.info("Not Login Yet.")
          Future.successful(None)
      }
    }


    protected def onUnauthorized(request: RequestHeader) =
      Results.Redirect(routes.Auth.loginPage()).withNewSession


    override protected def refine[A](request: Request[A]): Future[Either[Result, UserRequest[A]]] = {
      authUser(request).map {
        case Some(user) =>
          Right(new UserRequest(user, request))
        case _ =>
          Left(onUnauthorized(request))
      }
    }
  }


  @Singleton
  class AdminAction() extends ActionRefiner[UserRequest, UserRequest] {
    /**
     * 商户管理员认证
     */
    private val logger = LoggerFactory.getLogger(this.getClass)

    override protected def refine[A](request: UserRequest[A]): Future[Either[Result, UserRequest[A]]] = {
      Future.successful {
        if (request.user.userType < UserType.user) {
          Right(request)
        } else {
          Left(Results.Forbidden("Only Admin can do."))
        }
      }
    }
  }

  @Singleton
  class SystemAdminAction() extends ActionRefiner[UserRequest, UserRequest] {
    /**
     * 系统管理员认证
     */
    private val logger = LoggerFactory.getLogger(this.getClass)

    override protected def refine[A](request: UserRequest[A]): Future[Either[Result, UserRequest[A]]] = {
      Future.successful {
        if (request.user.userType == UserType.admin) {
          Right(request)
        } else {
          Left(Results.Forbidden("Only Admin can do."))
        }
      }
    }
  }

  @Singleton
  class CustomerAction @Inject()() extends ActionRefiner[Request, UserRequest] {

    val SessionTimeOut = 24 * 60 * 60 * 1000 //ms
    val logger = LoggerFactory.getLogger(getClass)

    protected def authUser(request: RequestHeader): Future[Option[rUser]] = {

      val session = request.session

      try {
        val ts = session.get(SessionKey.timestamp).get.toLong
        val uid = session.get(SessionKey.userId).get.toLong
        val userType = session.get(SessionKey.uType).get.toInt
        val nickName = session.get(SessionKey.nickName).get
        val headImg = session.get(SessionKey.headImg).get


        if (System.currentTimeMillis() - ts > SessionTimeOut) {
          Future.successful(None)
        } else {
          Future.successful(Some(rUser(uid, userType, nickName, headImg)))
        }
      } catch {
        case ex: Throwable =>
          logger.info("Not Login Yet.")
          Future.successful(None)
      }
    }


    protected def onUnauthorized(request: RequestHeader) =
      Results.Redirect(routes.Auth.loginPage()).withNewSession


    override protected def refine[A](request: Request[A]): Future[Either[Result, UserRequest[A]]] = {
      authUser(request).map {
        case Some(user) =>
          Right(new UserRequest(user, request))
        case _ =>
          Left(onUnauthorized(request))
      }
    }
  }

  @Singleton
  case class ActionUtils @Inject()(
                                    LoggingAction: LoggingAction,
                                    UserAction: UserAction,
                                    AdminAction: AdminAction,
                                    SystemAdminAction: SystemAdminAction,
                                    appSettings:AppSettings,
                                    system:ActorSystem,
                                    secureDAO: SecureDAO
                                    ) {
    val terraSecureKey = appSettings.terraSecureKey

    val log = Logger(this.getClass)

    private[this] val snRecords = new TrieMap[String, Long]()

    private def snLiveTimeInMillis = 3 * 60 * 1000

    def snCacheKey(appId: String, sn: String) = appId + "\u0001" + sn

    //lazy val 初始化在 MiamiAPI中
    lazy val cacheReducer = {
      //sn 1分钟刷新一次
      import concurrent.duration._
      system.scheduler.schedule(1 second, snLiveTimeInMillis / 3 millis) {
        val t = System.currentTimeMillis() - snLiveTimeInMillis
        val oldKeys = snRecords.filter(_._2 > t).keys
        snRecords --= oldKeys
      }
    }


    /**
     * 验证签名
      *
      * @param appid
     * @param sn
     * @param params
     * @param signature
     * @param action
     * @return
     */
    def checkSignature(
                        appid:String,
                        sn:String,
                        params: List[String],
                        signature: String
                        )(action: => EssentialAction): EssentialAction =
      EssentialAction { requestHeader =>
        val res = Await.result(secureDAO.getSecureKey(appid), 5 seconds)
        if (res.nonEmpty) {
          val secureKey = res.get
          val snKey = snCacheKey(appid, sn)
          if (snRecords.contains(snKey)) {
            snRecords(snKey) = System.currentTimeMillis()
            val result = Results.Ok(Json.obj("errCode" -> 1000101, "msg" -> "too frequently! please try later!"))
            Accumulator.done(result)
          }
          else {
            val expected = SecureUtil.generateSignature(params, secureKey)
            log.info(s"expect =$expected")
            val success = expected.equals(signature)
            if (success) {
              snRecords(snKey) = System.currentTimeMillis()
              action(requestHeader)
            }
            else {
              val result = Results.Ok(Json.obj("errCode" -> 1000102, "msg" -> "auth failed!"))
              Accumulator.done(result) // 'Done' means the Iteratee has completed its computations
            }
          }
        } else {
          val result = Results.Ok(Json.obj("errCode" -> 1000103, "msg" -> "appid does not exist!"))
          Accumulator.done(result) // 'Done' means the Iteratee has completed its computations
        }
      }

    def checkBasicSignature(
                        params: List[String],
                        signature: String,
                        secureKey: String
                      )(action: => EssentialAction): EssentialAction =
      EssentialAction { requestHeader =>

        val expected = SecureUtil.generateSignature(params, secureKey)
        log.debug(s"expected: ${expected}")
        log.debug(s"signature: ${signature}")
        val success = expected.equals(signature)
        log.debug(s"paras:$params,signature:$signature,secureKey:$secureKey,success=$success")
        if (success) {
          action(requestHeader)
        } else {
          val result = Results.Ok(Json.obj("errCode" -> 1000101, "msg" -> "signature error."))
          Accumulator.done(result)
        }
      }


  }




}