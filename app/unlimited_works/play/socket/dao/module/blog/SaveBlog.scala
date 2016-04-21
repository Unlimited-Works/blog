package unlimited_works.play.socket.dao.module.blog

import lorance.rxscoket.presentation.json.IdentityTask
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

  case class Req(introduce: String, title: String, body: String, _id: String, taskId: String = "blog/post/update") extends IdentityTask
  case class Rsp(taskId: String, result: Option[String]) extends IdentityTask

  def create(introduce: String, title: String, body: String, time: String, pn: String) = {
    DaoCommunicate.modelSocket.flatMap { j =>
      val p = Promise[Option[String]]
      j.sendWithResult[RspCreate, ReqCreate](ReqCreate(introduce, title, body, time, pn), Some(x => x.first)).
        subscribe( x =>
          p.trySuccess(x.result),
          e => p.tryFailure(e)
        )
      p.future
    }
  }

  case class ReqCreate(introduce: String, title: String, body: String, time: String, pen_name: String, taskId: String = "blog/post/create") extends IdentityTask
  case class RspCreate(taskId: String, result: Option[String]) extends IdentityTask
}
