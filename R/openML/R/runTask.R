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
#' @param learner [\code{character(1)}]\cr 
#'   Class of learner to solve the task. By convention, all classification learners start with 
#'   \dQuote{classif.}. A list of all learners is available on the \code{\link[mlr]{learners}} 
#'   help page.
#' @param upload [\code{logical(1)}]\cr
#'   Should the run be uploaded automatically? In this case you have to provide a valid 
#'   session token. See \code{\link{authenticateUser}}. Default is \code{FALSE}.
#' @param session.token [\code{character(1)}]\cr
#'   A vaild session token returned by \code{\link{authenticateUser}}. Must be provided if 
#'   \code{upload == TRUE}.
#' @param featch.all [\code{logical(1)}]\cr 
#'   Should not only the predictions but all of the computed information be returned? This includes
#'   test measures in each step of the resampling procedure as well as the aggregated result. See 
#'   \code{\link[mlr]{resample}}. Default is \code{FALSE}.
#' @return If \code{fetch.all == FALSE}: \code{\link[mlr]{ResamplePrediction}} . 
#'   Else: a named \code{list} returned by \code{\link[mlr]{resample}}.
#' @seealso \code{\linkS4class{OpenMLTask}}, \code{\link[mlr]{learners}}, 
#'   \code{\link{authenticateUser}}, \code{\link[mlr]{resample}}
#' @export
runTask <- function(task.id, task, learner, upload = FALSE, session.token, fetch.all = FALSE) {
  if(missing(task)) {
    if(missing(task.id)) {
      stopf("Either 'task.id' or 'task' must be specified.")
    }
    task <- downloadOpenMLTask(task.id)
  }
  mlrTask <- toMLR(task)
  lrn <- makeLearner(learner)
  res <- resample(lrn, mlrTask$mlr.task, mlrTask$mlr.rin, measures = mlrTask$mlr.measures)
  
  if(upload) {
    print("Automatic upload is not available yet.")
  }
  
  if(fetch.all) return(res)
  else return(res$pred$data)
}
