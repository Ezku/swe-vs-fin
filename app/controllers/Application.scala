package controllers

import play.api._
import play.api.mvc._
import models.integrations._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json._
import org.scala_tools.time.Imports._

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
        Ok(trains)
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
        Ok(train)
      }
    }
  }
  
  def sjTrainList = Action {
    Async {
      TrafikverketTrainexport.fetchTrainList map { list =>
        val trains = Json.obj(
          "trains" -> Json.arr((list \\ "Trafiklage").map { train =>
            Json.obj(
              "guid" -> (train \ "TagGrupp").text,
              "title" -> (train \ "AnnonseratTagId").text,
              "from" -> (train \ "Fran").text,
              "to" -> (train \ "Till").text,
              "scheduledDeparture" -> (train \ "AnnonseradTidpunktAvgang").text,
              "actualDeparture" -> (train \ "VerkligTidpunktAvgang").text,
              "estimatedDeparture" -> (train \ "BeraknadTidpunktAvgang").text,
              "scheduledArrival" -> (train \ "AnnonseradTidpunktAnkomst").text,
              "actualArrival" -> (train \ "VerkligTidpunktAnkomst").text,
              "estimatedArrival" -> (train \ "BeraknadTidpunktAnkomst").text
            )
          })
        )
        Ok(trains)
      }
    }
  }
}