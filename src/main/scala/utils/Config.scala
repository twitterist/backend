package utils

import com.typesafe.config.ConfigFactory

/** Application configuration used in [[Application]] */
object Config {

  /** Config properties instance
    * @see application.properties
    */
  private val config =  ConfigFactory.load()

  /** Application configuration */
  object app {
    val appConf = config.getConfig("app")

    val systemName = appConf.getString("systemName")
    val interface = appConf.getString("interface")
    val port = appConf.getInt("port")
    val userServiceName = appConf.getString("userServiceName")

  }

  /** Database configuration */
  object dbConfig {
    val dbConfig = config.getConfig("db")

    val url = dbConfig.getString("url")
    val user = dbConfig.getString("user")
    val password = dbConfig.getString("password")
    val driver = dbConfig.getString("driver")
  }
}
