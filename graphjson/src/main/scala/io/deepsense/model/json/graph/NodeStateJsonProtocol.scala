/**
 * Copyright (c) 2015, CodiLime Inc.
 */

package io.deepsense.model.json.graph

import spray.json._

import io.deepsense.commons.json.DateTimeJsonProtocol._
import io.deepsense.commons.json.ExceptionsJsonProtocol
import io.deepsense.graph.{Progress, State}

trait NodeStateJsonProtocol
  extends DefaultJsonProtocol
  with ExceptionsJsonProtocol
  with NullOptions {
  implicit val progressFormat = jsonFormat2(Progress.apply)

  implicit object NodeStateWriter
    extends JsonWriter[State]
    with DefaultJsonProtocol
    with NullOptions {

    override def write(state: State): JsValue = {
      JsObject(
        NodeStateJsonProtocol.Status -> state.status.toString.toJson,
        NodeStateJsonProtocol.Started -> state.started.toJson,
        NodeStateJsonProtocol.Ended -> state.ended.toJson,
        NodeStateJsonProtocol.Progress -> state.progress.toJson,
        NodeStateJsonProtocol.Results -> state.results.map(_.map(_.toString)).toJson,
        NodeStateJsonProtocol.Error -> state.error.toJson
      )
    }
  }
}

object NodeStateJsonProtocol extends NodeStateJsonProtocol {
  val Status = "status"
  val Started = "started"
  val Ended = "ended"
  val Progress = "progress"
  val Results = "results"
  val Error = "error"
}
