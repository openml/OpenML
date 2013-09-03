#' Generate a list of OpenML run parameter settings for a given MLR learner.
#'
#' Generate a list of \code{\link{OpenMLRunParameter}s} for a given MLR learner.
#' 
#' @param learner [\code{\link[mlr]{Learner}}]\cr
#'   An MLR learner object.
#' @return A list of \code{\link{OpenMLRunParameter}s}.
#' @examples
#' library(mlr)
#' lrn <- makeLearner("classif.rpart", minsplit = 1)
#' bagging <- makeBaggingWrapper(lrn, bag.iters = 500)
#' 
#' lrn.par.settings <- makeRunParameterList(lrn)
#' lrn.par.settings
#' 
#' bagging.par.settings <- makeRunParameterList(bagged)
#' bagging.par.settings
#' @export
makeRunParameterList <- function(learner, component = character(0)) {
  par.vals <- learner$par.vals
  par.names <- names(learner$par.vals)
  par.settings <- list()    
  for(i in seq_along(par.vals)){
    run.par <- OpenMLRunParameter(
      name = par.names[i], 
      value = as.character(par.vals[[i]]),
      component = component)
    par.settings <- c(par.settings, run.par)
  }
  if(!is.null(learner$next.learner)) {
    # Use the learner's id (without "classif." or "regr.") as the subcomponent's name... 
    # FIXME: check if or make sure that this is correct
    component <- strsplit(learner$next.learner$id, split = ".", fixed=TRUE)[[1]][2]
    inner.par.settings <- makeRunParameterList(learner$next.learner, component = component)
    par.settings <- c(par.settings, inner.par.settings)
  }
  return(par.settings)
}