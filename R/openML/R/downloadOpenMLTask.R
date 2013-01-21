downloadOpenMLTask <- function(id, file) {
  id <- convertInteger(id)
  checkArg(id, "integer", len = 1L, na.ok = FALSE)
  checkArg(file, "character", len = 1L, na.ok = FALSE)
  downloadFile(api.fun = "openml.tasks.search", file = file, task.id = id)  
}

parseOpenMLTask <- function(file) {
  checkArg(file, "character", len = 1L, na.ok = FALSE)
  doc <- xmlParse(file)
  
  # task
  task.id <- as.integer(xmlValue(getNodeSet(doc, "/oml:task/oml:task_id")[[1]]))
  task.type <- xmlValue(getNodeSet(doc, "/oml:task/oml:task_type")[[1]])  
  ns.parameters <- getNodeSet(doc, "/oml:task/oml:parameter")
  parameters <- lapply(ns.parameters, function(x) xmlValue(x))
  names(parameters) <- sapply(ns.parameters, function(x) xmlGetAttr(x, "name"))

  # data set description
  data.set.id <- as.integer(xmlValue(getNodeSet(doc, "/oml:task/oml:input/oml:data_set/oml:data_set_id")[[1]]))
  data.set.format <- xmlValue(getNodeSet(doc, "/oml:task/oml:input/oml:data_set/oml:data_format")[[1]])
  data.splits.id <- as.integer(xmlValue(getNodeSet(doc, "/oml:task/oml:input/oml:data_splits/oml:data_set_id")[[1]]))
  data.splits.format <- xmlValue(getNodeSet(doc, "/oml:task/oml:input/oml:data_splits/oml:data_format")[[1]])
  # FIXME
  dsd.file <- "../XML/Examples/dataset.xml"
  dsd <- parseOpenMLDataSetDescription(dsd.file)
  
  # prediction
  ns.preds.features <- getNodeSet(doc, "/oml:task/oml:output/oml:predictions/oml:feature")
  preds.features <- lapply(ns.preds.features, function(x) xmlGetAttr(x, "type"))
  names(preds.features) <- sapply(ns.preds.features, function(x) xmlGetAttr(x, "name"))
  task.preds <- list(
    name = xmlValue(getNodeSet(doc, "/oml:task/oml:output/oml:predictions/oml:name")[[1]]), 
    format = xmlValue(getNodeSet(doc, "/oml:task/oml:output/oml:predictions/oml:format")[[1]]),
    features = preds.features
  )
  
  OpenMLTask(
    task.id = task.id,
    task.type = task.type,
    task.pars = parameters,
    task.data.set = dsd,
    task.data.splits = data.frame(),
    task.preds = task.preds
  )
}

# parseOpenMLTask <- function(file) {
#   checkArg(file, "character", len = 1L, na.ok = FALSE)
#   doc <- xmlParse(file)
#   task.id <- as.integer(xmlValue(getNodeSet(doc, "/oml:task/oml:task_id")[[1]]))
#   task.type <- xmlValue(getNodeSet(doc, "/oml:task/oml:task_type")[[1]])
#   ns.parameters <- getNodeSet(doc, "/oml:task/oml:parameter")
#   parameters <- lapply(ns.parameters, function(x) xmlValue(x))
#   names(parameters) <- sapply(ns.parameters, function(x) xmlGetAttr(x, "name"))
#   print(parameters)
#   stop()
#   data.set.id <- as.integer(xmlValue(getNodeSet(doc, "/oml:task/oml:input/oml:data_set/oml:data_set_id")[[1]]))
#   data.set.format <- xmlValue(getNodeSet(doc, "/oml:task/oml:input/oml:data_set/oml:data_format")[[1]])
#   data.splits.id <- as.integer(xmlValue(getNodeSet(doc, "/oml:task/oml:input/oml:data_splits/oml:data_set_id")[[1]]))
#   data.splits.format <- xmlValue(getNodeSet(doc, "/oml:task/oml:input/oml:data_splits/oml:data_format")[[1]])
#   
#   output.predictions.name <- xmlValue(getNodeSet(doc, "/oml:task/oml:output/oml:predictions/oml:name")[[1]])
#   output.predictions.format <- xmlValue(getNodeSet(doc, "/oml:task/oml:output/oml:predictions/oml:format")[[1]])
#   ns.features <- getNodeSet(doc, "/oml:task/oml:output/oml:predictions/oml:feature")
#   getNodeAttr <- function(nodes, name, attr) xmlGetAttr(Filter(function(x) xmlGetAttr(x, "name") == name, nodes)[[1]], attr)
#   output.predictions.repeat.required <- getNodeAttr(ns.features, "repeat", "required")
#   output.predictions.fold.required <- getNodeAttr(ns.features, "fold", "required")
#   output.predictions.rowid.required <- getNodeAttr(ns.features, "row_id", "required")
#   
#   OpenMLTask(
#     task.id = task.id,
#     task.type = task.type,
#     data.set.id = data.set.id, 
#     prediction.type = prediction.type,
#     target.feature = target.feature,
#     evaluation.measures = evaluation.measures,
#     evaluation.method = evaluation.method,
#     evaluation.method.args = evaluation.method.args, 
#     repeats = repeats
#   )
# }


