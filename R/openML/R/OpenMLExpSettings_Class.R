################################################################# 
# THIS FILE DEFINES CLASS OpenMLExpSetts AND THE RESPECTIVE METHODS #
#################################################################
# Authors : L. Torgo, B. Bischl and P. Branco   Date: Jan 2013  #
# License: GPL (>= 2)                                           #
#################################################################

#' OpenMLExpSettings
#'
#' This class of objects contains the information on an experimental method.
#' 
#' Objects can be created by calls of the form \code{OpenMLExpSettings(...)}.
#' The objects contain information on ... .
#'
#' @section Slots: 
#'  \describe{
#'    \item{\code{type}}{[\code{character}]\cr
#'    The type of the evaluation method.}
#'    \item{\code{n.folds}}{[\code{integer(1)}]\cr
#'    The number of folds of the evaluation procedure (for most methods this is
#'    1, but for cross validation this will typically be a larger number).}
#'    \item{\code{n.repeats}}{[\code{integer(1)}]\cr
#'    The number of times the experimental procedure is to be repeated with different randomization.}
#'    \item{\code{data.splits}}{[\code{data.frame}]\cr
#'    The actual data splits to be used on each fold and each repetition. This is a data frame with columns: 
#'    \code{type} with possible values \code{TRAIN} or \code{TEST}; \code{rowid}; \code{fold}; and \code{repeat}.} 
#' }
#' @seealso \code{\linkS4class{OpenMLTask}}
#' 
#' @name OpenMLExpSettings
#' @rdname OpenMLExpSettings
#' @aliases OpenMLExpSettings-class
#' @exportClass OpenMLExpSettings

# ==============================================================
# CLASS: OpenMLExpSetts
# ==============================================================
# Luis Torgo, Bernd Bischl and Paula Branco, Jan 2013
# ==============================================================


# --------------------------------------------------------------
# class def
setClass("OpenMLExpSettings",
         representation(
                        type="character",
                        n.folds="numeric",
                        n.repeats="numeric",
                        data.splits="data.frame"
           ))


# --------------------------------------------------------------
# constructor function
OpenMLExpSettings <- function(type,n.folds,n.repeats,data.splits)
{
  new("OpenMLExpSettings",
      type=type,
      n.folds=n.folds,n.repeats=n.repeats,
      data.splits=data.splits)
}


# --------------------------------------------------------------
# Methods:


# show
# Note: The data splits and the predictions are not shown
setMethod("show","OpenMLExpSettings",
  function(object) {
    cat('\nEvaluation Method :: ', object@type, '\n')
    cat('Nr. of Folds      :: ', object@n.folds, '\n')
    cat('Nr. of Repeats    :: ', object@n.repeats, '\n')
    cat('Data Splits       :: \n')
    str(object@data.splits)
  }
)
