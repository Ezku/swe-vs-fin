package models.integrations

import play.api.libs.ws.{ WS, Response }
import scala.xml.XML
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Play

object TrafikverketTrainexport {
  object XmlData {
    lazy val apiKey = Play.current.configuration.getString("integrations.trafikverket.trainexport.key").getOrElse("")
    private lazy val trainList = WS.url("https://api.trafiklab.se/trafikverket/trainexport/traffic")
    private def bodyToXml(response: Response) = XML.loadString(response.body)

    lazy val fetchTrainList = 
      trainList
        .withQueryString(
          "key" -> apiKey,
          "TrafikplatsPrognos" -> "true"
        )
        .get()
        .map(bodyToXml _)
  }

  case class TrainDetails(
    val guid: String,
    val title: String,
    val from: String,
    val to: String,
    val scheduledDeparture: String,
    val actualDeparture: String,
    val scheduledArrival: String,
    val actualArrival: String
  )

  def fetchTrainList = XmlData.fetchTrainList.map { list => 
    (list \\ "Trafiklage").map { train =>
      TrainDetails(
        (train \ "TagGrupp").text,
        (train \ "AnnonseratTagId").text,
        (train \ "Fran").text,
        (train \ "Till").text,
        (train \ "AnnonseradTidpunktAvgang").text,
        (train \ "VerkligTidpunktAvgang").text,
        (train \ "AnnonseradTidpunktAnkomst").text,
        (train \ "VerkligTidpunktAnkomst").text
      )
    }
  }
}