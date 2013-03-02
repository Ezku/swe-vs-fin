package controllers

import play.api._
import play.api.mvc._
import models.integrations._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json._

object Application extends Controller {
  def index = Action {
    Ok(views.html.index("Sweden vs Finland - public transportation edition"))
  }
  
  def vrTrainList = Action {
    Async {
      VrOpenData.fetchTrainList map { list =>
        val trains = Json.obj(
          "trains" -> Json.arr((list \\ "item").map { train =>
            Json.obj(
              "guid" -> (train \ "guid").text,
              "title" -> (train \ "title").text,
              "status" -> (train \ "status").text
            )
          })
        )
        Ok(Json.stringify(trains))
      }
    }
  }
  
  def sjStationList = Action {
    Async {
      TrafikverketTrainInfo.fetchStationList map { list =>
        val stations = Json.obj(
          "stations" -> Json.arr((list \\ "Station").map { station =>
            Json.obj(
              "signature" -> (station \ "Signatur").text,
              "name" -> (station \ "Namn").text
            )
          })
        )
        Ok(Json.stringify(stations))
      }
    }
  }
}