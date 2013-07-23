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
  cat('\nTask ID :: ', object@task.id,
      '\n\nTask Type :: ', object@task.type,'\n')
  if (length(object@task.pars)) {
    for(i in 1:length(object@task.pars))
      cat('\t', names(object@task.pars)[i], ' = ', object@task.pars[[i]], '\n')
  }
  
  ## Target variables info
  catf('\nTask Target Feature :: %s', collapse(object@task.target.features, "\t"))
  
  ## Data set info
  if (!is.null(object@task.data.desc)) {
    cat('\nDataset :: ', object@task.data.desc@name,
        ' (openML ID = ', object@task.data.desc@id,
        ', version = ', object@task.data.desc@version, ')\n')
    cat('\tData frame with ', nrow(object@task.data.desc@data.set),
        ' rows and ', ncol(object@task.data.desc@data.set),' columns\n')
  }
  
  ## Estimation procedure info
  if (!is.null(object@task.estimation.procedure)) {
    cat('\nEstimation Procedure :: ', object@task.estimation.procedure@type, '\n')
    cat('\tData splits for estimation ', 
        ifelse(all(dim(object@task.estimation.procedure@data.splits) == 0), 'not available', 'available'), '\n')
    if (length(object@task.estimation.procedure@parameters)) {
      
      cat('\tParameters of the estimation procedure:\n')
      
      for(i in 1:length(object@task.estimation.procedure@parameters))
        cat('\t\t', names(object@task.estimation.procedure@parameters)[i], ' = ', object@task.estimation.procedure@parameters[[i]], '\n')
    }
  }
  cat('\nPredictions ::\n')
  cat('\tFormat = ', object@task.preds$format, '\n')
  cat('\tColumns:\n')
  for(i in 1:length(object@task.preds$features))
    cat('\t\t', names(object@task.preds$features)[i], ' = ', object@task.preds$features[[i]], '\n')
  
  cat('\nEvaluation Measures ::\n')
  for(i in 1:length(object@task.evaluation.measures))
    cat('\t', object@task.evaluation.measures[i], '\n')
})

