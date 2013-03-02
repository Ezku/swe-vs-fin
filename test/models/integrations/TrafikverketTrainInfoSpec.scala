package test.models.integrations

import org.specs2.mutable._
import models.integrations.TrafikverketTrainInfo
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.test._
import play.api.test.Helpers._

class TrafikverketTrainInfoSpec extends Specification {
	"Station list" should {
		"be fetched asynchronously" in {
			running(FakeApplication()) {
				TrafikverketTrainInfo.fetchStationList should beAnInstanceOf[Future[_]]
			}
		}
		"should contain stations as XML" in {
			running(FakeApplication()) {
			    for (stations <- TrafikverketTrainInfo.fetchStationList) {
			      stations should \\("Station")
			    }
			}
		}
		"should have station names" in {
			running(FakeApplication()) {
			    for {
			      stations <- TrafikverketTrainInfo.fetchStationList
			      station <- stations \\ "Station"
			    } {
			      station should \("Namn")
			    }
			}
		}
	}
}