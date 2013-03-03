package controllers

import play.api._
import play.api.mvc._
import models.integrations._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json._
import play.api.cache.Cached
import play.api.Play.current

object Application extends Controller {
  def index = Action {
    Ok(views.html.index("Sweden vs Finland - public transportation edition"))
  }

  def vrTrainList = Cached(_.uri, 300) {
    Action {
      Async {
        VrOpenData.fetchTrainList map { list =>
          Ok(Json.obj(
            "trains" -> list.map { train =>
              Json.obj(
                "guid" -> train.guid,
                "title" -> train.title,
                "lateness" -> train.lateness,
                "stops" -> train.stops.map { stop =>
                  Json.obj(
                    "guid" -> stop.guid,
                    "title" -> stop.title,
                    "scheduledArrival" -> stop.scheduledArrival,
                    "scheduledDeparture" -> stop.scheduledDeparture,
                    "estimatedArrival" -> stop.estimatedArrival,
                    "estimatedDeparture" -> stop.estimatedDeparture,
                    "completed" -> stop.completed,
                    "status" -> stop.status)
                })
            }))
        }
      }
    }
  }

  def sjTrainList = Cached(_.uri, 300) {
    Action {
      Async {
        TrafikverketTrainexport.fetchTrainList map { list =>
          Ok(Json.obj(
            "trains" -> list.map { train =>
              Json.obj(
                "guid" -> train.guid,
                "title" -> train.title,
                "from" -> train.from,
                "to" -> train.to,
                "scheduledDeparture" -> train.scheduledDeparture,
                "actualDeparture" -> train.actualDeparture,
                "scheduledArrival" -> train.scheduledArrival,
                "actualArrival" -> train.actualArrival,
                "lateness" -> train.lateness)
            }))
        }
      }
    }
  }
}