#' Solve a task with MLR.
#'
#' Solve a task with a specified learner from MLR, compute predictions and upload the results 
#' to the OpenML server.
#' 
#' @param task.id [\code{integer(1)}]\cr 
#'   An (optional) ID number of a task on the OpenML server, used to retrieve the task. 
#'   Either \code{task.id} or \code{task} is required.
#' @param task [\code{\linkS4class{OpenMLTask}}]\cr 
#'   An (optional) OpenML task. 
#'   Either \code{task.id} or \code{task} is required.
#' @param learner [\code{\link[mlr]{Learner}}]\cr 
#'   Learner object from package mlr to solve the task.
#' @param return.mlr.results [\code{logical(1)}]\cr 
#'   Should not only the predictions but all of the by MLR computed information be returned? 
#'   This includes test measures in each step of the resampling procedure as well as the aggregated 
#'   performance. See \code{\link[mlr]{resample}}. Default is \code{TRUE}.
#' @return List of:
#'   \item{run.pred}{[\code{\link[mlr]{ResamplePrediction}}]\cr
#'     Predictions resulting from the run. These are necessary in order to upload a run.}
#'   \item{mlr.resample.results}{[\code{list}]\cr
#'     The results of the MLR function \code{\link[mlr]{resample}}.\cr
#'     \code{NULL} if \code{return.mlr.results == FALSE}.}  
#' @seealso \code{\linkS4class{OpenMLTask}}, \code{\link[mlr]{learners}}, 
#'   \code{\link{authenticateUser}}, \code{\link[mlr]{resample}}
#' @export
runTask <- function(task, learner, return.mlr.results = TRUE) {
  checkArg(task, "OpenMLTask")
  checkArg(learner, "Learner")
  checkArg(return.mlr.results, "logical")
  #FIXME: add regression
  if(task@task.type == "Supervised Classification" & learner$type != "classif")
    stopf("Learner type does not correspond to task type.")
  mlr.task <- toMLR(task)
  res <- resample(learner, mlr.task$mlr.task, mlr.task$mlr.rin, measures = mlr.task$mlr.measures)
  results <- list(
    run.pred = res$pred$data, 
    mlr.resample.results = res
  )  
  if(!return.mlr.results) 
    results$mlr.resample.results <- NULL
  return(results)
}
