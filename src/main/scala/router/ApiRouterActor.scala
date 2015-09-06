package router

import akka.actor.{ Actor, ActorLogging }
import com.gettyimages.spray.swagger.SwaggerHttpService
import com.wordnik.swagger.model.ApiInfo
import service.UserService

import scala.reflect.runtime.universe._

/** Main actor used for routing the api endpoints
 *
 * @param userServiceImpl The user service needed for auth
 */
class ApiRouterActor(userServiceImpl: UserService) extends Actor with UserRouter with ActorLogging with Authenticator {

  /** UserService ref for [[router.Authenticator]] */
  override val userService = userServiceImpl

  /** SwaggerHttpService instance used for API doc
    *
    * @see https://github.com/gettyimages/spray-swagger
    * @see http://swagger.io/
    */
  val swaggerService = new SwaggerHttpService {
    override def apiTypes = Seq(typeOf[UserRouterDoc])
    override def apiVersion = "1.0"
    override def baseUrl = "/"
    override def docsPath = "api-docs-json"
    override def actorRefFactory = context
    override def apiInfo = Some(new ApiInfo(
      "Twitterist API",
      "This is the twitterist backend api. See docs below to write your own client.",
      "http://twitterist.org",
      "project@twitterist.org",
      "Licensed under MIT",
      "https://github.com/twitterist/backend"
    ))
  }

  override def actorRefFactory = context

  /** Actors main receive used for routing */
  def receive = runRoute(
    userOperations ~
    swaggerService.routes ~
    get {
      pathPrefix("") {
        pathEndOrSingleSlash {
          getFromResource("swagger-ui/index.html")
        }
      } ~
      pathPrefix("webjars") {
        getFromResourceDirectory("META-INF/resources/webjars")
      }
    }
  )

}