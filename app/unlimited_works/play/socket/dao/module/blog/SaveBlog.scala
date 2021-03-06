package unlimited_works.play.socket.dao.module.blog

import lorance.rxscoket.presentation.json.IdentityTask
import unlimited_works.play.playLogger
import unlimited_works.play.socket.DaoCommunicate

import scala.concurrent.Promise
import scala.concurrent.ExecutionContext.Implicits.global
/**
  *
  */
object SaveBlog {
  def update(introduce: String, title: String, body: String, blogId: String) = {
    DaoCommunicate.modelSocket.flatMap { j =>
      val p = Promise[Option[String]]
      j.sendWithResult[Rsp, Req](Req(introduce, title, body, blogId), Some(x => x.first)).
        subscribe( x =>
          p.trySuccess(x.result),
          e => p.tryFailure(e)
        )
      p.future
    }
  }

  case class Req(introduce: String, title: String, body: String, _id: String, model: String = "blog/post/update", taskId: String = lorance.rxscoket.presentation.getTaskId) extends IdentityTask

  //todo consider update fail please
  case class Rsp(taskId: String, result: Option[String]) extends IdentityTask

  //todo response with postId
  def create(introduce: String, title: String, body: String, time: String, pn: String) = {
    DaoCommunicate.modelSocket.flatMap { j =>
      val p = Promise[(Option[String], Option[String])]
      j.sendWithResult[RspCreate, ReqCreate](ReqCreate(introduce, title, body, time, pn), Some(x => x.first)).
        subscribe( x =>
          p.trySuccess(x.result, x.postId),
          e => p.tryFailure(e)
        )
      p.future
    }
  }

  case class ReqCreate(introduce: String, title: String, body: String, time: String, pen_name: String, model: String = "blog/post/create", taskId: String = lorance.rxscoket.presentation.getTaskId) extends IdentityTask

  //param: result Some(string) represent error info, None is success
  //param: postId Some(string) if save success
  case class RspCreate(result: Option[String], postId:Option[String], taskId: String) extends IdentityTask
}

object DeleteBlog {
  playLogger.logAim += "delete-blog"
  def delete(id: String) = {
    DaoCommunicate.modelSocket.flatMap { j =>
      val p = Promise[Option[String]]
      j.sendWithResult[DeleteRsp, DeleteReq](DeleteReq(id), Some(x => x.first)).
        subscribe( x => {
          playLogger.log(s"$x", 30, Some("delete-blog"))
          p.trySuccess(x.result)},
          e => p.tryFailure(e)
        )
      p.future
    }
  }

  case class DeleteReq(id: String, model: String = "blog/post/delete", taskId: String = lorance.rxscoket.presentation.getTaskId) extends IdentityTask

  //param: result Some(string) represent error info, None is success
  case class DeleteRsp(result: Option[String], taskId: String) extends IdentityTask
}
