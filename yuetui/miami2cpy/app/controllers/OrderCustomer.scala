package controllers

import _root_.util.CommentClient
import actor.OrderHandler.ConfirmDishArrived
import actor.OrderManager._
import actor.UpdateOrderActor.CreateOrder
import actor.{OrderManager, WebSocketActor}
import akka.actor.{ActorRef, ActorSystem}
import akka.stream.Materializer
import akka.util.{ByteString, Timeout}
import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}
import common.Constants._
import common.{AppSettings, _}
import models.JsonProtocols
import models.dao.OrderDAO.OrderState
import models.dao.{CommentDAO, GoodDAO, OrderDAO, StoreDAO}
import models.tables.SlickTables
import models.tables.SlickTables.{rOrders, rStores}
import play.api.Logger
import play.api.libs.iteratee.{Input, Done, Iteratee}
import play.api.libs.json._
import play.api.libs.streams.{Accumulator, ActorFlow}
import play.api.mvc._
import utils.{MammonClient, TerraClient}
import akka.pattern.ask
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
/**
 * Created by wangchunze on 2016/3/14.
 */

@Singleton
class OrderCustomer @Inject()(orderDAO: OrderDAO,
                              storeDAO:StoreDAO,
                              goodDAO:GoodDAO,
                              commentDAO: CommentDAO,
                              actionUtils: ActionUtils,
                              terraClient: TerraClient,
                              appSettings: AppSettings,
                              system: ActorSystem,
                              mat: Materializer,
                              commentClient:CommentClient,
                              mammonClient: MammonClient,
//                              @Named("updateOrderActor") updateOrderActor:ActorRef,
                              @Named("storePushActor") storePushActor:ActorRef,
                              @Named("configured-OrderManager") orderManager: ActorRef
                               ) extends Controller  with JsonProtocols{
  import actionUtils._

  private[this] val logger = Logger(this.getClass)
  private val userAction = LoggingAction andThen UserAction
  val baseUrl = appSettings.terraProtocol + "://" + appSettings.terraDomain
  val appId = appSettings.terraAppId
  private val pageSize=20  //每页显示的数量

  implicit val implicitMaterializer: Materializer = mat
  implicit val implicitActorSystem: ActorSystem = system

  implicit val timeout = Timeout(15.seconds)

  def socket(storeId:Long) = WebSocket.accept[String, String] { implicit request =>
    ActorFlow.actorRef{ out=>
    storePushActor ! AddStoreActorRef(out,storeId)
    WebSocketActor.props(out)
    }
  }

  def send(storeId:Long,orderId:Long)=Action.async{implicit request=>
    storePushActor ! SendNewOrder(storeId,orderId)
    Future.successful(Ok(success))
  }


  /**
   * 创建订单
 *
   * @return
   */
  def createOrder=Action.async{implicit request=>
    request.session.get(SessionKey.userId) match{
//    Option("10003") match {
      case Some(customerId)=>
        request.body.asJson match {
          case Some(order)=> //订单存在
           val storeId=    (order \ "storeId").as[Long]       //餐厅id
           val recipient=  (order \ "recipient").as[String]   //收饭人
           val address=    (order \ "address").as[String]     //地址
           val contact=    (order \ "contact").as[String]     //联系方式
           val remark=     (order \ "remark").as[String]      //备注
           val payStatus=  (order \ "payStatus").as[Int]      //0:货到付款 1:在线支付
           val arriveTime= (order \ "arriveTime").as[Long]    //预期送达时间
           val goods=   (order \ "goodsDetail").as[JsObject]  // 格式 Json.obj("1000001"->4,"1000002"->3)
           val createTime = System.currentTimeMillis()        //下单时间
           val goodsList = goods.fieldSet.toList.map(x=>(x._1.toLong,Json.stringify(x._2).toInt))
           val state= OrderState.WaitingPay.id //if(payStatus==0) OrderState.WaitingAccept.id else OrderState.WaitingPay.id
            if(goodsList.nonEmpty) { //检测菜品是否为空
              storeDAO.getStoreById(storeId).flatMap { res => //获取餐厅信息 以计算配送费等
                if (res.isDefined) {
                  val packFee = res.get.packFee //通过storeId得到配送费

                  val totalListFuture = Future.sequence(//根据菜品id拿到价格 Future[List]类型
                    goodsList.map { goods =>
                      val goodsId = goods._1
                      val goodNum = goods._2
                      goodDAO.getGoodsById(goodsId).map { goodsRes =>
                        if (goodsRes.isDefined) {
                          goodsRes.get.salePrice * goodNum
                        } else {
                          0
                        }
                      }
                    })

                  totalListFuture.flatMap { totalList =>
                    val totalFee = totalList.sum + packFee //总价格
                    val order=SlickTables.rOrders(
                    -1l,
                    storeId,
                    customerId.toLong,
                    recipient,
                    address,
                    contact,
                    remark,
                    packFee,
                    totalFee,
                    payStatus,
                    state,
                    tradeNo="",
                    arriveTime,
                    createTime
                    )

                    (orderManager ? PlaceOrder(order,goodsList)).flatMap{
                      case OrderCreated(orderId)=>
//                        updateOrderActor ! CreateOrder(orderId)         //发送给定时任务
//                        storePushActor ! SendNewOrder(storeId, orderId) //发送给消息推送
                        mammonClient.preCreate(orderId,order.totalFee,order.address).map{
                          case Right(json)=>
                            val url=mammonClient.pay(orderId)
                            logger.info(s"------url=$url")
                            Ok(successResult(Json.obj("data" -> url)))
                          case Left(json)=>
                            Ok(json)
                        }
                      case OrderCreateFailed=>
                        Future.successful(Ok(ErrorCode.orderCreateFailed))
                      case _=>
                        Future.successful(Ok(ErrorCode.orderCreateFailed))
                    }
                  }
                } else {
                  Future.successful(Ok(ErrorCode.orderCreateFailed))
                }
              }
            }else{
              Future.successful(Ok(ErrorCode.orderCreateFailed))
            }

          case None=> //订单不存在
            Future.successful(Ok(ErrorCode.requestAsJsonEmpty))
        }
      case None=>
        Future.successful(Ok(ErrorCode.userNotLogin++Json.obj("url"->
          (baseUrl+"/terra/login?redirect_url="+java.net.URLEncoder.encode(baseUrl+"/miami/&appid="+appId, "UTF-8")))))
    }

  }

  /**
   * 用户查看自己的订单列表
 *
   * @return
   */
  def getOrderByCustomer(page:Option[Int])= Action.async { implicit request =>
              request.session.get(SessionKey.userId) match {
//      Option("10004") match {
        case Some(customerId) =>
          val curPage = if (page.getOrElse(1) > 0) page.getOrElse(1) else 1
          orderDAO.getOrderNumByCustomer(customerId.toLong).flatMap { cnt =>
            val pageCount = cnt / pageSize + (if (cnt % pageSize == 0) 0 else 1) //页数
            orderDAO.listOrderByCustomerWithDetail(customerId.toLong, curPage, pageSize).flatMap { seq =>
              Future.sequence(seq.groupBy(_._2.orderId).toList
                .sortBy(t => t._2.head._1.get.createTime).reverse.map { r =>
                val orderGoods = r._2.map(_._2)
                val order = r._2.head._1.get

                //餐厅信息
                val storeInfoFuture = storeDAO.getStoreById(order.storeId).filter(_.nonEmpty).map { store =>
                  Json.obj(
                    "storeId" -> store.get.id,
                    "name" -> store.get.name,
                    "icon" -> store.get.icon
                  )
                }

                //订单中的菜品信息
                val goodsDetailFuture = Future.sequence(orderGoods.map { orderGoods =>
                  goodDAO.getGoodsById(orderGoods.goodId).filter(_.nonEmpty).map { goods =>
                    Json.obj(
                      "orderGoodsId" -> orderGoods.id,
                      "orderId" -> orderGoods.orderId,
                      "goodsId" -> orderGoods.goodId,
                      "goodsName" -> goods.get.name,
                      "price" -> goods.get.salePrice,
                      "icon" -> goods.get.icon,
                      "num" -> orderGoods.num
                    )
                  }
                })

                for {
                  store <- storeInfoFuture
                  goodsDetail <- goodsDetailFuture
                } yield {
                  Json.obj(
                    "orderId" -> order.id,
                    "store" -> store,
                    "customerId" -> order.customerId,
                    "recipient" -> order.recipient,
                    "address" -> order.address,
                    "contact" -> order.contact,
                    "remark" -> order.remark,
                    "packFee" -> order.packFee,
                    "totalFee" -> order.totalFee,
                    "state" -> order.state,
                    "tradeNo" -> order.tradeNo,
                    "arriveTime" -> order.arriveTime,
                    "createTime" -> order.createTime
                  ) ++ Json.obj("goodsDetail" -> goodsDetail)

                }
              }).map { data =>
                Ok(successResult(Json.obj("curPage" -> curPage, "pageCount" -> pageCount, "data" -> data)))
              }
            }
          }

        case None =>
          Future.successful(Ok(ErrorCode.userNotLogin ++ Json.obj("url" ->
            (baseUrl + "/terra/login?redirect_url=" + java.net.URLEncoder.encode(baseUrl + "/miami/#/order/list", "UTF-8") + "&appid=" + appId))))
      }
    }

  /**
   * 获取订单详情，包括餐厅信息 菜品信息 评价信息。用于订单列表中点击订单详情时显示
 *
   * @param orderId
   * @return
   */
  def getOrderDetail(orderId: Long) = Action.async { implicit request =>
    request.session.get(SessionKey.userId) match {
//          Option("10003") match {
      case Some(customerId) =>
        orderDAO.getOrderById(orderId).flatMap {
          case Some(order) =>
            //订单菜品详情
            val orderGoodsFuture = orderDAO.getGoodsByOrderId(orderId).map { seq =>
              seq.map { goods =>
                Json.obj(
                  "orderGoodsId" -> goods._1.id,
                  "orderId" -> goods._1.orderId,
                  "goodsId" -> goods._1.goodId,
                  "goodsName" -> goods._2.name,
                  "price" -> goods._2.salePrice,
                  "icon" -> goods._2.icon,
                  "num" -> goods._1.num
                )
              }
            }

            //获取餐厅信息
            val storeFuture = storeDAO.getStoreById(order.storeId).map {
              case Some(store) =>
                Json.obj(
                  "storeId" -> store.id,
                  "name" -> store.name,
                  "icon" -> store.icon,
                  "description" -> store.description,
                  "contact" -> store.contact
                )
              case None =>
                JsNull
            }

            val commentFuture = commentClient.getCommentsByItem(orderId.toString).map { res =>
              val errorCode = (res \ "errCode").as[Int]
              if (errorCode == 0) {
                val result = (res \ "result").as[Seq[JsObject]]
                if (result.length > 0) {
                  val msg = result.head
                  val extendContent = (msg \ "extendContent").as[String]
                  val transTime = scala.util.parsing.json.JSON.parseFull(extendContent) match {
                    case Some(e: Map[String, String]) =>
                      val extendContentJson = Json.parse(extendContent)
                      (extendContentJson \ "transTime").asOpt[Int].getOrElse(0)
                    case None =>
                      0
                  }
                  val dishGrade = (msg \ "grade").as[Int]
                  val picUrl = (msg \ "picUrl").as[String].split("#")
                  (msg ++ Json.obj(
                    "transTime" -> transTime,
                    "dishGrade" -> dishGrade,
                    "picUrl" -> picUrl
                  )).-("extendContent").-("grade")
                } else {
                  JsNull
                }
              } else {
                JsNull
              }
            }

            for {
              orderGoods <- orderGoodsFuture
              store <- storeFuture
              comment <- commentFuture
            } yield {
              val data = Json.obj(
                "orderId" -> order.id,
                "store" -> store,
                "customerId" -> order.customerId,
                "recipient" -> order.recipient,
                "address" -> order.address,
                "contact" -> order.contact,
                "remark" -> order.remark,
                "packFee" -> order.packFee,
                "totalFee" -> order.totalFee,
                "state" -> order.state,
                "tradeNo" -> order.tradeNo,
                "arriveTime" -> order.arriveTime,
                "createTime" -> order.createTime,
                "goodsDetail" -> orderGoods,
                "comment" -> comment
              )
              Ok(successResult(Json.obj("data" -> data)))
            }

          case None =>
            Future.successful(Ok(ErrorCode.orderNotExist))
        }

      case None =>
        Future.successful(Ok(ErrorCode.userNotLogin ++ Json.obj("url" ->
          (baseUrl + "/terra/login?redirect_url=" + java.net.URLEncoder.encode(baseUrl + "/miami/&appid=" + appId, "UTF-8")))))
    }
  }



  /**
   * 用户确认订单操作
   * 该操作会改变订单中菜品相应的销量和库存，改变餐厅的销量
 *
   * @param orderId
   * @return
   */

  def confirmReceipt(orderId:Long,storeId:Long)=Action.async { implicit request =>
        request.session.get(SessionKey.userId) match {
//    Option("10004") match {
      case Some(customerId) =>
        (orderManager ? ConfirmDishArrived(orderId,storeId)).map{
          case OperationSucceed=>
            orderDAO.increaseSales(orderId)
            Ok(success)
          case UpdateStateFailed=>
            Ok(ErrorCode.changeStateFailed)
        }
      case None =>
        Future.successful(Ok(ErrorCode.userNotLogin++Json.obj("url"->
          (baseUrl+"/terra/login?redirect_url="+java.net.URLEncoder.encode(baseUrl+"/miami/&appid="+appId, "UTF-8")))))
    }
  }


  /**
   * 取消订单 只有未支付或货到付款未接单的情况下可取消
 *
   * @param orderId
   * @return
   */
  def cancelOrder(orderId:Long)=Action.async{implicit request=>
    orderDAO.getOrderById(orderId).flatMap{
      case Some(order)=>
        if((order.payStatus==1 && order.state==OrderState.WaitingPay.id)
           ||(order.payStatus==0 && order.state==OrderState.WaitingAccept.id)){
          orderDAO.changeState(orderId,OrderState.OrderCanceled.id).map{res=>
            if(res>0){
              storePushActor ! SendCancelOrder(order.storeId,order.id)
              Ok(success)
            }else{
              Ok(ErrorCode.orderCancelFailed)
            }
          }
        }else{
          Future.successful(Ok(ErrorCode.orderCancelFailed))
        }
      case None=>
        Future.successful(Ok(ErrorCode.orderNotExist++Json.obj("url"->
          (baseUrl+"/terra/login?redirect_url="+java.net.URLEncoder.encode(baseUrl+"/miami/&appid="+appId, "UTF-8")))))
    }
  }




  /**
   * 获取用户的收货地址
 *
   * @return
   */
  def getCustomerAddress(storeId:Long)=Action.async{implicit request=>
    request.session.get(SessionKey.userId) match {
//    Option("10003") match {
      case Some(customerId) =>
        terraClient.getCustomerAddress(customerId.toLong).map { res =>
          Ok(res)
        }
      case None=>
        Future.successful(Ok(ErrorCode.userNotLogin++Json.obj("url"->
          (baseUrl+"/terra/login?redirect_url="+java.net.URLEncoder.encode(baseUrl+"/miami/#/order/checkout/"+storeId,"UTF-8")+"&appid="+appId))))
    }
  }


  /**
   * 设置用户的收货地址
   * 格式 Json.obj("name" -> "zhushighao", "address" -> "坤讯大厦", "phone" -> "13520358713")
 *
   * @return
   */
  def setCustomerAddress=Action.async { implicit request =>
    request.session.get(SessionKey.userId) match{
//    Option("10004") match {
      case Some(customerId) =>
        request.body.asJson match {
          case Some(addresses) =>
            val name = (addresses \ "name").as[String]
            val address = (addresses \ "address").as[String]
            val phone = (addresses \ "phone").as[String]
            terraClient.setCustomerAddress(customerId.toLong, name, address, phone).map { res =>
              Ok(res)
            }
          case None =>
            Future.successful(Ok(ErrorCode.requestAsJsonEmpty))
        }

      case None =>
        Future.successful(Ok(ErrorCode.userNotLogin++Json.obj("url"->
          (baseUrl+"/terra/login?redirect_url="+java.net.URLEncoder.encode(baseUrl+"/miami/&appid="+appId, "UTF-8")))))
    }
  }

  /**
   * 修改用户地址接口
 *
   * @return
   */
  def modifyCustomerAddress=Action.async{implicit request=>
    request.session.get(SessionKey.userId) match{
//    Option("10004") match {
      case Some(customerId)=>
//        Option(Json.obj("addressid"->2L,"name" -> "zhushighao", "address" -> "坤讯大厦", "phone" -> "13520358713")) match{
        request.body.asJson match{
          case Some(addresses) =>
            val addressid=(addresses \ "addressid").as[Long]
            val name = (addresses \ "name").as[String]
            val address = (addresses \ "address").as[String]
            val phone = (addresses \ "phone").as[String]
            terraClient.modifyCustomerAddress(addressid,customerId.toLong, name, address, phone).map { res =>
              Ok(res)
            }
          case None=>
            Future.successful(Ok(ErrorCode.requestAsJsonEmpty))
        }
      case None=>
        Future.successful(Ok(ErrorCode.userNotLogin++Json.obj("url"->
          (baseUrl+"/terra/login?redirect_url="+java.net.URLEncoder.encode(baseUrl+"/miami/&appid="+appId, "UTF-8")))))
    }
  }

  /**
   * 删除用户地址
 *
   * @param addressId
   * @return
   */
  def deleteCustomerAddress(addressId:Long)=Action.async{implicit request=>
    request.session.get(SessionKey.userId) match{
//      Option("10004") match {
      case Some(customerId)=>
            terraClient.deleteCustomerAddress(customerId.toLong,addressId).map { res =>
              Ok(res)
            }
      case None=>
        Future.successful(Ok(ErrorCode.userNotLogin++Json.obj("url"->
          (baseUrl+"/terra/login?redirect_url="+java.net.URLEncoder.encode(baseUrl+"/miami/&appid="+appId, "UTF-8")))))
    }
  }















}


