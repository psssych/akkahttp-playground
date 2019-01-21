import akka.http.scaladsl.model.{StatusCode, StatusCodes}

sealed trait ApiError {
  def statusCode: StatusCode
  def message: String
}


object ApiError {
//  final case class Error(statusCode: StatusCode, message: String) extends WithMessage

  object GenericApiError extends ApiError {
    val statusCode: StatusCode = StatusCodes.InternalServerError
    val message: String = "Unknown Error"
  }

  object EmptyTitleFieldError extends ApiError {
    val statusCode: StatusCode = StatusCodes.BadRequest
    val message: String = "The todo title must not be empty !"
  }

  final case class TodoNotFoundError(id: String) extends ApiError {
    val statusCode: StatusCode = StatusCodes.BadRequest
    val message: String = s"The todo to update must exist ! Got: $id"
  }
}