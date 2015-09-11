package service

import api.dto.{EnqueuedPredictionStatusDto, PredictionDto}
import model.PredictionResult
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/** Service to schedule and process predictions against the ML stack */
trait PredictionService {
  /** Enqueues a tweet [[api.dto.PredictionDto]] for processing and returns a reference id as [[api.dto.EnqueuedPredictionStatusDto]] */
  def enqueue(dto: PredictionDto, requestUri: String): Future[Option[EnqueuedPredictionStatusDto]]

  /** Returns the prediction status / result of an enqueued tweet by processingId */
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
