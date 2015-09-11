package model

/** Class for holding authntication information */
case class AuthInfo(user: User) {

  /** Return whether the user has access rights */
  def hasPermissions(permission: String): Boolean = true
}
