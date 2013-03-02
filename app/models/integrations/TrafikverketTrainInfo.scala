package models.integrations

import play.api.libs.ws.{ WS, Response }
import scala.concurrent.Future
import scala.xml.XML
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Play

object TrafikverketTrainInfo {
  lazy val apiKey = Play.current.configuration.getString("schedules.sj.apiKey").getOrElse("")
  private lazy val stationList = WS.url("https://api.trafiklab.se/trafikverket/traininfo/stations/listAllCurrentlyUsed")
  private def departureList(station: String) = WS.url(s"https://api.trafiklab.se/trafikverket/traininfo/stations/name/${station}/departures")
  private def bodyToXml(response: Response) = XML.loadString(response.body)

  lazy val fetchStationList =
    stationList
      .withQueryString("key" -> apiKey)
      .get()
      .map(bodyToXml _)
  
  def fetchDepartureList(station: String) =
    departureList(station)
      .withQueryString("key" -> apiKey, "maxItems" -> "500", "maxHours" -> "48")
      .get()
      .map(bodyToXml _)
}