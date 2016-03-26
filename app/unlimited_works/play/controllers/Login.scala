package unlimited_works.play.controllers

import play.api.mvc.{Action, Controller}
import unlimited_works.play.socket.dao.module.LoginModule
import unlimited_works.play.views
import scala.concurrent.ExecutionContext.Implicits.global
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

  def authenVerfiy = Action.async { implicit request =>
    val form = request.body.asFormUrlEncoded.get

    /**
      * TODO data form http should be confirmed by Bean that match the logic
      * some annotation such as email and password format, length of the password, etc.s
      */
    val account = form.get("account").map(_.head).getOrElse("")
    val password = form.get("password").map(_.head).getOrElse("")
    val rst = LoginModule.verifyAndGetId("administrator", "12345_md5")//(account, password)
//    val rst = LoginModule.verify(account, password)
    rst.map{ x =>
      println(s"AccountVerifyResult - $x")
      Ok("ok")
    }
  }
}
