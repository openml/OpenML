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
         representation(task.id="integer",
                        task.type="character",
                        task.pars="list",
                        task.data.desc.id="integer",
                        task.data.desc="OptionalOpenMLDataSetDescription",
                        task.data.splits.id="integer",
                        task.data.splits="OptionalOpenMLDataSplits",
                        task.preds="list",
                        task.evaluation.measures="character"
           ))


# --------------------------------------------------------------
# constructor function
OpenMLTask <- function(task.id,task.type,task.pars,
                       task.data.desc.id,task.data.desc,
                       task.data.splits.id,task.data.splits,
                       task.preds,task.evaluation.measures)
{
  new("OpenMLTask",
      task.id=task.id,task.type=task.type,task.pars=task.pars,
      task.data.desc.id=task.data.desc.id,task.data.desc=task.data.desc,
      task.data.splits.id=task.data.splits.id,task.data.splits=task.data.splits,
      task.preds=task.preds,task.evaluation.measures=task.evaluation.measures)
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
            cat('\nData splits for evaluation ::',ifelse(is.null(object@task.data.splits),'Not Available','Available'),'\n')
            cat('\nEvaluation Measures ::\n')
            for(i in 1:length(object@task.evaluation.measures))
              cat('\t',object@task.evaluation.measures[i],'\n')
          }
          )



# --------------------------------------------------------------
# Accessor functions

taskType <- function(task) {
  checkArg(task, "OpenMLTask")
  task@task.type
}


dataSplits <- function(task) {
  checkArg(task, "OpenMLTask")
  task@task.data.split
}

evaluationMeasures <- function(task) {
  checkArg(task, "OpenMLTask")
  task@task.evaluation.measures
}


targetFeature <- function(task) {
  checkArg(task, "OpenMLTask")
  task@task.pars$target_feature
}
  
sourceData <- function(task) {
  checkArg(task, "OpenMLTask")
  task@task.data.desc@data.set
}
  
