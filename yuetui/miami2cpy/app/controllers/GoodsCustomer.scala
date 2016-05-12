package controllers

import javax.inject.{Inject, Named, Singleton}

import models.JsonProtocols
import models.dao.{StoreDAO, CategoryDAO, GoodDAO}
import models.tables.SlickTables
import models.tables.SlickTables.{rGoods, rStores}
import org.slf4j.LoggerFactory
import play.api.Logger
import play.api.libs.json.{JsValue, Writes, Json}
import play.api.mvc.{Action, Controller}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by wangchunze on 2016/3/10.
 */

@Singleton
class GoodsCustomer @Inject()(val goodDAO: GoodDAO,
                              val storeDAO: StoreDAO,
                              val categoryDAO:CategoryDAO,
                              val storeDao:StoreDAO
                          )extends Controller with JsonProtocols{

private val logger = LoggerFactory.getLogger(this.getClass)



  /**
   * 获取所有的餐厅分类
   * @return
   */
  def listStoreCat()=Action.async{implicit request=>
   categoryDAO.listStoreCat().map{res=>
     Ok(successResult(Json.obj("data"->res)))
   }
  }


  /**
   * 获取餐厅的菜品和标签
   * sort 1:价格 2：销量 其它：默认
   * @param storeId sort
   * @return
   */
  def listGood(storeId:Long,sort:Int)=Action.async { implicit request =>
    val catFuture=categoryDAO.listGoodCat(storeId).map{cat=>
      cat
    }
    val goodFuture=goodDAO.getGoodsFromStore(storeId).map { res =>
        sort match {
          //排序后的good列表
          case 1 => res.sortBy(_.price) //价格
          case 2 => res.sortBy(_.sales) //销量
          case _ => res //默认
        }
    }

    for{cat <- catFuture
        good <- goodFuture
    }yield{
      Ok(successResult(Json.obj("data" ->Json.obj("category"->cat,"good"->good)))).as(withCharset(JSON))
    }

  }


  /**
   * 获取餐厅的菜品 按标签分类
   * sort 1:价格 2：销量 其它：默认
   * @param storeId sort
   * @return
   */
  def listGoodWithCate(storeId:Long,sort:Int)=Action.async { implicit request =>
    val catFuture=categoryDAO.listGoodCat(storeId).map{cat=>
      cat
    }
    val goodFuture=goodDAO.getGoodsFromStore(storeId).map { res =>
      res
    }

    for{cat <- catFuture
        good <- goodFuture
    }yield{
      val data=good.filter(g=>cat.exists(c=>c.id==g.catId)).groupBy(_.catId).map{r=>
        val cateId=r._1
        val cate=cat.find(c=>c.id==cateId).getOrElse(SlickTables.rCategories(0l,"","",0L,0))
        val good=sort match {
          //排序后的good列表
          case 1 => r._2.sortBy(_.price) //价格
          case 2 => r._2.sortBy(_.sales).reverse //销量
          case _ => r._2 //默认
        }
        (cate,good)
      }.toList.sortBy(_._1.rank).reverse.map{r=>
        Json.obj(
          "category"->r._1,
          "goods"->r._2
        )
      }
      Ok(successResult(Json.obj("data" ->data)))
    }
  }


  /**
   * 搜索美食和餐厅
   * @param searchKey
   * @return 相关美食和归属的店铺
   */
  def searchGood(searchKey:String)=Action.async{implicit request=>
      goodDAO.searchGood(searchKey).flatMap{good=>
      storeDao.searchStore(searchKey).flatMap {store=>
        Future.sequence(good.groupBy(_.storeId).map { res =>
          storeDAO.getStoreById(res._1).map { store => //根据storeId获得餐厅信息
            Json.obj("store" -> store)
          }.map { storeJson =>
            Json.obj("good" -> res._2) ++ storeJson //将餐厅和美食list封装json对象
          }
        }).map { resJson =>
          Ok(successResult(Json.obj("data" -> Json.obj("good"->resJson,"store"->store))))
        }
      }
    }
  }






}
