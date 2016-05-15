package unlimited_works.play.controllers.util

import play.api.mvc._

import scala.concurrent.Future

/**
  * verify login cookie does exist in server session ╰(￣▽￣)╮
  */
case class WithLogin[A](action: Action[A]) extends Action[A] with Results {
  override def parser: BodyParser[A] = action.parser

  override def apply(request: Request[A]): Future[Result] = {
    request.cookies.get(Config.CookieSession.GOD_SESSION).map(_.value) match {
      case None => Future.successful(Unauthorized)
      case Some(_) => action(request)
    }
  }
}
