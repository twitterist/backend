package api

import api.dto.UserDto
import com.wordnik.swagger.annotations._
import model.User
import service.UserService
import spray.http.MediaTypes._
import spray.http.StatusCodes._
import spray.httpx.SprayJsonSupport._
import spray.routing.{HttpService, Route}
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

/** API router for user management
  *
  * Used in [[ApiRouterActor]] tou dispatch API routing
  */
trait UserRouter extends HttpService with UserRouterDoc {
  self: Authenticator =>

  val userService: UserService

  val userOperations: Route = postRoute ~ readRoute ~ readAllRoute ~ deleteRoute

  override def readRoute = path("users" / IntNumber) { userId =>
    get {
      authenticate(basicUserAuthenticator) { authInfo =>
        respondWithMediaType(`application/json`) {
          onComplete(userService.get(userId)) {
            case Success(Some(user)) => complete(user)
            case Success(None) => complete(NotFound, "User not found")
            case Failure(ex) => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
          }
        }
      }
    }
  }

  override def readAllRoute = path("users") {
    get {
      authenticate(basicUserAuthenticator) { authInfo =>
        respondWithMediaType(`application/json`) {
          onComplete(userService.getAll) {
            case Success(users) => complete(users)
            case Failure(ex) => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
          }
        }
      }
    }
  }

  override def deleteRoute = path("users" / IntNumber) { userId =>
    delete {
      authenticate(basicUserAuthenticator) { authInfo =>
        respondWithMediaType(`application/json`) {
          onComplete(userService.delete(userId)) {
            case Success(ok) => complete(OK)
            case Failure(ex) => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
          }
        }
      }
    }
  }

  override def postRoute: Route = path("users") {
    post {
      authenticate(basicUserAuthenticator) { authInfo =>
        entity(as[UserDto]) { user =>
          respondWithMediaType(`application/json`) {
            onComplete(userService.add(user)) {
              case Success(Some(newUser)) => complete(Created, newUser)
              case Success(None) => complete(NotAcceptable, "Invalid user")
              case Failure(ex) => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
            }
          }
        }
      }
    }
  }
}

/** API documentation and spec for the [[UserRouter]]
  * @note Used for swagger ui
  */
@Api(value = "/users", description = "Operations for users.", consumes = "application/json", produces = "application/json")
trait UserRouterDoc {

  @ApiOperation(value = "Get a user by id", httpMethod = "GET", response = classOf[User])
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "userId", value = "ID of the user that needs to retrieved", required = true, dataType = "integer", paramType = "path")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Ok"),
    new ApiResponse(code = 403, message = "Unauthorized"),
    new ApiResponse(code = 404, message = "User not found")
  ))
  def readRoute: Route

  @ApiOperation(value = "Get all the users", httpMethod = "GET", response = classOf[List[User]])
  def readAllRoute: Route

  @ApiOperation(value = "Delete a user by id", httpMethod = "DELETE", response = classOf[Int])
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "userId", value = "ID of the user that needs to be deleted", required = true, dataType = "integer", paramType = "path")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 404, message = "User not found"),
    new ApiResponse(code = 403, message = "Unauthorized"),
    new ApiResponse(code = 400, message = "Invalid ID supplied")
  ))
  def deleteRoute: Route


  @ApiOperation(value = "Add a new user to the system", httpMethod = "POST", consumes = "application/json")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = "User object to be added", required = true, dataType = "api.dto.UserDto", paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 405, message = "Invalid user"),
    new ApiResponse(code = 403, message = "Unauthorized"),
    new ApiResponse(code = 201, message = "Entity Created")
  ))
  def postRoute: Route
}
