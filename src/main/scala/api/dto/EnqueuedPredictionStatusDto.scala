package api.dto

import com.wordnik.swagger.annotations.{ApiModelProperty, ApiModel}
import spray.json.DefaultJsonProtocol

import scala.annotation.meta.field

@ApiModel(description = "Prediction enqueue for potential tweets")
case class EnqueuedPredictionStatusDto(
  @(ApiModelProperty@field)(required = true, value = "The id of the enqueued tweet")
  processingId: Int,

  @(ApiModelProperty@field)(required = true, value = "The url to call to get status / result of the tweet")
  statusUrl: String
)

object EnqueuedPredictionStatusDto extends DefaultJsonProtocol{
  implicit val enqueuedPredictionStatusDtoFormat = jsonFormat2(EnqueuedPredictionStatusDto.apply)
}
