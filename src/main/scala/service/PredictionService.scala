package service

import api.dto.{EnqueuedPredictionStatusDto, PredictionDto}
import model.PredictionResult
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait PredictionService {
  /** Enqueues a tweet [[PredictionDto]] for processing and returns a reference id as [[EnqueuedPredictionStatusDto]] */
  def enqueue(dto: PredictionDto, requestUri: String): Future[Option[EnqueuedPredictionStatusDto]]

  def status(processingId: BigInt): Future[Option[PredictionResult]]
}

object PredictionService extends PredictionService {

  /** @inheritdoc*/
  override def enqueue(dto: PredictionDto, requestUri: String): Future[Option[EnqueuedPredictionStatusDto]] = Future {

    val processingId = 42 //TODO implement prediction

    Some(
      EnqueuedPredictionStatusDto(
        processingId,
        (requestUri.split('/').dropRight(1) ++ Array("status", processingId)).mkString("/")
      )
    )
  }

  override def status(processingId: BigInt): Future[Option[PredictionResult]] = Future {

    //TODO implement result check

    Some(PredictionResult(42, PredictionResult.ENQUEUED, Some("Prediction successful"), Some(0.6256)))
  }
}
