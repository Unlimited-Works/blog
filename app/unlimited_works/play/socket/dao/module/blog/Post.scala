package unlimited_works.play.socket.dao.module.blog

import lorance.rxscoket.presentation.json.IdentityTask
import unlimited_works.play.socket.DaoCommunicate

import scala.concurrent.{Promise, Future}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  *
  */
object Post {
  def get(postId: String) = {
    DaoCommunicate.modelSocket.flatMap{jp =>
      val rstObv = jp.sendWithResult[PostRsp, PostReq](PostReq(postId),
        Some(x => x.takeWhile(rsp => rsp.result.nonEmpty)))//.onErrorResumeNext{
//        case e: java.nio.channels.ClosedChannelException =>
//          DaoCommunicate.reconnect.
//      } todo deal with reconnect if server disconnect

      val p = Promise[Post]
      rstObv.subscribe(x => p.trySuccess(x.result.get), e => p.tryFailure(e))
      p.future
    }
  }

  case class PostReq(id: String, model: String = "blog/post", taskId: String = lorance.rxscoket.presentation.getTaskId) extends IdentityTask
  case class PostRsp(result: Option[Post], taskId: String) extends IdentityTask
  case class Post(pen_name: String, title: String, issue_time: String, introduction: String, body: String, share_sha: Option[String])

  def modifyAccess(postId: String, toPublic: Boolean) = {
    DaoCommunicate.modelSocket.flatMap{jProto =>
      val rstObv = jProto.sendWithResult[PostAccessRsp, PostAccessReq](PostAccessReq(postId, toPublic), Some(x => x.first))

      val p = Promise[Option[String]]
      rstObv.subscribe(x => p.trySuccess(x.share_sha), e => p.tryFailure(e))
      p.future
    }
  }

  case class PostAccessReq(postId: String, toPublic: Boolean, model: String = "blog/post/modify/access", taskId: String = lorance.rxscoket.presentation.getTaskId) extends IdentityTask
  case class PostAccessRsp(error: Option[String], share_sha: Option[String], taskId: String) extends IdentityTask
}
