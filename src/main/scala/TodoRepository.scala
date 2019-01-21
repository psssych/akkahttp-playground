import java.util.UUID

import ApiError.TodoNotFoundError
import TodoRepository.TodoNotFound

import scala.concurrent.Future

trait TodoRepository {

  def all(): Future[Seq[Todo]]
  def done(): Future[Seq[Todo]]
  def pending(): Future[Seq[Todo]]
  def create(todo: CreateTodo): Future[Todo]
  def update(id: String, updateTodo: UpdateTodo): Future[Todo]

}

object TodoRepository {
  final case class TodoNotFound(id: String) extends Exception(s"Todo with id $id not found.")
}

class InMemoryTodoRepository(initialTodos: Seq[Todo] = Seq.empty) extends TodoRepository {
  private var todos: Seq[Todo] = initialTodos

  override def all(): Future[Seq[Todo]] = Future.successful(todos)

  override def done(): Future[Seq[Todo]] = Future.successful(todos.filter(_.done))

  override def pending(): Future[Seq[Todo]] = Future.successful(todos.filter(!_.done))

  override def create(todo: CreateTodo): Future[Todo] = {
    val id = UUID.randomUUID().toString
    val todoToAdd = Todo(id, todo.title, todo.description, done = false)
    todos = todos :+ todoToAdd
    Future.successful(todoToAdd)
  }

  override def update(id: String, updateTodo: UpdateTodo): Future[Todo] = {
    todos.find(_.id == id) match {
      case Some(foundTodo) =>
        val updatedTodo = updateHelper(foundTodo, updateTodo)
        todos = todos.map(t => if (t.id == id) updatedTodo else t)
        Future.successful(updatedTodo)
      case None =>
        Future.failed(TodoNotFound(id))
    }
  }

  private def updateHelper(old: Todo, updated: UpdateTodo): Todo = {
    Todo(
      id = old.id,
      title = updated.title.getOrElse(old.title),
      description = updated.description.getOrElse(old.description),
      done = updated.done.getOrElse(old.done)
    )
  }
}