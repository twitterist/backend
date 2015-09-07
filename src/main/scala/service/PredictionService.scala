package service

import api.PredictionDto
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait PredictionService {
  /** Enqueues a tweet [[PredictionDto]] for processing and returns a reference id */
  def enqueuePrediction(dto: PredictionDto): Future[Option[Int]]
}

object PredictionService extends PredictionService {

  /** @inheritdoc */
  override def enqueuePrediction(dto: PredictionDto): Future[Option[Int]] = Future {

    //TODO implement queue and return reference id

    Some(42)
  }
}
