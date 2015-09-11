package api

import akka.actor.{ Actor, ActorLogging }
import com.gettyimages.spray.swagger.SwaggerHttpService
import com.wordnik.swagger.model.ApiInfo
import service.{PredictionService, UserService}

import scala.reflect.runtime.universe._

/** Main actor used for routing the api endpoints
 *
 * @param userServiceImpl The user service needed for auth
 */
class ApiRouterActor(userServiceImpl: UserService, predictionServiceImpl: PredictionService) extends Actor with UserRouter with PredictionRouter with ActorLogging with Authenticator {

  /** UserService ref for [[api.Authenticator]] */
  override val userService = userServiceImpl

  /** [[service.PredictionService]] injection ref. for [[PredictionRouter]] */
  override val predictionService = predictionServiceImpl

  /** SwaggerHttpService instance used for API doc
    *
    * @see https://github.com/gettyimages/spray-swagger
    * @see http://swagger.io/
    */
  val swaggerService = new SwaggerHttpService {
    override def apiTypes = Seq(typeOf[UserRouterDoc], typeOf[PredictionRouterDoc])
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

  /** Reference for the context */
  override def actorRefFactory = context

  /** Actors main receive used for routing */
  def receive = runRoute(
    userOperations ~
    predictionOperations ~
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