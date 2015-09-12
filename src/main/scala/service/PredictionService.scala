package service

import java.util.UUID

import api.dto.{EnqueuedPredictionStatusDto, PredictionDto}
import model.Prediction
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import jp.sf.amateras.solr.scala._

/** Service to schedule and process predictions against the ML stack */
trait PredictionService {

  /** Enqueues a tweet [[api.dto.PredictionDto]] for processing and returns a reference id as [[api.dto.EnqueuedPredictionStatusDto]] */
  def enqueue(dto: PredictionDto, requestUri: String): Future[Option[EnqueuedPredictionStatusDto]]

  /** Returns the prediction status / result of an enqueued tweet by processingId */
  def status(processingId: String): Future[Option[Prediction]]

}

object PredictionService extends PredictionService {

  val solrClient = new SolrClient("http://localhost:8983/solr/twitterist")

  /** @inheritdoc*/
  override def enqueue(dto: PredictionDto, requestUri: String): Future[Option[EnqueuedPredictionStatusDto]] = Future {

    val processingId = UUID.randomUUID().toString

    solrClient.add(Prediction(processingId, dto.tweet, dto.author))
    .commit()

    Some(
      EnqueuedPredictionStatusDto(
        processingId,
        (requestUri.split('/').dropRight(1) ++ Array("status", processingId)).mkString("/")
      )
    )
  }

  override def status(processingId: String): Future[Option[Prediction]] = Future {

    //TODO implement result check

    Some(Prediction(processingId, "tweet", "author", Prediction.ENQUEUED, Some("Prediction successful"), Some(0.6256)))
  }
}
