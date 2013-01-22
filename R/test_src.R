library(devtools)
library(XML)
library(RCurl)
library(BBmisc)
library(RWeka)

load_all("openML")


fn.task <- "../XML/Examples/task.xml"
fn.data.set.desc <- "../XML/Examples/dataset.xml"    
fn.data.set <- "../ARFF/iris.arff"    
fn.data.splits <- "../ARFF/foldconfig_task_1.arff"    


task <- parseOpenMLTask(fn.task)
task@task.data.desc = parseOpenMLDataSetDescription(fn.data.set.desc)
task@task.data.desc@data.set = parseOpenMLDataSet(fn.data.set)
task@task.data.splits = parseOpenMLDataSplits(fn.data.splits)
print(task)


# task = downloadOpenMLTask(id = 12, fetch.data.set.description = TRUE,
#   fetch.data.set = FALSE, fetch.data.splits = FALSE)
# print(task)

z <- toMLR(task)
lrn <- makeLearner("classif.rpart")
r <- resample(lrn, z$mlr.task, z$mlr.rin)
