package api

import api.dto.{EnqueuedPredictionStatusDto, PredictionDto, UserDto}
import model.User
import org.specs2.mock._
import org.specs2.mutable.Specification
import service.{PredictionService, UserService}
import spray.http.{ StatusCodes, BasicHttpCredentials }
import spray.httpx.SprayJsonSupport._
import spray.testkit.Specs2RouteTest
import utils.{ DatabaseSupportSpec, SpecSupport }

class PredictionIntegrationSpec extends Specification with Specs2RouteTest with PredictionRouter with SpecSupport with Authenticator with Mockito {

  // connects the DSL to the test ActorSystem
  implicit def actorRefFactory = system

  override val userService = UserService

  override val predictionService = PredictionService

  val user = BasicHttpCredentials("test1@test.com", "password1")

  "Prediction Endpoint" should {
    "leave GET requests to other paths unhandled" in this {
      Get("/prediction/foo") ~> addCredentials(user) ~> predictionOperations ~> check {
        handled must beFalse
      }
    }
  }

  "Prediction Endpoint#enqueue" should {
    "leave GET requests unhandled" in this {
      Get("/prediction/enqueue") ~> addCredentials(user) ~> predictionOperations ~> check {
        handled must beFalse
      }
    }

    "return a prediction status for POST requests to enqueue path" in this {
      Post("/prediction/enqueue", PredictionDto("This is a test Tweet @tw1tterist", "frank_neff")) ~> addCredentials(user) ~> predictionOperations ~> check {
        status mustEqual StatusCodes.Created
        //TODO check respose object
      }
    }
  }
}

