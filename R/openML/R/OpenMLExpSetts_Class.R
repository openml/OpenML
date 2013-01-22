################################################################# 
# THIS FILE DEFINES CLASS OpenMLExpSetts AND THE RESPECTIVE METHODS #
#################################################################
# Authors : L. Torgo, B. Bischl and P. Branco   Date: Jan 2013  #
# License: GPL (>= 2)                                           #
#################################################################


# ==============================================================
# CLASS: OpenMLExpSetts
# ==============================================================
# Luis Torgo, Bernd Bischl and Paula Branco, Jan 2013
# ==============================================================


# --------------------------------------------------------------
# class def
setClass("OpenMLExpSetts",
         representation(
                        type="character",
                        n.folds="numeric",
                        n.repeats="numeric",
                        data.splits="data.frame"
           ))


# --------------------------------------------------------------
# constructor function
OpenMLExpSetts <- function(type,n.folds,n.repeats,data.splits)
{
  new("OpenMLExpSetts",
      type=type,
      n.folds=n.folds,n.repeats=n.repeats,
      data.splits=data.splits)
}


# --------------------------------------------------------------
# Methods:


# show
# Note: The data splits and the predictions are not shown
setMethod("show","OpenMLExpSetts",
          function(object) {
            cat('\nEvaluation Method :: ',object@type,'\n')
            cat('Nr. of Folds      :: ',object@n.folds,'\n')
            cat('Nr. of Repeats    :: ',object@n.repeats,'\n')
            cat('Data Splits       :: \n')
            str(object@data.splits)
          }
          )
