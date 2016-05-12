package actor

/**
 * User: Taoz
 * Date: 12/5/2015
 * Time: 11:28 PM
 */
trait ActorResponse


case class Ok() extends ActorResponse

case class Failed(msg: String) extends ActorResponse


object TrueRsp extends ActorResponse

object FalseRsp extends ActorResponse

object NotSupportYet extends ActorResponse

object ActorOperateInternalError extends ActorResponse


object FinishWork