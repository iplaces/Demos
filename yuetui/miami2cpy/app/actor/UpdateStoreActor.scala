package actor

import actor.UpdateStoreActor.GradeUpdate
import akka.actor.Actor
import com.google.inject.{Inject, Singleton}
import models.dao.{StoreDAO, CommentDAO, OrderDAO}
import play.api.Logger
import scala.concurrent.ExecutionContext.Implicits.global
/**
 * Created by wangchunze on 2016/3/21.
 */
@Singleton
class UpdateStoreActor @Inject()(
                                orderDAO:OrderDAO,
                                storeDAO:StoreDAO,
                                commentDAO:CommentDAO
                                  )extends Actor{
  import concurrent.duration._
  private[this] val log=Logger(getClass)

  @throws[Exception](classOf[Exception])
  override def preStart():Unit={
    log.info(s"${self.path.name} actor starting...")
  }

  override def postStop():Unit={
    log.info(s"${self.path.name} actor stopping...")
  }

  private def dataDelay = {
    import com.github.nscala_time.time.Imports._
    val cur = System.currentTimeMillis()
    val triggerTime = DateTime.now.hour(23).minute(59).second(59)
    log.info(s"The first data add will at $triggerTime")
    triggerTime.getMillis - cur + 1 * 1000
  }

  private val updateStoreGrade = context.system.scheduler.schedule(10 millis,  1 days, self, GradeUpdate)

  override def receive:Receive={
    //更新评分和评论数
    case GradeUpdate =>
      storeDAO.listStoreAll(0).map{storeSeq=>
        storeSeq.map{store=>
          val storeId=store.id
          commentDAO.getAllByStoreId(storeId).map{commentSeq=>
            val length=commentSeq.length
            val sum=commentSeq.map(_.dishGrade).sum //总数量
            val gradeAve=sum.toFloat/length
            val grade=BigDecimal.decimal(gradeAve).setScale(2, BigDecimal.RoundingMode.HALF_UP)
            storeDAO.updateGrade(storeId,grade.toFloat)  //更新评分
            storeDAO.updateComments(storeId,sum)    //更新评论数
          }
        }
      }




    case unknow@_ =>log.info("unknow message:"+unknow+"in"+self.path.name+"from"+context.sender().path.name)
  }

}

object UpdateStoreActor{

  /**
   * 定时更新店铺评分和评论数
   */
  case class GradeUpdate()
}
