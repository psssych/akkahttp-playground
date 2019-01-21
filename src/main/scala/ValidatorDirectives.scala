import akka.http.scaladsl.server.{Directive0, Directives}

trait ValidatorDirectives extends Directives {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  def validateWith[T](validator: Validator[T])(t: T): Directive0 = {
    validator.validate(t) match {
      case Some(e) => complete(e.statusCode, e.message)
      case None => pass
    }
  }
}
