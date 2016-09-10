package unlimited_works.play.controllers.util

import play.api.mvc._
import play.api.http.HeaderNames._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Action decorator that provide CORS support
  *
  * @author Giovanni Costagliola, Nick McCready, Lorance Chen
  */
case class WithCors[A](action: Action[A]) extends Action[A] with Results {
  def apply(request: Request[A]): Future[Result] = {
    implicit val executionContext: ExecutionContext = play.api.libs.concurrent.Execution.defaultContext
    request.headers.get(ORIGIN).flatMap{ origin =>
      Config.Cors.find(origin).map { origVal =>
        if (request.method == "OPTIONS") {
          // preflight
          val corsAction = Action.async(action.parser) {
            request =>
              Future.successful(Ok("").withHeaders(
                ACCESS_CONTROL_ALLOW_ORIGIN -> origin,
                ACCESS_CONTROL_ALLOW_METHODS -> (List("GET", "POST") + "OPTIONS").mkString(", "),
                ACCESS_CONTROL_MAX_AGE -> "3600", //seconds
                ACCESS_CONTROL_ALLOW_HEADERS -> s"$ORIGIN, X-Requested-With, $CONTENT_TYPE, $COOKIE, $ACCEPT, $AUTHORIZATION, X-Auth-Token",
                ACCESS_CONTROL_ALLOW_CREDENTIALS -> "true"))
          }
          corsAction(request)
        } else {
          action(request).map { res =>
            res.withHeaders(
              ACCESS_CONTROL_ALLOW_ORIGIN -> origin,
              ACCESS_CONTROL_ALLOW_CREDENTIALS -> "true")
          }
        }
      }
    }.getOrElse(Future.successful(Forbidden("ACCESS_CONTROL_ALLOW_ORIGIN forbid")))
  }

  override def parser: BodyParser[A] = action.parser
}

//case class WithMainDomainSession[A](action: Action[A]) extends Action[A] with Results {
//  def apply(request: Request[A]): Future[Result] = action(request)
//  def apply(append: Map[String, String], predicate: (Request[A] => Boolean))(request: Request[A]): Future[Result] = {
//    if(predicate(request)) {
//      implicit val executionContext: ExecutionContext = play.api.libs.concurrent.Execution.defaultContext
//
//      action(request) map{ result =>
//        result.withSession {
//
//          append.foldLeft[Session](request.session)((session, a) => session + a)
//        }
//      }
//    } else {
//      action(request)
//    }
//  }
//
//  override def parser: BodyParser[A] = action.parser
//}
