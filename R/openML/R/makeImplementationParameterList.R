#' Generate a list of OpenML implementation parameters for a given MLR learner.
#'
#' Generate a list of \code{\link{OpenMLImplementationParameter}s} for a given MLR learner.
#' 
#' @param learner [\code{\link[mlr]{Learner}}]\cr
#'   An MLR learner object.
#' @return A list of \code{\link{OpenMLImplementationParameter}s}.
#' @examples
#' library(mlr)
#' lrn <- makeLearner("classif.randomForest")
#' pars <- makeImplementationParameterList(lrn)
#' pars
#' @export
makeImplementationParameterList <- function(learner) {
  pars <- learner$par.set$pars
  par.list <- list()
  for(i in seq_along(pars)){
    name <- pars[[i]]$id
    data.type <- pars[[i]]$type
    # FIXME: data.type Should be either integer, numeric, string, vector, matrix, object.
    # if(data.type == "discrete") data.type <- "string"      ? 
    # if(data.type == "numericvector") data.type <- "vector" ? 
    # ...
    if(pars[[i]]$has.default)
      default.value <- as.character(pars[[i]]$default)
    else
      default.value <- character(0)
    impl.par <- OpenMLImplementationParameter(
      name = name, 
      data.type = data.type, 
      default.value = default.value)
    par.list <- c(par.list, impl.par)
  }
  return(par.list)
}