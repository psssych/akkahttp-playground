import ApiError.EmptyTitleFieldError

trait Validator[T] {
  def validate(t: T): Option[ApiError]
}

object CreateTodoValidator extends Validator[CreateTodo] {

  override def validate(createTodo: CreateTodo): Option[ApiError] =
    if (createTodo.title.isEmpty) Some(EmptyTitleFieldError) else None

}

object UpdateTodoValidator extends Validator[UpdateTodo] {

  override def validate(t: UpdateTodo): Option[ApiError] =
    if (t.title.exists(_.isEmpty)) Some(EmptyTitleFieldError) else None

}
