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
    "contain stations as XML" in {
      running(FakeApplication()) {
        for (stations <- TrafikverketTrainInfo.fetchStationList) {
          stations should \\("Station")
        }
      }
    }
    "have station names" in {
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
  
  "Departure list" should {
    val station = "Rinkeby"
    "be fetched asynchronously" in {
      running(FakeApplication()) {
        TrafikverketTrainInfo.fetchDepartureList(station) should beAnInstanceOf[Future[_]]
      }
    }
    "contain departures as XML" in {
      running(FakeApplication()) {
        for (departures <- TrafikverketTrainInfo.fetchDepartureList(station)) {
          departures should \\("Trafiklage")
        }
      }
    }
    "have train ids for departing trains" in {
      running(FakeApplication()) {
        for {
          departures <- TrafikverketTrainInfo.fetchDepartureList(station)
          departure <- departures \\ "Trafiklage"
        } {
          departure should \("TagGrupp")
        }
      }
    }
  }
}