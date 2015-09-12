import akka.actor.{ ActorRef, ActorSystem, Props }
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import api.ApiRouterActor
import com.typesafe.config.{ConfigFactory, Config}
import service.{PredictionService, UserService}
import spray.can.Http
import utils.Config._

import scala.concurrent.duration._

/** Main application class */
object Application extends App {

  val conf: Config = ConfigFactory.load()

  /** Main actor system instance */
  implicit val system = ActorSystem(app.systemName, conf)

  /** Main actor instance configured as spray router ([[api.ApiRouterActor]]) */
  val apiRouterActor: ActorRef = system.actorOf(Props(
    classOf[ApiRouterActor],
    UserService,
    PredictionService
  ), app.userServiceName)

  /** Defines application timeout */
  implicit val timeout = Timeout(5.seconds)
  
  // start a new HTTP server with service actor as the handler
  IO(Http) ? Http.Bind(apiRouterActor, interface = app.interface, port = app.port)

}
