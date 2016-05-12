package models

import models.tables.SlickTables._
import play.api.libs.json._
import utils.TimeUtil._
/**
 * User: Taoz
 * Date: 8/18/2015
 * Time: 12:10 PM
 */
trait JsonProtocols {

  object ErrorCode {
    val signatureError = 1000101
    val jsonFormatError = 1000102
    val requestAsJsonEmpty = jsonResult(1000103, "receive empty request body")
    val operationTimeOut = jsonResult(1000104, "operation time out")
    val unknownMessage = jsonResult(1000105,"unknown messages.")

    val categoryInsertFailed = jsonResult(1000201, "failed to insert category into database")
    val categoryDeleteFailed = jsonResult(1000202, "failed to delete category in database")
    val categoryExistGoods  = jsonResult(1000203, "please move the goods away, then delete the category")

    val goodInsertFailed = jsonResult(1000301, "failed to insert good into database")
    val goodDeleteFailed = jsonResult(1000302, "failed to delete good in database")
    val goodUpFailed = jsonResult(1000303, "failed to up good in database")
    val goodOffFailed = jsonResult(1000304, "failed to off good in database")
    val goodAddStockFailed = jsonResult(1000305, "failed to add good stock in database")
    val goodEditFailed = jsonResult(1000306, "failed to edit good into database")

    val uploadImageEmptyForm = jsonResult(1000000, "No image file.")
    val uploadImageFailed = jsonResult(1000001, "upload image failed.")

    val storeInsertFailed = jsonResult(1000401, "failed to insert store into database")
    val storeDeleteFailed = jsonResult(1000402, "failed to delete store in database")
    val storeEditedFailed = jsonResult(1000403, "failed to edit store in database")
    val storeNonExisted   = jsonResult(1000404, "store may no longer exist")
    val storeExistFailed = jsonResult(1000405, "the account has been created!")

    val orderCreateFailed = jsonResult(1000501,"failed to create order!")
    val changeStateFailed = jsonResult(1000502,"failed to change the order state!")
    val orderNotExist     = jsonResult(1000503,"the order does not exist!")
    val orderCancelFailed = jsonResult(1000504,"failed to cancel the order!")
    val orderRefundFailed = jsonResult(1000505,"failed to apply for refund!")

    val userNotLogin          = jsonResult(1000601,"the customer does not login!")
    val customerIdMismatch    = jsonResult(1000602,"the customer id isn't matched!")
    val storeIdMismatch       = jsonResult(1000603, "the store id isn't matched!")

    val commentCreateFailed=jsonResult(1000701,"create the comment failed!")

    val refundCreateFailed = jsonResult(1000711,"create the refund failed!")

    val openMiamiFailed  = jsonResult(1000801,"open miami service failed!")
    val disableMiamiFailed=jsonResult(1000802,"disable miami service failed!")

    val payAmountMismatch = jsonResult(1000901, "pay amount mismatch!")


  }



  def jsonResult(errorCode: Int, errorMsg: String) = {
    Json.obj("errCode" -> errorCode, "msg" -> errorMsg)
  }

  def jsonResult(errorCode: Int, errorMsg: String, data: JsObject) = {
    Json.obj("errCode" -> errorCode, "msg" -> errorMsg) ++ data
  }

  def successResult(data: JsObject) = success ++ data

  val success = jsonResult(0, "ok")

  implicit val rCategories: Writes[rCategories] = new Writes[rCategories] {
    override def writes(obj: rCategories): JsValue = {
      Json.obj(
        "id" -> obj.id,
        "name" -> obj.name,
        "icon" -> obj.icon,
        "store_id" -> obj.storeId,
        "rank" -> obj.rank
      )
    }
  }

  implicit val rGoods: Writes[rGoods] = new Writes[rGoods] {
    override def writes(obj: rGoods): JsValue = {
      Json.obj(
        "id" -> obj.id,
        "store_id" -> obj.storeId,
        "cat_id" -> obj.catId,
        "name" -> obj.name,
        "price" -> obj.price,
        "sale_price" -> obj.salePrice,
        "description" -> obj.description,
        "icon" -> obj.icon,
        "stock" -> obj.stock,
        "sales" -> obj.sales,
        "state" -> obj.state,
        "create_time" -> obj.createTime
      )
    }
  }

  implicit val rStores: Writes[rStores] = new Writes[rStores] {
    override def writes(obj: rStores): JsValue = {
      Json.obj(
        "id" -> obj.id,
        "name" -> obj.name,
        "description" -> obj.description,
        "contact" -> obj.contact,
        "address" -> obj.address,
        "icon" -> obj.icon,
        "open_from" -> obj.openFrom,
        "open_to" -> obj.openTo,
        "base_price" -> obj.basePrice,
        "pack_fee" -> obj.packFee,
        "cat_id" -> obj.catId,
        "sales" -> obj.sales,
        "comments" -> obj.comments,
        "grades" -> obj.grades.toString,
        "cost_time" -> obj.costTime,
        "state" -> obj.state,
        "create_time" -> obj.createTime,
        "modified_time" -> obj.modifiedTime
      )
    }
  }

  implicit val rOrders: Writes[rOrders] = new Writes[rOrders] {
    override def writes(obj: rOrders): JsValue = {
      Json.obj(
        "id" -> obj.id,
        "store_id" -> obj.storeId,
        "customer_id" -> obj.customerId,
        "recipient" -> obj.recipient,
        "address" -> obj.address,
        "contact" -> obj.contact,
        "remark" -> obj.remark,
        "pack_fee" -> obj.packFee,
        "total_fee" -> obj.totalFee,
        "pay_status"->obj.payStatus,
        "state" -> obj.state,
        "trade_no" -> obj.tradeNo,
        "arrive_time" -> obj.arriveTime,
        "create_time" -> obj.createTime
      )
    }
  }

  implicit val rOrderGoods:Writes[rOrderGoods] = new Writes[rOrderGoods]{
    override def writes(obj:rOrderGoods):JsValue = {
      Json.obj(
      "id"->obj.id,
      "orderId"->obj.orderId,
      "goodsId"->obj.goodId,
      "num"->obj.num
      )
    }
  }

  implicit val rComments:Writes[rComments] = new Writes[rComments]{
    override def writes(obj:rComments):JsValue = {
      Json.obj(
      "id"->obj.id,
      "orderId"->obj.orderId,
      "transTime"->obj.transTime,
      "dishGrade"->obj.dishGrade,
      "storeId"->obj.storeId,
      "createTime"->obj.createTime
      )
    }
  }

}