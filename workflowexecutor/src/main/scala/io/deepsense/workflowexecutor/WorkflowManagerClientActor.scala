/**
 * Copyright 2015, deepsense.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.deepsense.workflowexecutor

import scala.concurrent.Future

import akka.actor.Actor
import akka.pattern.pipe
import spray.client.pipelining._
import spray.http.{HttpRequest, HttpResponse, StatusCodes}
import spray.json._

import io.deepsense.models.json.graph.GraphJsonProtocol.GraphReader
import io.deepsense.models.json.workflow.WorkflowWithResultsJsonProtocol
import io.deepsense.models.workflows.{ExecutionReport, Workflow, WorkflowWithResults}
import io.deepsense.workflowexecutor.WorkflowManagerClientActorProtocol.{GetWorkflow, Request, SaveState, SaveWorkflow}
import io.deepsense.workflowexecutor.exception.UnexpectedHttpResponseException

class WorkflowManagerClientActor(
    val workflowApiAddress: String,
    val workflowApiPrefix: String,
    val reportsApiPrefix: String,
    override val graphReader: GraphReader)
  extends Actor
  with WorkflowWithResultsJsonProtocol {

  import context.dispatcher

  private val pipeline: HttpRequest => Future[HttpResponse] = sendReceive

  override def receive: Receive = {
    case r: Request => r match {
      case GetWorkflow(workflowId) => getWorkflow(workflowId) pipeTo sender()
      case SaveWorkflow(workflow) => saveWorkflowWithState(workflow) pipeTo sender()
      case SaveState(workflowId, state) => saveState(workflowId, state) pipeTo sender()
    }
    case message => unhandled(message)
  }

  private val downloadWorkflowUrl = (workflowId: Workflow.Id) =>
    s"$workflowApiAddress/$workflowApiPrefix/$workflowId"
  private val saveWorkflowWithStateUrl = (workflowId: Workflow.Id) =>
    s"$workflowApiAddress/$workflowApiPrefix/$workflowId"
  private val saveStateUrl = (workflowId: Workflow.Id) =>
    s"$workflowApiAddress/$reportsApiPrefix/$workflowId"

  private def getWorkflow(workflowId: Workflow.Id): Future[Option[WorkflowWithResults]] = {
    pipeline(Get(downloadWorkflowUrl(workflowId)))
      .map(handleGetResponse)
  }

  private def saveWorkflowWithState(workflow: WorkflowWithResults): Future[Unit] = {
    pipeline(Put(saveWorkflowWithStateUrl(workflow.id), workflow))
      .map(handleUploadResponse)
  }

  private def saveState(workflowId: Workflow.Id, state: ExecutionReport): Future[Unit] = {
    pipeline(Post(saveStateUrl(workflowId), state))
      .map(handleUploadResponse)
  }

  private def handleGetResponse(response: HttpResponse): Option[WorkflowWithResults] = {
    response.status match {
      case StatusCodes.OK =>
        Some(response.entity.data.asString.parseJson.convertTo[WorkflowWithResults])
      case StatusCodes.NotFound =>
        None
      case _ => throw UnexpectedHttpResponseException(
        "Workflow download failed", response.status, response.entity.data.asString)
    }
  }

  private def handleUploadResponse(response: HttpResponse): Unit = {
    response.status match {
      case success: StatusCodes.Success => ()
      case _ => throw UnexpectedHttpResponseException(
        "Upload failed", response.status, response.entity.data.asString)
    }
  }
}

object WorkflowManagerClientActorProtocol {

  sealed trait Request

  case class GetWorkflow(workflowId: Workflow.Id) extends Request
  case class SaveWorkflow(workflow: WorkflowWithResults) extends Request
  case class SaveState(workflowId: Workflow.Id, state: ExecutionReport) extends Request
}
