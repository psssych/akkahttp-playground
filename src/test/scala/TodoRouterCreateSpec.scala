import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}

class TodoRouterCreateSpec extends WordSpec with Matchers with ScalatestRouteTest with TodoMocks {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  "TodoRouter" should {

    val testCreateTodo = CreateTodo("test todo", "test description")
    "create a new todo with valid data" in {
      val repository = new InMemoryTodoRepository()
      val router = new TodoRouter(repository)

      Post("/todos", testCreateTodo) ~> router.route ~> check {
        status shouldBe StatusCodes.OK
        val response = responseAs[CreateTodo]
        response shouldBe testCreateTodo
      }
    }

    "not create a new todo without Title" in {
      val repository = new InMemoryTodoRepository()
      val router = new TodoRouter(repository)

      Post("/todos", CreateTodo("", "")) ~> router.route ~> check {
        status shouldBe ApiError.EmptyTitleFieldError.statusCode
        val resp = responseAs[String]
        resp shouldBe ApiError.EmptyTitleFieldError.message
      }
    }

    "handle repository failure when creating todos" in {
      val repository = FailingTodoRepository
      val router = new TodoRouter(repository)

      Post("/todos", testCreateTodo) ~> router.route ~> check {
        status shouldBe ApiError.GenericApiError.statusCode
        val response = responseAs[String]
        response shouldBe ApiError.GenericApiError.message
      }
    }
  }
}
