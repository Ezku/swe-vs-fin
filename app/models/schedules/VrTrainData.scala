package models.schedules

import play.api.libs.ws.{WS,Response}
import scala.concurrent.Future
import scala.xml.XML
import scala.concurrent.ExecutionContext.Implicits.global

object VrTrainData {
	private lazy val trainList = WS.url("http://188.117.35.14/TrainRSS/TrainService.svc/AllTrains")
	private def trainData(guid: String) = WS.url(s"http://188.117.35.14/TrainRSS/TrainService.svc/trainInfo?train=${guid}")
	private def bodyToXml(response: Response) = XML.loadString(response.body)

	lazy val fetchTrainList = trainList.get().map(bodyToXml _)
	def fetchTrainData(guid: String) = trainData(guid).get().map(bodyToXml _)
}