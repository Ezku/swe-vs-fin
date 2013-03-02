package test.models.integrations

import org.specs2.mutable._
import models.integrations.VrOpenData
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class VrOpenDataSpec extends Specification {
  "Train list" should {
    "be fetched asynchronously" in {
      VrOpenData.fetchTrainList should beAnInstanceOf[Future[_]]
    }
    "contain RSS items" in {
      for (list <- VrOpenData.fetchTrainList) {
        list should \\("item")
      }
    }
    "have RSS items with train data" in {
      for {
        list <- VrOpenData.fetchTrainList
        item <- list \\ "item"
      } {
        item should \("guid")
      }
    }
  }

  "Train data" should {
    "be fetched asynchronously" in {
      VrOpenData.fetchTrainData("foo") should beAnInstanceOf[Future[_]]
    }
  }
}