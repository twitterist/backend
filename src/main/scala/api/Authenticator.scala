package api

import model.{AuthInfo, User, UserPassword}
import service.UserService
import spray.routing.authentication.{BasicAuth, UserPass}
import spray.routing.directives.AuthMagnet

import scala.concurrent.{ExecutionContext, Future}

/** Authentication service used in the controllers */
trait Authenticator {

  /** The user service to fetch user data */
  def userService: UserService

  /** Main method for authentication
   *
   * @param ec Injected ExecutionContext from spray
   * @return A (Basic-)Auth object
   */
  def basicUserAuthenticator(implicit ec: ExecutionContext): AuthMagnet[AuthInfo] = {
    def validateUser(userPass: Option[UserPass]): Future[Option[AuthInfo]] = {
      userPass.fold[Future[Option[AuthInfo]]] {
        Future.successful(None)
      } {
        userPass => Future {
          Some(AuthInfo("username"))
        }
          //TODO auth

      }
    }

    def authenticator(userPass: Option[UserPass]): Future[Option[AuthInfo]] = validateUser(userPass)

    BasicAuth(authenticator _, realm = "Private API")
  }
}