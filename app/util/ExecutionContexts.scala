package util

import scala.concurrent.ExecutionContext

class ExecutionContexts {

  implicit lazy val dbExecutionContext: ExecutionContext = play.api.libs.concurrent.Execution.defaultContext


}
