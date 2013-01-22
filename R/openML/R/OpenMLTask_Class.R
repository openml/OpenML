################################################################# 
# THIS FILE DEFINES CLASS OpenMLTask AND THE RESPECTIVE METHODS #
#################################################################
# Authors : L. Torgo, B. Bischl and P. Branco   Date: Jan 2013  #
# License: GPL (>= 2)                                           #
#################################################################


# ==============================================================
# CLASS: OpenMLTask
# ==============================================================
# Luis Torgo, Bernd Bischl and Paula Branco, Jan 2013
# ==============================================================


# --------------------------------------------------------------
# class def
setClass("OpenMLTask",
         representation(task.id="numeric",
                        task.type="character",
                        task.pars="list",
                        task.data.set="OptionalOpenMLDataSetDescription",
                        task.data.splits="data.frame",
                        task.preds="list"
           ))


# --------------------------------------------------------------
# constructor function
OpenMLTask <- function(task.id,task.type,task.pars,
                       task.data.set,task.data.splits,
                       task.preds)
{
  new("OpenMLTask",
      task.id=task.id,task.type=task.type,task.pars=task.pars,
      task.data.set=task.data.set,task.data.splits=task.data.splits,
      task.preds=task.preds)
}



# --------------------------------------------------------------
# Methods:


# show
# Note: The data splits and the predictions are not shown
setMethod("show","OpenMLTask",
          function(object) {
            cat('\nTask ID :: ',object@task.id,
                '\n\nTask Type :: ',object@task.type,'\n')
            for(i in 1:length(object@task.pars))
              cat('\t',names(object@task.pars)[i],' = ',object@task.pars[[i]],'\n')
            if (!is.null(object@task.data.set)) {
              cat('\nDataset :: ',object@task.data.set@name,
                  ' (openML ID = ',object@task.data.set@id,
                  ', version = ',object@task.data.set@version,')\n')
              cat('\tData frame with ',nrow(object@task.data.set@data.set),
                  ' rows and ',ncol(object@task.data.set@data.set),' columns\n')
            }
            cat('\nPredictions ::\n')
            cat('\tFormat = ',object@task.preds$format,'\n')
            cat('\tColumns:\n')
            for(i in 1:length(object@task.preds$features))
              cat('\t\t',names(object@task.preds$features)[i],' = ',object@task.preds$features[[i]],'\n')
            cat('\nData splits for evaluation ::',ifelse(all(dim(object@task.data.splits) == 0),'Not Available','Available'),'\n')
          }
          )



# --------------------------------------------------------------
# Accessor functions

taskType <- function(obj) {
  if (!is(obj,"OpenMLTask")) stop(obj,' needs to be of class "OpenMLTask".\n')
  obj@task.type
}


expSettings <- function(obj) {
  if (!is(obj,"OpenMLTask")) stop(obj,' needs to be of class "OpenMLTask".\n')
  OpenMLExpSettings(obj@task.pars$evaluation_method,as.numeric(obj@task.pars$number_folds),as.numeric(obj@task.pars$number_repeats),obj@task.data.splits)
}

evaluationMeasures <- function(obj) {
  if (!is(obj,"OpenMLTask")) stop(obj,' needs to be of class "OpenMLTask".\n')
  obj@task.pars$evaluation_measure
}


targetFeature <- function(obj) {
  if (!is(obj,"OpenMLTask")) stop(obj,' needs to be of class "OpenMLTask".\n')
  obj@task.pars$target_feature
}
  
