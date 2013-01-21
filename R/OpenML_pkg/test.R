library(devtools)
library(XML)
library(RCurl)
library(RWeka)
library(mlr)

load_all("skel")

# x = getURL("http://expdb.cs.kuleuven.be/expdb/expdbsqljson.php?q=select%20name%20from%20algorithm")
# fromJSON(x)



#fn = "my_r_task.xml"
fn.task = "../../XML/Examples/task.xml"
fn.dsd = "../../XML/Examples/dataset.xml"
#fn2 = "../../data_set_description.xml"
#fn.task = "mytask.xml"
#fn.dsd = "mydsd.xml"
#downloadOpenMLTask(id = 1, file = fn.task)
task = parseOpenMLTask(fn.task)
dsd = parseOpenMLDataSetDescription(fn.dsd)
# print(task)
# downloadOpenMLDataSetDescription(id = task$data.set.id, file = fn.dsd)
# dsd = readOpenMLDataSetDescription(fn.dsd)
# print(dsd)
# dsd = retrieveData(dsd)
# print(dsd)
# toMlr()
#print(task)

# mlr.task = makeClassifTask(data=iris, target="Species")
# mlr.lrn = makeLearner("classif.rpart", predict.type="prob")
# mlr.mod = train(mlr.lrn, mlr.task)
# mlr.pred = predict(mlr.mod, mlr.task)
# df = as.data.frame(mlr.pred)
# probs = getProbabilities(mlr.pred)
# writeResultFileClassification(file = "res.arff", task, list(), df$response, as.matrix(probs))
#z = toMLR(task)

#lrn = makeLearner("classif.rpart")
#r = resample(lrn, z$mlr.task, z$mlr.rin)