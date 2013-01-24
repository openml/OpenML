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
                        type="character",
                        data.splits.id="integer",
                        data.splits="data.frame",
                        parameters="list"
           ))



setClassUnion("OptionalOpenMLEstimationProcedure",
              c("OpenMLEstimationProcedure","NULL"))

# --------------------------------------------------------------
# constructor function
OpenMLEstimationProcedure <- function(type,data.splits.id,data.splits,parameters)
{
  new("OpenMLEstimationProcedure",
      type=type,
      data.splits.id=data.splits.id,
      data.splits=data.splits,
      parameters=parameters)
}


# --------------------------------------------------------------
# Methods:


# show
# Note: The data splits and the predictions are not shown
setMethod("show","OpenMLEstimationProcedure",
          function(object) {
            cat('\nEstimation Method :: ',object@type,'\n')
            cat('Parameters         ::\n')
            for(i in 1:length(object@parameters))
              cat('\t',names(object@parameters)[i],' = ',object@parameters[[i]],'\n')
            cat('Data Splits        :: \n')
            str(object@data.splits)
          }
          )
