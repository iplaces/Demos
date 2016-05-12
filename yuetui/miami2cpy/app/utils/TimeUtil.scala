package utils

import java.sql.Date
import java.text.SimpleDateFormat

import scala.collection.immutable.HashMap

/**
 * Created by wangchunze on 2016/3/23.
 */
object TimeUtil {

  def format(timeMs:Long,format:String = "yyyy-MM-dd HH:mm:ss") ={
    val data  = new Date(timeMs)
    val simpleDateFormat = new SimpleDateFormat(format)
    simpleDateFormat.format(data)
  }

  def getMinuteOfNow={
    val data  = new Date(System.currentTimeMillis())
    val format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
    format.format(data).split("-")(4).toInt
  }

  /**
   * 格式化时间 输入时某天开始的分钟数
   * @param minute
   */
  def formatFromMinute(minute:Long)={
    import com.github.nscala_time.time.Imports._
    val triggerTime = DateTime.now.hour(0).minute(0).second(0).getMillis
    format(triggerTime+minute*60*1000,"HH:mm:ss")
  }

  def main(args: Array[String]) {
//    println(getMinuteOfNow)
//    println(formatFromMinute(80))
//    var map=collection.mutable.HashMap[Int,String]()
//    map.put(1,"aaa")
//    map.put(1,"bbb")
//    map.put(1,"ccc")
//    println(map)
  }

}
