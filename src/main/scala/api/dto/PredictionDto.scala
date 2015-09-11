package api.dto

import com.wordnik.swagger.annotations.{ApiModel, ApiModelProperty}
import spray.json.DefaultJsonProtocol

import scala.annotation.meta.field

@ApiModel(description = "Prediction enqueue for potential tweets")
case class PredictionDto(
                    @(ApiModelProperty @field)(required = true, value = "Tweet to predict (140 chars)")
                    tweet: String,

                    @(ApiModelProperty @field)(required = true, value = "Screen name of the tweets author")
                    author: String)

object PredictionDto extends DefaultJsonProtocol{
  implicit val predictionDtoFormat = jsonFormat2(PredictionDto.apply)
}
