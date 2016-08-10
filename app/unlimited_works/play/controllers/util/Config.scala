package unlimited_works.play.controllers.util

/**
  *
  */
object Config {
  val MAIN_DOMAIN = ".scalachan.com"

  object Cors extends Enumeration {
    val CORS_SERVER_DOMAIN = Value(1, "http://server.scalachan.com")
    val CORS_SERVERS_DOMAIN = Value(2, "https://server.scalachan.com")
    val CORS_WWW_DOMAIN = Value(3, "http://www.scalachan.com")
    val CORS_WWWS_DOMAIN = Value(4, "https://www.scalachan.com")
    val CORS_WWWS_DOMAIN_TEST = Value(5, "https://www.scalachan.com:4000")
    val CORS_WWWSs_DOMAIN_TEST = Value(6, "https://www.scalachan.com:4000")

    def find(src: String) = {
      values.find{x =>
        src.startsWith(x.toString)
      }
    }
  }

  object CookieSession{
    val GOD_SESSION = "GOD_SESSION"
    val ACCOUNT_ID = "accountId"
  }
}
