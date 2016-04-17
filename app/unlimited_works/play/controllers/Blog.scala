package unlimited_works.play.controllers

import java.text.SimpleDateFormat
import java.util.Date

import play.api.mvc._
import unlimited_works.play.socket.dao.module.blog.{Post, Overview, PenName}
import unlimited_works.play.socket.dao.module.blog.Post.{Post => PostRsp}
import unlimited_works.play.views

import scala.concurrent.ExecutionContext.Implicits.global
import net.liftweb.json.JsonDSL._
import net.liftweb.json._

import scala.concurrent.Future

/**
  *
  */
object Blog extends Controller {

  //authenticate confirmation from cookie named uuid
  def index = Action { request =>
    val logined_? = request.session.get("accountId").nonEmpty
    if (logined_?)
//    request.cookies.get("uuid").map { uuid =>
    //      Ok(views.html.blog.index(""))
    //    }.getOrElse {
    //      Redirect("/signin")
    //    }
      Ok(views.html.blog.index())
    else Ok(views.html.signin())
  }

  def post(id: String) = Action { request =>
    //todo create a Action to control NO login request
    val logined_? = request.session.get("accountId").nonEmpty
    if (logined_?) Ok(views.html.blog.post())
    else Ok(views.html.signin())
  }

  def postContentAjax(id: String) = Action.async { request =>
    //todo create a Action to control NO login request
    val logined_? = request.session.get("accountId").nonEmpty
    if (logined_?)
      Post.get(id).map { post =>
        import net.liftweb.json.Extraction._
        implicit val f = net.liftweb.json.DefaultFormats
        //pen_name: String, title: String, issue_time: String, introduction:
        val formatTime = {
          val date = new Date(post.issue_time.toLong)
          val df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm")
          df2.format(date)
        }
        val adjust = PostRsp(post.pen_name, post.title, formatTime, post.introduction, post.body)
        println("response - " + prettyRender(decompose(adjust)))
        Ok(compactRender(("result" -> 200) ~ ("post" -> decompose(adjust))))
      }
    else Future(Ok(views.html.signin()))
  }

  //pen_name
  def ajaxPenName = Action.async{ implicit request =>
    val id = request.session("accountId")

    PenName.get(id).map{ r =>
      Ok(compactRender(("result" -> 200) ~ ("penName" -> r.pen_name)))
    }
  }

  //title, issue_item, introduction
  def ajaxOverview(skip: Int, limit: Int) = Action.async{ implicit request =>
    val id = request.session("accountId")
//
    PenName.get(id).flatMap {pName =>
      Overview.get(pName.pen_name, skip, limit).map { s =>
        val blogs: JArray = s.map { item =>
          ("id" -> item.result.get.id) ~
          ("title" -> item.result.get.title) ~
          ("issueTime" -> {
            val date = new Date(item.result.get.issue_time.toLong)
            val df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm")
            df2.format(date)
          }) ~
          ("introduction" -> item.result.get.introduction)
        }

        Ok(compactRender(("result" -> 200) ~ ("blogs" -> blogs)))
      }
    }
  }

  def postRenderTest = Action { implicit request =>
    Ok(views.html.blog.post_test.render())
  }
}

/**
  * structure
 */
case class Content( title: String, //标题
                    appendix: String, //附录
                    `abstract`: String, //摘要
                    keywords: List[String],//关键词
                    introduction: String,//引言
                    body: String,//正文
                    conclusion: String,//结束语
                    acknowledgement: List[String],//致谢
                    references: List[String])//参考书目