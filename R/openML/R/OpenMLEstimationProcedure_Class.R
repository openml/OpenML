################################################################# 
# THIS FILE DEFINES CLASS OpenMLEstimationProcedure AND THE RESPECTIVE METHODS #
#################################################################
# Authors : L. Torgo, B. Bischl and P. Branco   Date: Jan 2013  #
# License: GPL (>= 2)                                           #
#################################################################



#' OpenMLEstimationProcedure
#'
#' This class of objects contains the information describing an openML estimation procedure.
#' 
#' Objects can be created by calls of the form \code{OpenMLEstimationProcedure(...)}.
#' The objects contain information on ... .
#'
#'@section Slots: 
#'  \describe{
#'    \item{\code{type}}{[\code{character}]\cr
#'    The type of the estimation procedure.}
#'    \item{\code{data.splits.url}}{[\code{character}]\cr
#'    The url of the data splits.}
#'    \item{\code{data.splits}}{[\code{data.frame}]\cr
#'    The data splits of the estimation procedure.}
#'    \item{\code{parameters}}{[\code{list}]\cr
#'    The parameters of the estimation procedure. E.g., the number of CV-folds, the number of repetitions, etc.}
#'  }
#'
#' @name OpenMLEstimationProcedure
#' @rdname OpenMLEstimationProcedure
#' @aliases OpenMLEstimationProcedure-class
#' @exportClass OpenMLEstimationProcedure


# ==============================================================
# CLASS: OpenMLEstimationProcedure
# ==============================================================
# Luis Torgo, Bernd Bischl and Paula Branco, Jan 2013
# ==============================================================


# --------------------------------------------------------------
# class def
setClass("OpenMLEstimationProcedure",
         representation(
                        type = "character",
                        data.splits.url = "character",
                        data.splits = "data.frame",
                        parameters = "list"
           ))



setClassUnion("OptionalOpenMLEstimationProcedure",
              c("OpenMLEstimationProcedure","NULL"))

# --------------------------------------------------------------
# constructor function
OpenMLEstimationProcedure <- function(type, data.splits.url, data.splits, parameters) {
  new("OpenMLEstimationProcedure",
    type = type,
    data.splits.url = data.splits.url,
    data.splits = data.splits,
    parameters = parameters
  )
}


# --------------------------------------------------------------
# Methods:


# show
# Note: The data splits and the predictions are not shown
setMethod("show", "OpenMLEstimationProcedure",
  function(object) {
    catf('\nEstimation Method :: %s',object@type)
    catf('Parameters         ::')
    for(i in 1:length(object@parameters))
      catf('\t%s = %s', names(object@parameters)[i], object@parameters[[i]])
    catf('Data Splits        :: ')
    str(object@data.splits)
  }
)
