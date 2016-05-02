package unlimited_works.play.socket.dao.module.blog

import lorance.rxscoket.presentation.json.IdentityTask
import rx.lang.scala.Observable
import unlimited_works.play.socket.DaoCommunicate

import scala.concurrent.{Promise, Future}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  *
  */
object Overview {

  def get(penName: String, skip: Int, limit: Int) = {
    val d = DaoCommunicate.modelSocket.flatMap{s =>
      val rsp = s.sendWithResult[OverviewRsp, OverviewReq](OverviewReq(penName, limit, skip), Some(x => x.takeWhile(_.result.nonEmpty)))
      toFuture(rsp)
    }
    d
  }

  private def toFuture(observable: Observable[OverviewRsp]): Future[List[OverviewRsp]] = {
    val p = Promise[List[OverviewRsp]]
    val lst = scala.collection.mutable.ListBuffer[OverviewRsp]()
    observable.subscribe(
      s => {
        lorance.rxscoket.log("overview Rsp ready - " + lst.mkString("\n"))
        lst.synchronized(lst.+=(s))
      },
      e => p.tryFailure(e),
      () => p.trySuccess(lst.toList)
    )

    p.future
  }

  case class OverviewReq(penName: String, limit: Int, skip: Int, model: String = "blog/index/overview", taskId: String = lorance.rxscoket.presentation.getTaskId) extends IdentityTask
  case class OverviewRsp(result: Option[OverviewContent], taskId: String) extends IdentityTask
  case class OverviewContent(id: String, title: String, issue_time: String, introduction: String)
}
