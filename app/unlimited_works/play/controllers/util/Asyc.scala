//package unlimited_works.play.controllers.util
//
//import scala.concurrent.ExecutionContext.Implicits.global
//import play.api.mvc._
//import play.api.mvc.Results._
//import play.api.http.HeaderNames._
//
//import scala.concurrent.Future
//
//object Cors extends Filter {
//  def apply(next: (RequestHeader) => Future[SimpleResult])(request: RequestHeader): Future[SimpleResult] = {
//    val origin = request.headers.get(ORIGIN).getOrElse("*")
//    if (request.method == "OPTIONS") {
//      val response = Ok.withHeaders(
//        ACCESS_CONTROL_ALLOW_ORIGIN -> origin,
//        ACCESS_CONTROL_ALLOW_METHODS -> "POST, GET, OPTIONS, PUT, DELETE",
//        ACCESS_CONTROL_MAX_AGE -> "3600",
//        ACCESS_CONTROL_ALLOW_HEADERS -> s"$ORIGIN, X-Requested-With, $CONTENT_TYPE, $ACCEPT, $AUTHORIZATION, X-Auth-Token",
//        ACCESS_CONTROL_ALLOW_CREDENTIALS -> "true"
//      )
//      Future.successful(response)
//    } else {
//      next(request).map {
//        res => res.withHeaders(
//          ACCESS_CONTROL_ALLOW_ORIGIN -> origin,
//          ACCESS_CONTROL_ALLOW_CREDENTIALS -> "true"
//        )
//      }
//    }
//  }
//}
//
//object Global extends WithFilters(Cors) {...}