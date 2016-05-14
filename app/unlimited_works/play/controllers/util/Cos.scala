package unlimited_works.play.controllers.util

import play.api.mvc._
import play.api.http.HeaderNames._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Action decorator that provide CORS support
  *
  * @author Giovanni Costagliola, Nick McCready, Lorance Chen
  */
case class WithCors[A](action: Action[A]) extends Action[A] with Results{
  def apply(request: Request[A]): Future[Result] = {
    implicit val executionContext: ExecutionContext = play.api.libs.concurrent.Execution.defaultContext
    val origin = request.headers.get(ORIGIN).getOrElse("*")//todo
    if (request.method == "OPTIONS") { // preflight
      val corsAction = Action.async(action.parser) {
        request =>
          Future.successful( Ok("").withHeaders(
            ACCESS_CONTROL_ALLOW_ORIGIN -> origin,
            ACCESS_CONTROL_ALLOW_METHODS -> (List("GET","POST") + "OPTIONS").mkString(", "),
            ACCESS_CONTROL_MAX_AGE -> "1728000",//20 days
            ACCESS_CONTROL_ALLOW_HEADERS ->  s"$ORIGIN, X-Requested-With, $CONTENT_TYPE, $ACCEPT, $AUTHORIZATION, X-Auth-Token",
            ACCESS_CONTROL_ALLOW_CREDENTIALS -> "true"))
      }
      corsAction(request)
    } else { // actual request
      action(request).map(res => res.withHeaders(
        ACCESS_CONTROL_ALLOW_ORIGIN -> origin,
        ACCESS_CONTROL_ALLOW_CREDENTIALS -> "true"
      ))
    }
  }

  override def parser: BodyParser[A] = action.parser
}
