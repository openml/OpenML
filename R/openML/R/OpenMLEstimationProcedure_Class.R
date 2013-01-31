################################################################# 
# THIS FILE DEFINES CLASS OpenMLEstimationProcedure AND THE RESPECTIVE METHODS #
#################################################################
# Authors : L. Torgo, B. Bischl and P. Branco   Date: Jan 2013  #
# License: GPL (>= 2)                                           #
#################################################################


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
