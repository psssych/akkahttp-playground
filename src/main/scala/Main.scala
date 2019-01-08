import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

import scala.concurrent.Await
import scala.util.{Failure, Success}

object Main extends App {

  val host = "0.0.0.0"
  val port = 9000

  implicit val system: ActorSystem = ActorSystem(name = "todoapi")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  import system.dispatcher

  val initialTodos = Seq(
    Todo("0", "Do shit", "Shit needs to be done", false),
    Todo("1", "Do more shit", "Theres a crap load of shit to be done !", true)
  )

  val todoRepository = new InMemoryTodoRepository(initialTodos)
  val router = new TodoRouter(todoRepository)
  val server = new Server(router, host, port)

  val binding = server.bind()

  binding.onComplete {
    case Success(_) => println("Fuck yeah !!!")
    case Failure(exception) => println(s"Failed :C -> ${exception.getMessage}")
  }

  import scala.concurrent.duration._
  Await.result(binding, 3.seconds)

}
