package api

import com.wordnik.swagger.annotations._
import service.{PredictionService, UserService}
import spray.http.MediaTypes._
import spray.http.StatusCodes._
import spray.httpx.SprayJsonSupport._
import spray.routing.{HttpService, Route}
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

/** API router for tweet predictions
  *
  * Used in [[ApiRouterActor]] tou dispatch API routing
  */
trait PredictionRouter extends HttpService with PredictionRouterDoc {
  self: Authenticator =>

  val predictionService: PredictionService

  val predictionOperations: Route = predictionEnqueue

  override def predictionEnqueue: Route = path("tweet/prediction") {
    post {
      authenticate(basicUserAuthenticator) { authInfo =>
        entity(as[PredictionDto]) { prediction =>
          respondWithMediaType(`application/json`) {
            onComplete(predictionService.enqueuePrediction(prediction)) {
              case Success(Some(processingId)) => complete(Created, processingId.toString) //TODO JSON format
              case Success(None) => complete(NotAcceptable, "Invalid prediction")
              case Failure(ex) => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
            }
          }
        }
      }
    }
  }
}


/** API documentation and spec for the [[PredictionRouter]]
 * @note Used for swagger ui
 */
@Api(value = "/tweet/prediction", description = "Tweet prediction endpoint", consumes = "application/json", produces = "application/json")
trait PredictionRouterDoc {

  @ApiOperation(value = "Predict an new tweet", httpMethod = "POST", consumes = "application/json")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = "Tweet prediction object to be added", required = true, dataType = "api.PredictionDto", paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Bad request / invalid JSON provided"),
    new ApiResponse(code = 403, message = "Unauthorized"),
    new ApiResponse(code = 201, message = "Entity Created")
  ))
  def predictionEnqueue: Route
}
