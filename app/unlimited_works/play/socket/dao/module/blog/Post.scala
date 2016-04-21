package unlimited_works.play.socket.dao.module.blog

import lorance.rxscoket.presentation.json.IdentityTask
import unlimited_works.play.socket.DaoCommunicate

import scala.concurrent.{Promise, Future}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  *
  */
object Post {
  //todo protocol not match
  def get(id: String) = {
    DaoCommunicate.modelSocket.flatMap{jp =>
      val rstObv = jp.sendWithResult[PostRsp, PostReq](PostReq(id),
        Some(x => x.takeWhile(rsp => rsp.result.nonEmpty)))//.onErrorResumeNext{
//        case e: java.nio.channels.ClosedChannelException =>
//          DaoCommunicate.reconnect.
//      } todo deal with reconnect if server disconnect

      val p = Promise[Post]
      rstObv.subscribe(x => p.trySuccess(x.result.get), e => p.tryFailure(e))
      p.future
    }
  }

  case class PostReq(id: String, taskId: String = "blog/post") extends IdentityTask
  case class PostRsp(result: Option[Post], taskId: String) extends IdentityTask
  case class Post(pen_name: String, title: String, issue_time: String, introduction: String, body: String)
}
