package model

import api.dto.PredictionDto
import com.wordnik.swagger.annotations.{ApiModelProperty, ApiModel}
import spray.json.DefaultJsonProtocol

import scala.annotation.meta.field

/**
 *
 * @param id The id of the processing result
 * @param status The processing status as string
 * @param message A human readable text message
 * @param highscore A Highscore indicator (double) between 0 and 1
 */
@ApiModel(description = "The prediction result")
case class Prediction (
                 @(ApiModelProperty @field)(value = "unique identifier for the prediction")
                 id: String,

                 @(ApiModelProperty @field)(required = true, value = "Tweet to predict (140 chars)")
                 tweet: String,

                 @(ApiModelProperty @field)(required = true, value = "Screen name of the tweets author")
                 author: String,

                 @(ApiModelProperty @field)(value = "returns the status of the prediction [ENQUEUED, RUNNING, FINISHED, ERROR]")
                 status: String = Prediction.defaultStatus,

                 @(ApiModelProperty @field)(value = "a human readable message also used in error case")
                 message: Option[String] = None,

                 @(ApiModelProperty @field)(value = "the predicted highscore")
                 highscore: Option[Double] = None)

/** Companion object used for constants and JsonFormat */
object Prediction extends DefaultJsonProtocol {

  final val ENQUEUED = "ENQUEUED"
  final val RUNNING = "RUNNING"
  final val FINISHED = "FINISHED"
  final val ERROR = "ERROR"

  implicit val predictionStatusFormat = jsonFormat6(Prediction.apply)

  val defaultStatus = ENQUEUED
}
