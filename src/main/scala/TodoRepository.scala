import scala.concurrent.Future

trait TodoRepository {

  def all(): Future[Seq[Todo]]
  def done(): Future[Seq[Todo]]
  def pending(): Future[Seq[Todo]]

}

class InMemoryTodoRepository(initialTodos: Seq[Todo] = Seq.empty) extends TodoRepository {
  private val todos: Seq[Todo] = initialTodos

  override def all(): Future[Seq[Todo]] = Future.successful(todos)

  override def done(): Future[Seq[Todo]] = Future.successful(todos.filter(_.done))

  override def pending(): Future[Seq[Todo]] = Future.successful(todos.filter(!_.done))
}