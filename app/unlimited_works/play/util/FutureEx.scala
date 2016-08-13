package unlimited_works.play.util

import scala.concurrent.{ExecutionContext, Future}

/**
  *
  */
object FutureEx {
  implicit def toFutureOpt[T](optFur: Option[Future[T]])(implicit ec: ExecutionContext): Future[Option[T]] = {
    optFur match {
      case None => Future.successful(None)
      case Some(fur) => fur.map(t =>
        Some(t)
      )
    }
  }
}
