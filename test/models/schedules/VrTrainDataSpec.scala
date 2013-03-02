package test.models.schedules

import org.specs2.mutable._
import models.schedules.VrTrainData
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class VrTrainDataSpec extends Specification {
	"Train list" should {
		"be fetched asynchronously" in {
			VrTrainData.fetchTrainList should beAnInstanceOf[Future[_]]
		}
		"contain RSS items" in {
			for (list <- VrTrainData.fetchTrainList) {
				list should \\("item")
			}
		}
		"have RSS items with train data" in {
			for {
			 	list <- VrTrainData.fetchTrainList
			 	item <- list \\ "item"
			} {
				item should \("guid")
			}
		}
	}
	
	"Train data" should {
		"be fetched asynchronously" in {
			VrTrainData.fetchTrainData("foo") should beAnInstanceOf[Future[_]]
		}
	}
}