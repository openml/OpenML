# FIXME @method show \code{signature(object = "OpenMLTask")}: method used to show the contents of a OpenMLTask object. 

#' OpenMLTask
#'
#' This class of objects contains the information describing an openML task.
#' 
#' Objects can be created by calls of the form \code{OpenMLTask(...)}.
#' The objects contain information on ... .
#'
#'@section Slots: 
#'  \describe{
#'    \item{\code{task.id}}{[\code{integer(1)}]\cr
#'    The task's OpenML ID.}
#'    \item{\code{task.type}}{[\code{character}]\cr 
#'    The task's type.}
#'    \item{\code{task.pars}}{[\code{list}]\cr 
#'    A set of parameters specific to the task type.}
#'    \item{\code{task.target.features}}{[\code{character}]\cr 
#'    The name(s) of the target feature(s)}
#'    \item{\code{task.data.desc.id}}{[\code{integer(1)}]\cr 
#'    The OpenML ID of the data set associated with the task.}
#'    \item{\code{task.data.desc}}{[\code{\link{OptionalOpenMLDataSetDescription}}]\cr 
#'    Information on the data set.}
#'    \item{\code{task.estimation.procedure}}{[\code{\link{OptionalOpenMLEstimationProcedure}}]\cr 
#'    Information on the task's estimation method and the asoociated data splits.}
#'    \item{\code{task.preds}}{[\code{list}]\cr 
#'    A list that contains information on the format of the predictions for the particular task.}
#'    
#'  }
#'
#' @seealso \code{\linkS4class{OpenMLDataSetDescription}}
#' @examples
#' showClass("OpenMLTask")
#' @name OpenMLTask
#' @rdname OpenMLTask
#' @aliases OpenMLTask-class
#' @exportClass OpenMLTask

setClass("OpenMLTask", representation(
  task.id = "integer",
  task.type = "character",
  task.pars = "list",
  task.target.features = "character",
  task.data.desc.id = "integer",
  task.data.desc = "OptionalOpenMLDataSetDescription",
  task.estimation.procedure = "OptionalOpenMLEstimationProcedure",
  task.preds = "list",
  task.evaluation.measures = "character"
))


# ***** Constructor *****
OpenMLTask <- function(task.id, task.type, task.pars, task.target.features,
                       task.data.desc.id, task.data.desc,
                       task.estimation.procedure,
                       task.preds, task.evaluation.measures) {
  new("OpenMLTask",
      task.id = task.id, task.type = task.type, task.pars = task.pars,
      task.target.features = task.target.features,
      task.data.desc.id = task.data.desc.id, task.data.desc = task.data.desc,
      task.estimation.procedure = task.estimation.procedure,
      task.preds = task.preds, task.evaluation.measures = task.evaluation.measures
  )
}


# ***** Methods *****

# show
# Note: The data splits and the predictions are not shown
setMethod("show", "OpenMLTask", function(object) {
  ## Task general info
  catf('\nTask ID ::  %i \n\nTask Type ::  %s', object@task.id, object@task.type)
  if (length(object@task.pars)) 
    cat(collapse(paste("\t", names(object@task.pars), " = ", object@task.pars), sep = "\n"))
  
  ## Target variables info
  catf('\nTask Target Feature :: %s', collapse(object@task.target.features, "\t"))
  
  ## Data set info
  if (!is.null(object@task.data.desc)) {
    catf('\nDataset ::  %s  (openML ID =  %i, version = %s)', 
         object@task.data.desc@name, object@task.data.desc@id, object@task.data.desc@version)
    catf('\tData frame with %i rows and %i columns', 
         nrow(object@task.data.desc@data.set), ncol(object@task.data.desc@data.set))
  }
  
  ## Estimation procedure info
  if (!is.null(object@task.estimation.procedure)) {
    catf('\nEstimation Procedure :: %s', object@task.estimation.procedure@type)
    catf('\tData splits for estimation %s.',
         ifelse(all(dim(object@task.estimation.procedure@data.splits) == 0), 'not available', 'available'))
    if (length(object@task.estimation.procedure@parameters)) {
      
      cat('\tParameters of the estimation procedure:\n')
      
      cat(collapse(paste("\t", names(object@task.estimation.procedure@parameters), " = ", 
                         object@task.estimation.procedure@parameters), sep = "\n"))
    }
  }
  cat('\nPredictions ::\n')
  catf('\tFormat = %s', object@task.preds$format)
  cat('\tColumns:\n')
  cat(collapse(paste("\t\t", names(object@task.preds$features), " = ", object@task.preds$features), sep = "\n"))
  cat('\nEvaluation Measures ::\n')
  catf('%s', collapse(object@task.evaluation.measures, '\n'))
})

