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
#' @param learner[\code{\link[mlr]{Learner}}]\cr 
#'   Learner object from package mlr to solve the task. 
#'   A list of all learners is available on the \code{\link[mlr]{learners}} help page.
#' @param fetch.all [\code{logical(1)}]\cr 
#'   Should not only the predictions but all of the computed information be returned? This includes
#'   test measures in each step of the resampling procedure as well as the aggregated result. See 
#'   \code{\link[mlr]{resample}}. Default is \code{FALSE}.
#' @return If \code{fetch.all == FALSE}: \code{\link[mlr]{ResamplePrediction}} . 
#'   Else: a named \code{list} returned by \code{\link[mlr]{resample}}.
#' @seealso \code{\linkS4class{OpenMLTask}}, \code{\link[mlr]{learners}}, 
#'   \code{\link{authenticateUser}}, \code{\link[mlr]{resample}}
#' @export
runTask <- function(task, learner, fetch.all = FALSE) {
  checkArg(task, "OpenMLTask")
  checkArg(learner, "Learner")
  #FIXME check that learner type corresponds to task
  mlr.task <- toMLR(task)
  res <- resample(learner, mlr.task$mlr.task, mlr.task$mlr.rin, measures = mlr.task$mlr.measures)
  list(
    openml.run.result = res$pred$data, 
    mlr.resample.pred = res
  )  
}
