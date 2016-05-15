package unlimited_works.play.controllers.util

import play.api.http.HeaderNames._
import play.api.mvc.{BodyParser, Result, Request, Results, Action, Session, EssentialFilter, EssentialAction, Cookies}

import scala.concurrent.{ExecutionContext, Future}

/**
  *
  */
//case class SessionFilter[A]() extends EssentialFilter {
//  def apply2(next: Action[A]) = Action { req: Request[A] =>
//    implicit val executionContext: ExecutionContext = play.api.libs.concurrent.Execution.defaultContext
//
//    next(req).map { result =>
//
//      Cookies.fromSetCookieHeader(result.header.headers.get("Set-Cookie")).get("PLAY_SESSION") match {
//        case Some(sessionCookie) =>
//          result.withCookies(sessionCookie.copy(domain = Some(Config.MAIN_DOMAIN)))
//        case None => result
//      }
//    }
//  }
//}

//case class WithSession[A](action: Action[A]) extends Action[A] with Results{
//  def apply(request: Request[A]): Future[Result] = {
//    Cookies.fromSetCookieHeader(result.header.headers.get("Set-Cookie")).get("PLAY_SESSION") match {
//      case Some(sessionCookie) =>
//        result.withCookies(sessionCookie.copy(domain = Some(Config.MAIN_DOMAIN)))
//      case None => result
//    }
//  }
//
//  override def parser: BodyParser[A] =  {
//    action
//    action.parser
//  }
//}