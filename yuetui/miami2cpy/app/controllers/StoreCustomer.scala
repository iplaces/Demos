package controllers

import javax.inject.{Inject, Named, Singleton}

import models.JsonProtocols
import models.dao.{StoreDAO, CategoryDAO, GoodDAO}
import models.tables.SlickTables.rStores
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
class StoreCustomer @Inject()(storeDao:StoreDAO,
                             categoryDAO:CategoryDAO
                           )extends Controller with JsonProtocols{
  private val logger = LoggerFactory.getLogger(this.getClass)

  private val pageSize=20  //餐厅每页显示的数量


  /**
   * 根据分类获取餐厅
   * catId为0 获取全部  page:页码
   * sort 1:销量 2:评分 3:配送速度 4:起送价 其它:默认
    *
    * @param catId
   * @return
   */
  def getStoreByCat(catId:Long,sort:Int,page:Option[Int])=Action.async{implicit request=>
    val curPage= if(page.getOrElse(1)>0) page.getOrElse(1) else 1 //获取当前页码 如果page不存在或不合法 默认page=1
    storeDao.getStoreSumByCat(catId).flatMap{cnt=>                //总条数
      val pageCount=cnt / pageSize + (if(cnt % pageSize==0) 0 else 1)  //页数
      storeDao.listStoreByCat(catId,sort,curPage,pageSize).map{res=>
        Ok(successResult(Json.obj("curPage"->curPage,"pageCount"->pageCount,"data"->res)))
      }
    }
  }


  /**
   * 获取餐厅信息
    *
    * @param storeId
   * @return
   */
  def getStoreInfo(storeId:Long)=Action.async{implicit request=>
    storeDao.getStoreById(storeId).map{res=>
      logger.info(s"-----$res")
      Ok(successResult(Json.obj("data"->res)))
    }
  }

  /**
   * 搜索餐厅
    *
    * @param searchKey
   * @return
   */
  def searchStore(searchKey:String)=Action.async{implicit request=>
    storeDao.searchStore(searchKey).map{res=>
      Ok(successResult(Json.obj("data"->res)))
    }
  }

  /**
   * 获取所有餐厅 提供给仇科凯首页展示
    *
    * @param sort  1:销量 2:评分 3:配送速度 4:起送价 其它:默认
   * @return
   */
  def storeListForHome(sort:Int,num:Option[Int])=Action.async{implicit request=>
    val callBack=request.getQueryString("callback").getOrElse("")
    storeDao.listStoreAll(0).flatMap{storeSeq=>
      Future.sequence(storeSeq.groupBy(_.catId).map{res=>
        categoryDAO.getCatById(res._1).map{cat=>
          Json.obj(
            "typeName"-> cat.get.name,
            "list"-> res._2.take(num.getOrElse(4)).map{store=>
              Json.obj("name"->store.name,"url"->store.id)
            })
        }
      })
    }.map{res=>
      Ok(callBack+'('+successResult(Json.obj("divName"->"外卖","list"->res))+')')
    }
  }


}
