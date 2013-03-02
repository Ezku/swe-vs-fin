package test.models.schedules

import org.specs2.mutable._
import models.schedules.VrTrainData
import scala.concurrent.Future

class VrTrainDataSpec extends Specification {
	"Train list" should {
		"be fetched asynchronously" in {
			VrTrainData.fetchTrainList should beAnInstanceOf[Future[_]]
		}
		"contain RSS data" in {
			for (list <- VrTrainData.fetchTrainList) {
				list should \("rss")
			}
		}
	}
}