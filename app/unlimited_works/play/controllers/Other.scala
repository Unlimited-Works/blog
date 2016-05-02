package unlimited_works.play.controllers

import play.api.mvc.{Controller, Action}
import unlimited_works.play.views

/**
  *
  */
object Other extends Controller {
  def undefined(undefined: String) = Action { implicit request =>
    Ok(views.html.NotFound404())
  }

  def unAuthentication = Action {request =>
    Ok(views.html.UnAuthentication401())
  }
}
