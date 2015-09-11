package api.dto

import com.wordnik.swagger.annotations.{ApiModelProperty, ApiModel}
import spray.json.DefaultJsonProtocol

import scala.annotation.meta.field

/** DTO to return and enqueued prediction to the API client
 *
 * @param processingId A unique id used for processing the prediction
 * @param statusUrl A fully qualified URL where to check the status of the prediction
 */
@ApiModel(description = "Prediction enqueue for potential tweets")
case class EnqueuedPredictionStatusDto(
  @(ApiModelProperty@field)(required = true, value = "The id of the enqueued tweet")
  processingId: Int,

  @(ApiModelProperty@field)(required = true, value = "The url to call to get status / result of the tweet")
  statusUrl: String
)

/** JSON format for the [[EnqueuedPredictionStatusDto]] */
object EnqueuedPredictionStatusDto extends DefaultJsonProtocol{
  implicit val enqueuedPredictionStatusDtoFormat = jsonFormat2(EnqueuedPredictionStatusDto.apply)
}
