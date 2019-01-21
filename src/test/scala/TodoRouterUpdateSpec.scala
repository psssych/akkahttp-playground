import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}

class TodoRouterUpdateSpec extends WordSpec with Matchers with ScalatestRouteTest with TodoMocks {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  val id: String = UUID.randomUUID().toString
  val testTodo = Todo(id, "original title", "original description", done = false)
  val testUpdateTodo = UpdateTodo(Some("test todo"), None, Some(true))

  "TodoRouter" should {

    "update a todo with valid data" in {
      val repository = new InMemoryTodoRepository(Seq(testTodo))
      val router = new TodoRouter(repository)

      Put(s"/todos/$id", testUpdateTodo) ~> router.route ~> check {
        status shouldBe StatusCodes.OK
        val response = responseAs[Todo]
        response.id shouldBe id
        response.title shouldBe testUpdateTodo.title.get
        response.description shouldBe testTodo.description
        response.done shouldBe testUpdateTodo.done.get
      }
    }

    "return not found in non existent todo" in {
      val repository = new InMemoryTodoRepository(Seq(testTodo))
      val router = new TodoRouter(repository)

      Put(s"/todos/1", testUpdateTodo) ~> router.route ~> check {
        status shouldBe ApiError.TodoNotFoundError("1").statusCode
        val response = responseAs[String]
        response shouldBe ApiError.TodoNotFoundError("1").message
      }
    }

    "not update a todo with invalid data" in {
      val repository = new InMemoryTodoRepository(Seq(testTodo))
      val router = new TodoRouter(repository)

      Put(s"/todos/$id", testUpdateTodo.copy(title = Some(""))) ~> router.route ~> check {
        status shouldBe ApiError.EmptyTitleFieldError.statusCode
        val response = responseAs[String]
        response shouldBe ApiError.EmptyTitleFieldError.message
      }
    }

    "handle repository failures when updaeing todos" in {
      val repository = FailingTodoRepository
      val router = new TodoRouter(repository)

      Put(s"/todos/$id", testUpdateTodo) ~> router.route ~> check {
        status shouldBe ApiError.GenericApiError.statusCode
        val response = responseAs[String]
        response shouldBe ApiError.GenericApiError.message
      }
    }
  }
}
