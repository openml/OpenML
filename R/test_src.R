library(devtools)
library(XML)
library(RCurl)

load_all("openML")


fn.task = "../XML/Examples/task.xml"
task = parseOpenMLTask(fn.task)

#fn = "my_r_task.xml"
#fn.dsd = "../../XML/Examples/dataset.xml"
#fn2 = "../../data_set_description.xml"
#fn.task = "mytask.xml"
#fn.dsd = "mydsd.xml"
#downloadOpenMLTask(id = 1, file = fn.task)

#dsd = parseOpenMLDataSetDescription(fn.dsd)
