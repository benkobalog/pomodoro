package utils.implicits

import play.api.http.{ContentTypeOf, ContentTypes, Writeable}
import play.api.mvc.Codec

object Play {
  implicit def writeable(implicit codec: Codec): Writeable[io.circe.Json] = {
    Writeable(json => codec.encode(json.toString))
  }

  implicit def contentTypeOfCirceJson(implicit codec: Codec): ContentTypeOf[io.circe.Json] = {
    ContentTypeOf[io.circe.Json](Some(ContentTypes.JSON))
  }
}
