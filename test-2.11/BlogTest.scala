import unlimited_works.play.socket.dao.module.Blog
import unlimited_works.play.socket.dao.module.Blog.BlogModel
import lorance.rxscoket._

import scala.concurrent.ExecutionContext.Implicits.global
/**
  *
  */
object BlogTest extends App {
  logLevel = 10
//  val x = Blog.insertBlog(BlogModel("l", "title02", "1459093533755", "introduction02", "body02"))
//  x.map(rst => log(s"response form dao - $rst"))


  val x = Blog.findBlogs("l", 0)
  x.map(r => log(r.toString))


  Thread.currentThread().join()
}
