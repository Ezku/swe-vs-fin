package models.integrations

import play.api.libs.ws.{WS,Response}
import scala.concurrent.Future
import scala.xml.XML
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Play

object TrafikverketTrainInfo {
	lazy val apiKey = Play.current.configuration.getString("schedules.sj.apiKey").getOrElse("")
	private lazy val stationList = WS.url("https://api.trafiklab.se/trafikverket/traininfo/stations/listAllCurrentlyUsed")
	private def bodyToXml(response: Response) = XML.loadString(response.body)

	lazy val fetchStationList = stationList.withQueryString("key" -> apiKey).get().map(bodyToXml _)
/*

	private lazy val trainList = WS.url("http://188.117.35.14/TrainRSS/TrainService.svc/AllTrains")
	private def trainData(guid: String) = WS.url(s"http://188.117.35.14/TrainRSS/TrainService.svc/trainInfo?train=${guid}")

	lazy val fetchTrainList = trainList.get().map(bodyToXml _)
	def fetchTrainData(guid: String) = trainData(guid).get().map(bodyToXml _)*/
}