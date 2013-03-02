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
  
  def vrTrainData(guid:String) = Action {
    Async {
      VrOpenData.fetchTrainData(guid) map(_ \ "channel") map { data =>
        
        val train = Json.obj(
            "train" -> Json.obj(
               "guid" -> (data \ "trainguid").text,
               "title" -> (data \ "title").text,
               "lateness" -> (data\ "lateness").text,
               "stops" -> Json.arr((data \ "item").map { stop =>
                 Json.obj(
                     "guid" -> (stop \ "guid").text,
                     "title" -> (stop \ "title").text,
                     "scheduledArrival" -> (stop \ "scheduledTime").text,
                     "scheduledDeparture" -> (stop \ "scheduledDepartTime").text,
                     "estimatedArrival" -> (stop \ "eta").text,
                     "estimatedDeparture" -> (stop \ "etd").text,
                     "completed" -> (stop \ "completed").text,
                     "status" -> (stop \ "status").text
                 )
               })
            )
        )
        Ok(Json.stringify(train))
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
  
  def sjArrivalList(signature: String) = Action {
    Async {
      TrafikverketTrainInfo.fetchArrivalList(signature) map { list =>
        val arrivals = Json.obj(
          "arrivals" -> Json.arr((list \\ "Trafiklage").map { arrival =>
            Json.obj(
              "guid" -> (arrival \ "TagGrupp").text,
              "title" -> (arrival \ "AnnonseratTagId").text,
              "from" -> (arrival \ "Fran").text,
              "to" -> (arrival \ "Till").text,
              "scheduledArrival" -> (arrival \ "AnnonseradTidpunktAnkomst").text,
              "actualArrival" -> (arrival \ "VerkligTidpunktAnkomst").text,
              "estimatedArrival" -> (arrival \ "BeraknadTidpunktAnkomst").text
            )
          })
        )
        Ok(Json.stringify(arrivals))
      }
    }
  }
  
  def sjDepartureList(signature: String) = Action {
    Async {
      TrafikverketTrainInfo.fetchArrivalList(signature) map { list =>
        val departures = Json.obj(
          "departures" -> Json.arr((list \\ "Trafiklage").map { departure =>
            Json.obj(
              "guid" -> (departure \ "TagGrupp").text,
              "title" -> (departure \ "AnnonseratTagId").text,
              "from" -> (departure \ "Fran").text,
              "to" -> (departure \ "Till").text,
              "scheduledDeparture" -> (departure \ "AnnonseradTidpunktAvgang").text,
              "actualDeparture" -> (departure \ "VerkligTidpunktAvgang").text,
              "estimatedDeparture" -> (departure \ "BeraknadTidpunktAvgang").text
            )
          })
        )
        Ok(Json.stringify(departures))
      }
    }
  }
}