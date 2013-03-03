package models.integrations

import play.api.libs.ws.{WS,Response}
import scala.concurrent.Future
import scala.xml.XML
import scala.concurrent.ExecutionContext.Implicits.global

object VrOpenData {

  object XmlData {
    private lazy val trainList = WS.url("http://188.117.35.14/TrainRSS/TrainService.svc/AllTrains")
    private def trainData(guid: String) = WS.url(s"http://188.117.35.14/TrainRSS/TrainService.svc/trainInfo?train=${guid}")
    private def bodyToXml(response: Response) = XML.loadString(response.body)

    lazy val fetchTrainList = trainList.get().map(bodyToXml _)
    def fetchTrainData(guid: String) = trainData(guid).get().map(bodyToXml _)
  }

  case class TrainInfo(
    val guid: String,
    val title: String,
    val status: String
  )

  def fetchTrainList = XmlData.fetchTrainList.flatMap { list =>
    val trains = (list \\ "item").map { train =>
      TrainInfo(
        (train \ "guid").text,
        (train \ "title").text,
        (train \ "status").text
      )
    }
    Future.sequence {
      trains.map { train => fetchTrainData(train.guid) }
    }
  }

  case class TrainDetails(
    val guid: String,
    val title: String,
    val lateness: String,
    val stops: Seq[TrainStop]
  )
  case class TrainStop(
    val guid: String,
    val title: String,
    val scheduledArrival: String,
    val scheduledDeparture: String,
    val estimatedArrival: String,
    val estimatedDeparture: String,
    val completed: String,
    val status: String
  )

  def fetchTrainData(guid: String) = XmlData.fetchTrainData(guid).map (_ \ "channel") map { data =>
    TrainDetails(
      (data \ "trainguid").text,
      (data \ "title").text,
      (data \ "lateness").text,
      (data \ "item").map { stop =>
        TrainStop(
           (stop \ "guid").text,
           (stop \ "title").text,
           (stop \ "scheduledTime").text,
           (stop \ "scheduledDepartTime").text,
           (stop \ "eta").text,
           (stop \ "etd").text,
           (stop \ "completed").text,
           (stop \ "status").text
        )
      }
    )
  }
}