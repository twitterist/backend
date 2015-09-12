package api

import javax.ws.rs.Path

import api.dto.{EnqueuedPredictionStatusDto, PredictionDto}
import com.wordnik.swagger.annotations._
import model.{PredictionResult, User}
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

  /** [[service.PredictionService]] instance for ML operations and queue */
  val predictionService: PredictionService

  /** Holds exposed routes */
  val predictionOperations: Route = predictionEnqueue ~ predictionResult

  /** @inheritdoc */
  override def predictionEnqueue: Route = pathPrefix("prediction") {
    path("enqueue") {
      post {
        requestUri { uri =>
          authenticate(basicUserAuthenticator) { authInfo =>
            entity(as[PredictionDto]) { prediction =>
              respondWithMediaType(`application/json`) {
                onComplete(predictionService.enqueue(prediction, uri.toString())) {
                  case Success(Some(predictionResult)) => complete(Created, predictionResult)
                  case Success(None) => complete(BadRequest, "Invalid prediction")
                  case Failure(ex) => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
                }
              }
            }
          }
        }
      }
    }
  }

  /** @inheritdoc */
  override def predictionResult: Route = pathPrefix("prediction") {
    path("status" / Rest) { processingId =>
      get {
        authenticate(basicUserAuthenticator) { authInfo =>
          respondWithMediaType(`application/json`) {
            onComplete(predictionService.status(processingId)) {
              case Success(Some(predictionResult)) => complete(predictionResult)
              case Success(None) => complete(NotFound, "Invalid processingId")
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
@Api(
  value = "/prediction",
  description = "Tweet prediction endpoint",
  consumes = "application/json",
  produces = "application/json"
)
trait PredictionRouterDoc {

  /** API route to enqueue a new prediction */
  @Path("/enqueue")
  @ApiOperation(
    value = "Enqueue a new tweet prediction",
    httpMethod = "POST",
    consumes = "application/json",
    response = classOf[EnqueuedPredictionStatusDto]
  )
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
      name = "body",
      value = "Tweet prediction object to be added",
      required = true,
      dataType = "api.dto.PredictionDto",
      paramType = "body"
    )
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Bad request - Invalid JSON provided"),
    new ApiResponse(code = 403, message = "Unauthorized"),
    new ApiResponse(code = 201, message = "Created - Prediction was successfully enqueued")
  ))
  def predictionEnqueue: Route

  /** API route to check the status / get the result of an existing prediction */
  @Path("/status/{processingId}")
  @ApiOperation(
    value = "Get prediction status / result",
    httpMethod = "GET",
    response = classOf[PredictionResult]
  )
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
      name = "processingId",
      value = "Id of the enqueued tweet",
      required = true,
      dataType = "integer",
      paramType = "path"
    )
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Ok - JSON result of the prediction"),
    new ApiResponse(code = 202, message = "Accepted - Prediction is not finished yet"),
    new ApiResponse(code = 403, message = "Unauthorized"),
    new ApiResponse(code = 404, message = "Not Found - Prediction with given ID does not exist")
  ))
  def predictionResult: Route
}
