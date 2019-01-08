import akka.http.scaladsl.server.{Directive1, Directives}

import scala.concurrent.Future
import scala.util.{Failure, Success}


trait TodoDirectives extends Directives {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  def handle[T](f: Future[T])(e: Throwable => ApiError): Directive1[T] = onComplete(f).flatMap{
    case Success(t) => provide(t)
    case Failure(exception) =>
      val apiError = e(exception)
      complete(apiError.statusCode, apiError.message)
  }

  def handleWithGeneric[T](f: Future[T]): Directive1[T] = handle(f)(_ => ApiError.generic)

}
