package models.integrations

import play.api.libs.ws.{ WS, Response }
import scala.xml.XML
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Play

object TrafikverketTrainexport {
  lazy val apiKey = Play.current.configuration.getString("integrations.trafikverket.trainexport.key").getOrElse("")
  private lazy val trainList = WS.url("https://api.trafiklab.se/trafikverket/trainexport/traffic")
  private def bodyToXml(response: Response) = XML.loadString(response.body)

  lazy val fetchTrainList = 
    trainList
      .withQueryString("key" -> apiKey)
      .get()
      .map(bodyToXml _)
}