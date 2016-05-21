package unlimited_works.play.socket.dao.module.blog

import java.util.concurrent.TimeoutException

import lorance.rxscoket.presentation.getTaskId
import lorance.rxscoket.presentation.json.IdentityTask
import net.liftweb.json.DefaultFormats
import rx.lang.scala.Observable
import unlimited_works.play.socket.DaoCommunicate
import unlimited_works.play.socket.dao.Method

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise

/**
  * find with skip X and limit Y
  * {
  *   "taskId": "thread-1:timestamp",//if not have represent needn't call back
  *   "dataBase": "blog",
  *   "collection": "accounts",
  *   ...
  *}
  */
object Blog {
  val dBName = "blog"
  val collName = "blogs"
  implicit val formats = DefaultFormats

  /**
    *"method": "aggregate",
    *"params": [
    *  {$match:
    *    {"pen_name": "administrator"},
    *  },
    *  {"$limit": Y},
    *  {"$skip": X},
    *  {"$project": }
    *]
    *
    * result -
    * {
      * title: String, //标题
      * issue_time: String//发布时间
    * //      * appendix: String, //附录
    * //      * `abstract`: String, //摘要
    * //      * keywords: List[String],//关键词
      * introduction: String,//引言
      * body: String,//正文
    * //      * conclusion: String,//结束语
    * //      * acknowledgement: List[String],//致谢
    * //      * references: List[String])//参考书目
    * }
    */
  def findBlogs(penName: String, skip: Int) = {
    val proto = FindBlogsReq(getTaskId, dBName, collName,
      Method.AGGREGATE,
      List(Match(PenName(penName)), Limit(10), Skip(skip)))
    DaoCommunicate.getDefaultInstance.flatMap{o =>
      val r = o.sendWithResult[FindBlogRsp, FindBlogsReq](proto, Some(x => x.takeWhile(_.rusult.nonEmpty)))
      findBlogRstToFuture(r)
    }
  }


  //todo bug: only parse one result, the later request can't find task response!!!
  def insertBlog(blog: BlogModel) = {
    val proto = InsertBlog(getTaskId, dBName, collName,
      Method.INSERT,
      InsertParam(blog))
    DaoCommunicate.getDefaultInstance.flatMap{o =>
      val r = o.sendWithResult[InsertRsp, InsertBlog](proto, Some(x => x.takeUntil(_.result.isEmpty)))
      insertBlogFuture(r)
    }
  }

  private def insertBlogFuture(observable: Observable[InsertRsp]) = {
    val p = Promise[InsertRsp]
    observable.subscribe(
      s => p.trySuccess(s),
      e => p.tryFailure(e),
      () => p.tryFailure(new TimeoutException())
    )

    p.future
  }


  private def findBlogRstToFuture(observable: Observable[FindBlogRsp]) = {
    val p = Promise[List[FindBlogRsp]]

    val lstRsp = mutable.ListBuffer[FindBlogRsp]()

    observable.subscribe(
      s => lstRsp += s,
      e => p.tryFailure(e),
      () => p.trySuccess(lstRsp.toList)
    )

    p.future
  }

  case class FindBlogsReq(taskId: String, dataBase: String, collection: String,
                          method: String,
                          params: List[Param]) extends IdentityTask
  sealed class Param
  case class Match(`$match`: PenName) extends Param
  case class PenName(pen_name: String)
  case class Limit(`$limit`: Int) extends Param
  case class Skip(`$skip`: Int) extends Param

  case class FindBlogRsp(taskId: String, rusult: Option[BlogModel]) extends IdentityTask

  case class BlogModel(pen_name: String, title: String, issue_time: String,
                       introduction: String, body: String)

  //insert
  case class InsertBlog(taskId: String, dataBase: String, collection: String,
                        method: String,
                        params: InsertParam) extends IdentityTask
  case class InsertParam(documents: BlogModel)

  case class InsertRsp(taskId: String, result: Option[String]) extends IdentityTask
}