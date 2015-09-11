package model

import com.wordnik.swagger.annotations.{ApiModelProperty, ApiModel}
import spray.json.DefaultJsonProtocol

import scala.annotation.meta.field

@ApiModel(description = "The prediction result")
case class PredictionResult (
                 @(ApiModelProperty @field)(value = "unique identifier for the prediction")
                 processingId: Int,

                 @(ApiModelProperty @field)(value = "returns the status of the prediction [ENQUEUED, RUNNING, FINISHED, ERROR]")
                 status: String = PredictionResult.defaultStatus,

                 @(ApiModelProperty @field)(value = "a human readable message also used in error case")
                 message: Option[String] = None,

                 @(ApiModelProperty @field)(value = "the predicted highscore")
                 highscore: Option[Double] = None)

object PredictionResult extends DefaultJsonProtocol {

  final val ENQUEUED = "ENQUEUED"
  final val RUNNING = "RUNNING"
  final val FINISHED = "FINISHED"
  final val ERROR = "ERROR"

  implicit val predictionStatusFormat = jsonFormat4(PredictionResult.apply)

  val defaultStatus = ENQUEUED
}
