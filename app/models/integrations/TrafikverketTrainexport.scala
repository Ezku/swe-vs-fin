package models.integrations

import play.api.libs.ws.{ WS, Response }
import scala.xml.XML
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Play
import org.joda.time.DateTime
import org.joda.time.Interval
import scala.util.Try

object TrafikverketTrainexport {
  object XmlData {
    lazy val apiKey = Play.current.configuration.getString("integrations.trafikverket.trainexport.key").getOrElse("")
    private lazy val trainList = WS.url("https://api.trafiklab.se/trafikverket/trainexport/traffic")
    private def bodyToXml(response: Response) = XML.loadString(response.body)

    lazy val fetchTrainList = 
      trainList
        .withQueryString(
          "key" -> apiKey
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
  ) {
    def isValid =
      (from.length > 0) &&
      (to.length > 0) &&
      (title.length > 0)
    def hasDeparted =
      (actualDeparture.length > 0)
    def lateness = hasDeparted match {
      case true =>
        Try(Some(
          DateTime.parse(actualDeparture).getMillis() - DateTime.parse(scheduledDeparture).getMillis()
        )).getOrElse(None)
      case _ => None
    }
  }

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
    }.groupBy(_.guid).mapValues { trainStops =>
      trainStops
      	.filter({train => train.isValid && train.hasDeparted})
      	.sortBy(_.actualDeparture)
      	.reverse
      	.headOption      
    }.values.flatten
  }
}