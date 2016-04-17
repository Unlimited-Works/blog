package unlimited_works.play.controllers.util

import play.api.mvc.{Result, ActionBuilder, RequestHeader, Request}
import unlimited_works.play.socket.DaoCommunicate

import scala.concurrent.Future

//
//class Authentication(fail: => (Request[A]) => Future[Result]) extends ActionBuilder[Request] {
//  //    class AfterLogin(f: Option[User] => Option[User]) extends AuthenticatedBuilder[User](req => f(getUserFromRequest(req)),
//  //        implicit req => {
//  //          preLoginPage
//  //        })
//  def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
//    request.session.get("accountId") match {
//      case None =>
//      case Some(id) => block(request)
//    }
//
//  }
//}