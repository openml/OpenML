###########################################################
## RUN AN EXISTING WORKFLOW ON AN OPENML TASK
###########################################################
## L. Torgo, Jan 2013
###########################################################
## Example calls:
##
## r <- runOnTask("9999",
##                WFfunc='standardWF',
##                WFpars=list(learner='rpartXse',learner.pars=list(se=0)),
##                WFdeps=c('DMwR'))
##
## pred.sv <- function(m,t,...) attr(predict(m,t,...),'probabilities')
## r <- runOnTask("9999",
##                WFfunc="standardWF",
##                WFpars=list(learner='svm',
##                            learner.pars=list(cost=10,probability=T),
##                            predictor='pred.sv',
##                            predictor.pars=list(probability=T)
##                           ),
##                WFdeps=c('DMwR','e1071')
##               )
##
runOnTask <- function(taskID,
                      WFfunc,WFpars,
                      WFdeps,
                      saveWF.script=T)
{

  ## Get task info
  if (taskID == '9999') {
    load('/home/ltorgo/Research/Projects/OnGoing/Harvest/ResearchWork/OpenML/R/iris_oml_task.RData')
    ## the data.splits slot is basically a mess currently
    task@task.estimation.procedure@data.splits <- read.table('/home/ltorgo/Research/Projects/OnGoing/Harvest/ResearchWork/OpenML/R/openML/R/irisDS.txt')
    #task@task.estimation.procedure@data.splits[,2] <- as.integer(task@task.estimation.procedure@data.splits[,2])
  } else task <- downloadOpenMLTask(taskID)

  ## preparing to run exp with task info
  tgts <- targetFeatures(task)
  sd  <- sourceData(task)
  es  <- task@task.estimation.procedure
  ev  <- evaluationMeasures(task)
  
  ## constructing the predictive task(s) in DMwR format
  ## not working
  ## d <- sapply(tgts,function(tgt) dataset(as.formula(paste(tgt,'~ .')),sd))
  d <- dataset(as.formula(paste(tgts[1],'~.')),sd)

  ## constructing the experimental settings in DMwR format
  setts <- switch(es@type,
           "cross-validation"=cvSettings(ds=es@data.splits),
           "holdout"=hldSettings(ds=es@data.splits),
            cvSettings()
                  )
  
  ## Specific pars for DMwR-specific workflows
  if (WFfunc %in% c('standardWF','timeseriesWF'))
    WFpars <- c(list(.outPreds=T,evaluator=NULL),WFpars)

  ## Running the experiments on DMwR
  theWF <- learner(WFfunc,WFpars)
  res <- switch(es@type,
  "cross-validation"=crossValidation(theWF,d,setts,itsInfo=T),
  "holdout"=holdOut(theWF,d,setts,itsInfo=T),
   stop(paste('Unfortunately',es@type,' experiments are not yet supported...'))
                )

  ## moving the results back to a OpenML format
  runRes <- DMwR2runRes(2,10,res)

  ## creating an implementation object for the WF
  impl <- learner2implementation(theWF,WFdeps)

  
  ## generating the workflow/implementation script for DMwR WFs
  scriptFN <- ""
  if (saveWF.script & WFfunc %in% c('standardWF','timseriesWF')) {
    scriptFN <- paste(WFfunc,WFpars$learner,'R',sep='.')
    srcTxt <- genScriptText(WFfunc,WFpars,WFdeps)
    cat(srcTxt,file=scriptFN)
  }

  ## creating a run object
  run <- OpenMLRun(taskID,'known after uploading (to change)')   # parameters missing (will be messy)

  list(implementation=impl,scriptFileName=scriptFN,run=run,runResults=runRes)
}



## ======================================================
## Creates a data frame ready for upload in OpenML from the
## results object of a DMwR experiment
## ======================================================
## L. Torgo, Jan 2013
## ======================================================
##
DMwR2runRes <- function(nr,nf,res) {
  lst <- attr(res,'itsInfo')
  if (length(lst) != nr*nf)  lst <- lst[which(names(lst)=='preds')]
  
  if (nr*nf != length(lst)) stop('Something odd happened.\n\t n.fold*n.repeats is different from the list of results returned by DMwR.')

  run <- data.frame()
  for(r in 1:nr)
    for(f in 1:nf) {
      classProbs <- lst[[(r-1)*nf+f]]
      classProbs <- cbind("repeat"=rep(r,nrow(classProbs)),
                          fold=rep(f,nrow(classProbs)),
                          row_id=rownames(classProbs),
                          classProbs)
      run <- rbind(run,classProbs)
    }
  run
}



## ======================================================
## Create an OpenMLImplementation object from a DMwR learner
## object. It tries hard to fill in as much information as
## possible
## ======================================================
## L. Torgo, Jan 2013
## ======================================================
##
learner2implementation <- function(wf,pcks.deps='') {
  i <- OpenMLImplementation(name=wf@func,description='')
  if (wf@func %in% c('standardWF','timeseriesWF')) {
    fn <- paste(paste(wf@func,wf@pars$learner,sep='_'),'R',sep='.')
    i@description <- paste('The DMwR pre-defined workflow',wf@func,
                           'using',wf@pars$learner,'as the learner',
                           ifelse(wf@pars$predictor,paste(',',wf@pars$predictor,'as the predictor function')),
                           ifelse(wf@pars$evaluator,paste(',',wg@pars$evaluator,'as the evaluator function.')))
    i@dependencies <- paste(pcks.deps,collapse=',')
  }
  if (wf@func == 'standardWF') {
    
  } else if (wf@func == 'timeseriesWF') {
    
  } else {

  }
  i
}


## ======================================================
## This function will be called to generate the script files
## This should only be used for DMwR-specific standard workflows
## If the user is using his own workflow he should write the
## script file, that should have the same name as the user-defined
## workflow function and it should contain two statments assigning
## the adequate values to variables ".WFfunc" and ".WFpars"
## Example:
## - User writes a workflow function named "mySpecStrat"
## - He should write a R script file with name "mySpecStrat.R"
## - This file should contain all necessary code and library calls
##   to run the workflow.
## - Moreover, the file should have these two assignements:
## .WFfunc <- "mySpecStrat"
## .WFpars <- list(mypar1='defaultValue1', <other parameters>)
##
## This means that if I want to use the workflow of the user in
## a different problem (for instance I just downloaded the WF from
## OpenML and I want to apply it to my data set), I just need to do:
##
## source('mySpecStrat.R')
## do.call(.WFfunc,c(list(formulaOfMyProbl,TrainOfMyProbl,TestOfMyProbl),
##                   .WFpars))
##
## ======================================================
## L. Torgo, Jan 2013
## ======================================================
##
genScriptText <- function(fun,pars,deps) {
  t <- ''
  ## library calls
  for(d in deps) t <- c(t,paste('library(',d,')',sep=''),'\n')

  ## information on the workflow
  t <- c(t,paste('.WFfunc <- "',fun,'"\n',sep=''))
  t <- c(t,'.WFpars <- ',deparse(pars),'\n')
  t
}
  




## ======================================================
## Given and OpenML implementation ID, this runs it on a
## predictive task the user has.
## A predictive task is define by a formula, a training
## data frame and a test data frame.
## The result will be the predictions of the implementation
## for the test set.
## ======================================================
## L. Torgo, Jan 2013
## ======================================================
##
useImplementation <- function(implID,form,train,test,...) {

  ## Load the implementation and associated script from OpenML
  ## Note : this is not ready yet
  ## It should return a list with the implementation object
  ## and the filename of the R script
  ## impl <- downnloadOpenMLImplementation(implID)
  
  ## For now, for testing purposes lets use an existing local script
  impl <- list(scriptFN='standardWF.rpartXse.R')
  
  ## Assuming the R script follows "my rules" for correct upload
  ## there will be a variable ".WFfunc" and another ".WFpars"
  source(impl$scriptFN)
  if (!(exists(".WFfunc") & exists(".WFpars")))
    stop(paste("Both '.WFfunc' and '.WFpars' are required to exist in script file",impl$scriptFN))

  ## The worflow DMwR object
  wf <- learner(.WFfunc,c(.WFpars,...))

  ## Now apply it to the user problem and return the results
  res <- runLearner(wf,form,train,test)

  ## some post-processing necessary for DMwR-based workflows
  if (.WFfunc %in% c('standardWF','timeseriesWF'))
    res <- attr(res,'itInfo')$preds

  res
}
