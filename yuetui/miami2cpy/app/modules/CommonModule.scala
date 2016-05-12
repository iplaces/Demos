package modules

import com.google.inject.AbstractModule
import common.AppSettings
import play.api.{Configuration, Environment}


/**
 * Created by ZYQ on 2016/3/7.
 **/
class CommonModule(
  environment: Environment,
  configuration: Configuration) extends AbstractModule {




  override def configure(): Unit = {

    bind(classOf[AppSettings]).asEagerSingleton()




  }


}


