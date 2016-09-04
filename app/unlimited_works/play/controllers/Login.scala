package unlimited_works.play.controllers

import play.api.libs.json.Json
import play.api.libs.json._
import play.api.mvc.{Cookie, Action, Controller}
import unlimited_works.play.controllers.util.{Config, WithCors}
import unlimited_works.play.socket.dao.module.LoginModule
import unlimited_works.play.socket.dao.module.account.Account
import unlimited_works.play.socket.dao.module.account.Account.AccountExistRsp
import unlimited_works.play.util.{FutureEx, RedisService, Helpers, SessionMultiDomain}
import unlimited_works.play.views
import scala.concurrent.ExecutionContext.Implicits.global
import net.liftweb.json._
import net.liftweb.json.JsonDSL._

import scala.concurrent.Future


/**
  * signin page
  */
object Signin extends Controller {

  /**
    * enter signin page
    *
    * @return html resource
    */
  def index = Action {
    Ok(views.html.signin())
  }
//
  def ajaxAuthenVerfiy2 = WithCors {
//    WithMainDomainSession(Map("a" -> "b")) {
      Action.async { implicit request =>
//        val form = request.body.asFormUrlEncoded.get
//        val key = form.get("key").map(_.head).getOrElse("")

        Future.successful(Ok(compactRender(("result" -> 400) ~ ("msg" -> "账号和密码不能为空"))).
          withCookies(Cookie(name = "key", value = "value2", maxAge = Some(3600), domain = Some(".scalachan.com"), httpOnly = false)))
      }
  }

  def ajaxAuthenVerfiy =
    WithCors {
        Action.async {
          implicit request =>
            val form = request.body.asFormUrlEncoded.get
            val account = form.get("account").map(_.head).getOrElse("")
            val password = form.get("password").map(_.head).getOrElse("")

            if (account.isEmpty || password.isEmpty)
              Future(Ok(compactRender(("result" -> 400) ~ ("msg" -> "账号和密码不能为空"))))
            else {
              //TODO login with specify field 要求明确的指出是哪个字段
              val rst = LoginModule.verifyAndGetId(account, password)
              rst.flatMap { x =>
                println(s"AccountVerifyResult - $x")
                if (x.result.nonEmpty) {
                  val key = Helpers.stringTo32ByteMD5(account + password).toUpperCase
                  val accountId = x.result.get._id.`$oid`
                  SessionMultiDomain.put(key, "accountId" -> accountId).map { x =>
                    println("result - " + x + ":" + key + " " + account)
                    Ok(compactRender("result" -> 200)).withCookies(
                      Cookie(name = Config.CookieSession.GOD_SESSION, value = key, maxAge = Some(3600 * 24 * 365), domain = Some(".scalachan.com"), httpOnly = false),
                      Cookie(name = Config.CookieSession.VISITOR, value = "", maxAge = Some(-1), domain = Some(".scalachan.com"), httpOnly = false)
                    )
                  }
                }
                else Future.successful(Ok(compactRender(("result" -> 400) ~ ("msg" -> "身份验证失败"))))
              }
            }
        }
    }

  def isSelf(accountId: Option[String]) = WithCors {
    Action.async { req =>
      //self query accountId == session accountId or (accountId == None && sessionId logined)
      //self accountId
      SessionMultiDomain.getAccountId(req).map { sActId =>
        (sActId, accountId) match {
          case (Some(sId), Some(queryId)) if sId == queryId =>
            Ok(Json.obj("result" -> 200))
          case (Some(sId), None | Some("")) =>
            Ok(Json.obj("result" -> 200))
          case _ => Ok(Json.obj("result" -> 400))
        }
      }
    }
  }

//  def isSelf2 = WithCors {
//    Action {
//      Ok(Json.obj("result" -> 200))
//    }
//  }
  // /signin/register.json
  def registerAjax = WithCors {
    Action.async { req =>
      val form = req.body.asFormUrlEncoded.get
      val invitationCode = form.get("invitationCode").map(_.head).getOrElse("")
      val email = form.get("email").map(_.head).getOrElse("")
      val username = form.get("username").map(_.head).getOrElse("")
      val penName = form.get("penName").map(_.head).getOrElse("")
      val password = form.get("password").map(_.head).getOrElse("")
      val passwordAgain = form.get("passwordAgain").map(_.head).getOrElse("")

      case class FieldValidator(key: String, value: String, validator: String => Option[String]) {
        def validate = validator(value.trim).map(key -> _)
      }

      //soul from Tch.He
      val checkInput = List(
        FieldValidator("invitationCode", invitationCode, value => if (value.isEmpty) Some("不能为空") else None),
        FieldValidator("email", email, value => if (value.isEmpty) Some("邮箱不能为空") else None),
        FieldValidator("email", email, value => if (!unlimited_works.play.util.Format.checkEmail(email)) Some("邮箱格式不正确") else None),
        FieldValidator("username", username, value => if (value.isEmpty) Some("用户名不能为空") else None),
        FieldValidator("penName", penName, value => if (value.isEmpty) Some("笔名不能为空") else None),
        FieldValidator("password", password, value => {
          if (value.isEmpty) Some("密码不能为空")
          else if (value != passwordAgain) {
            Some("两次密码输入不同")
          } else None
        })
      ).foldLeft(Json.obj()) { (errors, fieldValidator) =>
        fieldValidator.validate.map { e => errors.+((e._1, JsString(e._2))) }.getOrElse(errors)
      }

      if (checkInput.values.isEmpty) {
        Account.invitationCodeUseable_?(invitationCode).flatMap{inDbCanUse => //verify in db
          if(inDbCanUse) Future.successful(true) else {
            FutureEx.toFutureOpt(req.cookies.find(_.name == Config.CookieSession.VISITOR).
              map(_.value).
              map(x => RegisterMailCache.getMailNumber(x, email))).
              map(_.flatten.contains(invitationCode))
          }
        }.flatMap { canUse =>
          if (canUse) {
            Account.accountExist(email, username, penName).map {
              case AccountExistRsp(None, _, _) =>
                Account.useInvitationCode(invitationCode) //todo refine - if verify by email NOT do this
                Account.register(email, username, penName, password)
                Ok(Json.obj("result" -> 200))
              case AccountExistRsp(Some(account), _, _) =>
                if (account.penName == penName) {
                  Ok(Json.obj("result" -> 400, "error" -> "pen_name已存在"))
                } else if (account.username == username) {
                  Ok(Json.obj("result" -> 400, "error" -> "usernmae已存在"))
                } else if (account.email == email) {
                  Ok(Json.obj("result" -> 400, "error" -> "email已存在"))
                } else {
                  Ok(Json.obj("result" -> 400, "error" -> "其他错误"))
                }
            }
          } else {
            Future.successful(Ok(Json.obj("result" -> 400, "error" -> "邀请码不可用或者与邮箱不匹配")))
          }
        }
      } else {
        Future.successful(Ok(checkInput.+("result" -> JsNumber(400))))
      }
    }
  }
}

object RegisterMailCache {
  val REGISTER_MAIL = "register_mail"

  val redisClient = RedisService.client
  val cacheSeconds = 60 * 15
  def getKeyBySession(visitorSession: String) = visitorSession + "__" + REGISTER_MAIL

  def clearMailCode(visitorSession: String): Future[Boolean] ={
    val code = ((Math.random() * 9 + 1) * 100000).toInt.toString
    val key = getKeyBySession(visitorSession)
    redisClient.set(key, code).flatMap { _ =>
      redisClient.expire(key, cacheSeconds)
    }

  }

  def getMailNumber(visitorSession: String, mail: String) = {
    redisClient.get[String](getKeyBySession(visitorSession)).map( x => x.flatMap(y => if(y.split(";").last == mail) y.split(";").headOption else None))
  }
}