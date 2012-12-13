library(devtools)
library(XML)
library(RCurl)
library(RWeka)

load_all("skel")

# x = getURL("http://expdb.cs.kuleuven.be/expdb/expdbsqljson.php?q=select%20name%20from%20algorithm")
# fromJSON(x)



#dsd = readDataSetDescription("/home/bischl/cos/OpenML/data_set_desc.xml")
#print(dsd)
#catf("")
#dsd = retrieveData(dsd)
#print(dsd)
#catf("")

task = readTaskFile("/home/bischl/cos/OpenML/refinedTask.xml")
print(task)

z = toMLR(task)

lrn = makeLearner("classif.rpart")
r = resample(lrn, z$mlr.task, z$mlr.rin)