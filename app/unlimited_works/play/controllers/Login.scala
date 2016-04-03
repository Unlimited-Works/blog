package unlimited_works.play.controllers

import play.api.mvc.{Action, Controller}
import unlimited_works.play.socket.dao.module.LoginModule
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

  def ajaxAuthenVerfiy = Action.async { implicit request =>
    val form = request.body.asFormUrlEncoded.get
    val account = form.get("account").map(_.head).getOrElse("")
    val password = form.get("password").map(_.head).getOrElse("")

    if(account.isEmpty || password.isEmpty)
      Future(Ok(compactRender(("result" -> 400) ~ ("msg" -> "账号和密码不能为空"))))
    else {
      val rst = LoginModule.verifyAndGetId(account, password)
      rst.map { x =>
        println(s"AccountVerifyResult - $x")
        if (x.result.nonEmpty) Ok(compactRender("result" -> 200))
        else Ok(compactRender(("result" -> 400) ~ ("msg" -> "身份验证失败")))
      }
    }
  }
}
