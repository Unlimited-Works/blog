package unlimited_works.play.controllers

import java.text.SimpleDateFormat
import java.util.Date

import play.api.libs.json.{JsObject, Json}
import play.api.mvc._
import unlimited_works.play.controllers.util.Config.Cors
import unlimited_works.play.controllers.util.WithCors
import unlimited_works.play.socket.dao.module.blog.{DeleteBlog, SaveBlog, Post, Overview, PenName}
import unlimited_works.play.socket.dao.module.blog.Post.{Post => PostRsp}
import unlimited_works.play.util.SessionMultiDomain
import unlimited_works.play.views

import scala.concurrent.ExecutionContext.Implicits.global
import net.liftweb.json.JsonDSL._
import net.liftweb.json._

import scala.concurrent.Future
import scala.util.{Success, Try}

/**
  *
  */
object Blog extends Controller {

  //authenticate confirmation from cookie named uuid
  def index = Action { request =>
    val logined_? = request.session.get("accountId").nonEmpty
    if (logined_?) Ok(views.html.blog.index())
    else Ok(views.html.signin())
  }

  //http://play.helloworld.com:9000/blog/post/57231eb3921b552f08bf3d09/?share_sha=test_sha
  /**
    * entrance of the post
    * 1. author entrance
    * 2. visitor - has matched sha
    * 3. other - sha not match or post not sha
    */
  def post(id: String) = Action.async { request =>
    val shareSHA = request.getQueryString("share_sha").getOrElse("")
    request.session.get("accountId") match {
      case None =>
        Post.get(id).map{ post =>
          if (post.share_sha.exists(_.equals(shareSHA))) {
            Ok(views.html.blog.post())
          } else {
            Ok(views.html.UnAuthentication401("the post is not yours and not in sharing"))
          }
        }
      case Some(accountId) =>
        //todo the zip logic is frequence used
        PenName.get(accountId) zip Post.get(id) map {
          case (pn, post) if pn.pen_name == post.pen_name => //author
            Ok(views.html.blog.post())
          case (_, post) if post.share_sha.exists(_.equals(shareSHA)) => //visitor
            Ok(views.html.blog.post())
          case _ => //other
            Ok(views.html.UnAuthentication401("the post is not yours and not in sharing"))
        }
    }
  }

  /**
    * read blog content Authentication
    * 1. author: all content
    * 2. visitor 1) post has SHA 2) url SHA is match
    * 3. other is 404
    *
    * @param id postId
    * @return
    */
  def postContentAjax(id: String, share_sha: String) = Action.async { request =>
    def onSuccess(post: PostRsp, visitor: Boolean) = {
      val formatTime = {
        val date = new Date(post.issue_time.toLong)
        val df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm")
        df2.format(date)
      }
      val rsp = Json.obj(
        "result" -> 200,
        "pen_name" -> post.pen_name,
        "title" -> post.title,
        "issue_time" -> formatTime,
        "introduction" -> post.introduction,
        "body" -> post.body,
        "is_visitor" -> visitor
      )
      Ok(rsp)
    }

    request.session.get("accountId") match {
      case None => //visitor or other
        Post.get(id) map { post =>
          if(post.share_sha.exists(_.equals(share_sha))) { //visitor
            onSuccess(post, true)
          } else { //other
            Ok(Json.obj("result" -> 401))
          }
        }
      case Some(accountId) =>
        PenName.get(accountId) zip Post.get(id) map {
          case (pn, post) if pn.pen_name == post.pen_name => //author
            onSuccess(post, false)
          case (_, post) if post.share_sha.exists(_.equals(share_sha)) => //visitor
            onSuccess(post, true)
          case _ => //other
            Ok(Json.obj("result" -> 401))
        }
    }
  }

  //pen_name
  def ajaxPenName = WithCors {
    Action.async { implicit request =>
      val accountId = request.cookies.get("GOD_SESSION").flatMap { cok =>
        SessionMultiDomain.get(cok.value, "accountId")
      }.getOrElse("")
      PenName.get(accountId).map { r =>
        Ok(compactRender(("result" -> 200) ~ ("penName" -> r.pen_name)))
      }
//      Future.successful( Ok(compactRender(("result" -> 200) ~ ("penName" -> "123"))))

    }
  }

  //title, issue_item, introduction
  def ajaxOverview(skip: Int, limit: Int) = WithCors {
    Action.async{ implicit request =>
      val accountId = request.cookies.get("GOD_SESSION").flatMap { cok =>
        SessionMultiDomain.get(cok.value, "accountId")
      }.getOrElse("")

      PenName.get(accountId).flatMap {pName =>
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
  }

  /**
    * author - session accountId's pen_name == post.pen_name
    * visitor - session accountId's pen_name != post.pen_name
    * no-login / other - session not contains accountId
    */
  def postBlogAjax = Action.async { implicit request =>
    request.session.get("accountId").map { accountId =>
      val form = request.body.asFormUrlEncoded.get
      val blog = form.get("blog").map(_.head).getOrElse("")
      val postId = form.get("postId").map(_.head).getOrElse("")

      PenName.get(accountId) zip Post.get(postId) flatMap {
        case (pn, p) if pn.pen_name == p.pen_name => //author
          if(blog.length > 5000) Future.successful(Ok(compactRender(("result" -> 400) ~ ("error" -> "文章不能超过5000字"))))
          else {
            val sp = blog.split('\n')
            val first = Try(sp(0)).getOrElse("todo")
            val second = Try(sp(1)).getOrElse("todo")
            SaveBlog.update(second, first, blog, postId).map {
              case None =>
                Ok(compactRender("result" -> 200))
              case Some(msg) =>
                Ok(compactRender(("result" -> 400) ~ ("error" -> msg)))
            }
          }
        case _ => //
          Future.successful(Ok(compactRender("result" -> 401)))
      }
    }.getOrElse(Future.successful(Ok(compactRender("result" -> 401))))
  }

  def createBlog = Action { request =>
    val logined_? = request.session.get("accountId").nonEmpty
    if (logined_?) Ok(views.html.blog.create())
    else Ok(views.html.signin())
  }

  def createBlogAjax = Action.async { implicit request =>
    val id = request.session.get("accountId").getOrElse("")
    val form = request.body.asFormUrlEncoded.get
    val blog = form.get("blog").map(_.head).getOrElse("")

    val sp = blog.split('\n')
    val first = Try(sp(0)).getOrElse("todo")
    val second = Try(sp(1)).getOrElse("todo")
    val time = System.currentTimeMillis.toString
    //    PenName.get(id).flatMap{ i =>
    PenName.get(id).flatMap { pn =>
      if(blog.length > 5000) Future.successful(Ok(compactRender(("result" -> 400) ~ ("error" -> "文章不能超过5000字"))))
      else {
        SaveBlog.create(second, first, blog, time, pn.pen_name).map {
          case None =>
            Ok(compactRender("result" -> 200))
          case Some(msg) =>
            Ok(compactRender(("result" -> 400) ~ ("error" -> msg)))
        }
      }
    }
  }

  //post
  def deleteBlogAjax = Action.async{ req =>
    val id = req.session.get("accountId").getOrElse("")
    val form = req.body.asFormUrlEncoded.get
    val postId = form.get("postId").map(_.head).getOrElse("")

    //is the post belong to the accountId
    PenName.get(id) zip Post.get(postId) flatMap{ //pnAndPost =>
      //compare the post penName does equal to penName
      case (pn, p) if pn.pen_name == p.pen_name =>
        DeleteBlog.delete(postId) map {
          case None => Ok(Json.obj("result" -> 200))
          case Some(error) => Ok(Json.obj("result" -> 400, "error" -> "delete fail"))
        }
      case _ =>
        Future.successful(Ok(Json.obj("result" -> 400, "error" -> "post is not yours")))
    }
  }

  def postAccess(postId: String) = Action.async{req =>
    Post.get(postId) map {
      case PostRsp(_, _, _, _, _, Some(sha)) =>
        Ok(Json.obj("result" -> 200,
          "isPublic" -> true,
          "share_sha" -> sha
        ))
      case _ =>
        Ok(Json.obj("result" -> 200,
          "isPublic" -> false
        ))
    }
  }

  def postModifyAccess = Action.async { request =>
    val id = request.session.get("accountId").getOrElse("")
    val form = request.body.asFormUrlEncoded.get

    val postId = form.get("postId").map(_.head).getOrElse("")
    val currentIsPublic = form.get("currentIsPublic").map(_.head).getOrElse("")
    def strToBool(str: String) = str match {
      case "true" => Some(true)
      case "false" => Some(false)
      case _ => None
    }
    val rst = strToBool(currentIsPublic) match {
      case Some(curIsPublic) => //
        //verify is author
        PenName.get(id) zip Post.get(postId) flatMap{
          case (pn, p) if pn.pen_name == p.pen_name =>
            Post.modifyAccess(postId, !curIsPublic) map {
              case Some(shareSha) =>
                if(!curIsPublic) {//返回了shareSHA -> toPublic -> curIsPublic is false
                  //ok
                  Ok(Json.obj(
                    "result" -> 200,
                    "hasSHA" -> true,
                    "share_sha" -> shareSha))
                } else {
                  Ok(Json.obj("result" -> 521,
                    "hasSHA" -> true,
                    "share_sha" -> shareSha,
                    "error" -> "这种情况不需要返回一个shareSHA"))
                }
              case None =>
                if(curIsPublic) {//没有返回shareSHA -> toPublic is false -> curIsPublic is true
                  Ok(Json.obj("result" -> 200, "hasSHA" -> false))
                } else {
                  Ok(Json.obj("result" -> 521, "hasSHA" -> false, "error" -> "这种情况需要返回一个shareSHA"))
                }
            }
          case _ =>
            Future.successful(Ok(Json.obj(
              "result" -> 401,
              "error" -> "can't modify the post access because it is not yours"
            )))
        }
      case _ => //not contains the param
        Future.successful(Ok(Json.obj(
          "result" -> 400,
          "error" -> "currentIsPublic parameter can't transfer to Boolean"
        )))
    }

    rst
  }
}
