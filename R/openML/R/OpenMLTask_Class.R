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
                        task.data.desc.id="numeric",
                        task.data.desc="OptionalOpenMLDataSetDescription",
                        task.data.splits.id="numeric",
                        task.data.splits="data.frame",
                        task.preds="list"
           ))


# --------------------------------------------------------------
# constructor function
OpenMLTask <- function(task.id,task.type,task.pars,
                       task.data.desc.id,task.data.desc,
                       task.data.splits.id,task.data.splits,
                       task.preds)
{
  new("OpenMLTask",
      task.id=task.id,task.type=task.type,task.pars=task.pars,
      task.data.desc.id=task.data.desc.id,task.data.desc=task.data.desc,
      task.data.splits.id=task.data.splits.id,task.data.splits=task.data.splits,
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
            if (!is.null(object@task.data.desc)) {
              cat('\nDataset :: ',object@task.data.desc@name,
                  ' (openML ID = ',object@task.data.desc@id,
                  ', version = ',object@task.data.desc@version,')\n')
              cat('\tData frame with ',nrow(object@task.data.desc@data.set),
                  ' rows and ',ncol(object@task.data.desc@data.set),' columns\n')
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

taskType <- function(task) {
  checkArg(task, "OpenMLTask")
  task@task.type
}


expSettings <- function(obj) {
  checkArg(task, "OpenMLTask")
  OpenMLExpSettings(task@task.pars$evaluation_method,as.numeric(task@task.pars$number_folds),as.numeric(task@task.pars$number_repeats),task@task.data.splits)
}

evaluationMeasures <- function(obj) {
  checkArg(task, "OpenMLTask")
  task@task.pars$evaluation_measure
}


targetFeature <- function(obj) {
  checkArg(task, "OpenMLTask")
  task@task.pars$target_feature
}
  
sourceData <- function(obj) {
  checkArg(task, "OpenMLTask")
  task@task.data.desc@data.set
}
  
