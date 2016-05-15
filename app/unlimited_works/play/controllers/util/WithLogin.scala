package unlimited_works.play.controllers.util

import play.api.mvc._

import scala.concurrent.Future

/**
  * verify login cookie does exist in server session ╰(￣▽￣)╮
  * todo return accountId. It should be define a new Action type
  */
case class WithLogin[A](action: Action[A]) extends Action[A] with Results {
  def accountId(request: Request[A]) = {
    request.cookies.get(Config.CookieSession.GOD_SESSION).map(_.value)
  }

  override def parser: BodyParser[A] = action.parser

  override def apply(request: Request[A]): Future[Result] = {
    accountId(request) match {
      case None => Future.successful(Unauthorized)
      case Some(_) => action(request)
    }
  }
//
//  def apply(request: (String, Request[A])): Future[Result] = {
//    request.cookies.get(Config.CookieSession.GOD_SESSION).map(_.value) match {
//      case None => Future.successful(Unauthorized)
//      case Some(_) => action(request)
//    }
//  }
}
