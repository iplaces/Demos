package modules

import actor.{OrderManager, StorePushActor, UpdateOrderActor, UpdateStoreActor}
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport

/**
 * Created by ZYQ on 2016/3/10.
 **/
class ActorModule extends AbstractModule with AkkaGuiceSupport{

  override def configure() : Unit = {
//    bindActor[UpdateOrderActor]("updateOrderActor")
    bindActor[UpdateStoreActor]("updateStoreActor")
    bindActor[StorePushActor]("storePushActor")

    bindActor[OrderManager]("configured-OrderManager")

  }

}
