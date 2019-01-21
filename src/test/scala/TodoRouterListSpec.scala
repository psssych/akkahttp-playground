import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{FlatSpec, Matchers, WordSpec}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

class TodoRouterListSpec extends WordSpec with Matchers with ScalatestRouteTest with TodoMocks {
  private val pendingTodo = Todo("0", "Do shit", "Shit needs to be done", false)
  private val doneTodo = Todo("1", "Do more shit", "Theres a crap load of shit to be done !", true)

  private val todos = Seq(pendingTodo, doneTodo)

  "TodoRouter" should {

    val repository = new InMemoryTodoRepository(todos)
    val router = new TodoRouter(repository)

    "return all todos" in {
      Get("/todos") ~> router.route ~> check {
        status shouldBe StatusCodes.OK
        val response = responseAs[Seq[Todo]]
        response shouldBe todos
      }
    }

    "return done todos" in {
      Get("/todos/done") ~> router.route ~> check {
        status shouldBe StatusCodes.OK
        val response = responseAs[Seq[Todo]]
        response.length shouldBe 1
        response.head shouldBe doneTodo
      }
    }

    "return pending todos" in {
      Get("/todos/pending") ~> router.route ~> check {
        status shouldBe StatusCodes.OK
        val response = responseAs[Seq[Todo]]
        response.length shouldBe 1
        response.head shouldBe pendingTodo
      }
    }
  }

  "Failing Repository" should {

    val repository = FailingTodoRepository
    val router = new TodoRouter(repository)

    "handle repository failure in todos route" in {
      Get("/todos") ~> router.route ~> check {
        status shouldBe ApiError.GenericApiError.statusCode
        val resp = responseAs[String]
        resp shouldBe ApiError.GenericApiError.message
      }
    }

    "handle repository failure in todos/done route" in {
      Get("/todos/done") ~> router.route ~> check {
        status shouldBe ApiError.GenericApiError.statusCode
        val resp = responseAs[String]
        resp shouldBe ApiError.GenericApiError.message
      }
    }

    "handle repository failure in todos/pending route" in {
      Get("/todos/pending") ~> router.route ~> check {
        status shouldBe ApiError.GenericApiError.statusCode
        val resp = responseAs[String]
        resp shouldBe ApiError.GenericApiError.message
      }
    }
  }

}
