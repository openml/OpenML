library(devtools)
library(XML)
library(RCurl)
library(BBmisc)
library(RWeka)

load_all("openML")

options(warn = 2)

fn.task <- "../XML/Examples/task.xml"
fn.data.set.desc <- "../XML/Examples/dataset.xml"    
fn.data.set <- "../ARFF/iris.arff"    
fn.data.splits <- "../ARFF/folds_task_1.arff"    

#dsd <- parseOpenMLDataSetDescription(fn.data.set.desc)
#print(dsd)

task <- parseOpenMLTask(fn.task)
task@task.data.desc <- parseOpenMLDataSetDescription(fn.data.set.desc)
task@task.data.desc@data.set <- parseOpenMLDataSet(task@task.data.desc, fn.data.set)
task@task.estimation.procedure@data.splits <- parseOpenMLDataSplits(task@task.data.desc@data.set, fn.data.splits)
#print(task)

print(summary(task@task.estimation.procedure@data.splits))
print(head(task@task.estimation.procedure@data.splits))

# task = downloadOpenMLTask(id = 12, fetch.data.set.description = TRUE,
#   fetch.data.set = FALSE, fetch.data.splits = FALSE)
# print(task)

  z <- toMLR(task)
  lrn <- makeLearner("classif.rpart")
  r <- resample(lrn, z$mlr.task, z$mlr.rin, measures = z$mlr.measures)
