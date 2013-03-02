package models.integrations

import play.api.libs.ws.{ WS, Response }
import scala.concurrent.Future
import scala.xml.XML
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Play

object TrafikverketTrainInfo {
  lazy val apiKey = Play.current.configuration.getString("integrations.trafikverket.traininfo.key").getOrElse("")
  private lazy val stationList = WS.url("https://api.trafiklab.se/trafikverket/traininfo/stations/listAllCurrentlyUsed")
  private def arrivalList(station: String) = WS.url(s"https://api.trafiklab.se/trafikverket/traininfo/stations/sign/${station}/departures")
  private def trainInfo(trainId: String) = WS.url(s"https://api.trafiklab.se/trafikverket/traininfo/trains/${trainId}")
  private def bodyToXml(response: Response) = XML.loadString(response.body)

  lazy val fetchStationList =
    stationList
      .withQueryString("key" -> apiKey)
      .get()
      .map(bodyToXml _)
  
  def fetchArrivalList(stationSignature: String) =
    arrivalList(stationSignature)
      .withQueryString("key" -> apiKey, "maxItems" -> "500", "maxHours" -> "48")
      .get()
      .map(bodyToXml _)
  
  def fetchTrainInfo(trainId: String) =
    trainInfo(trainId)
      .withQueryString("key" -> apiKey)
      .get()
      .map(bodyToXml _)
    
}