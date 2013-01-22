#FIXME: check file io errorsr, dir writable and so on
downloadOpenMLTask <- function(id, dir = tempdir(), clean.up = TRUE, fetch.data.set.description = TRUE, fetch.data.set = TRUE, fetch.data.splits = TRUE) {
  id <- convertInteger(id)
  checkArg(id, "integer", len = 1L, na.ok = FALSE)
  checkArg(dir, "character", len = 1L, na.ok = FALSE)
  checkArg(clean.up, "logical", len = 1L, na.ok = FALSE)
  checkArg(fetch.data.set.description, "logical", len = 1L, na.ok = FALSE)
  checkArg(fetch.data.set, "logical", len = 1L, na.ok = FALSE)
  checkArg(fetch.data.splits, "logical", len = 1L, na.ok = FALSE)
  
  fn.task <- file.path(dir, "task.xml")
  fn.data.set.desc <- file.path(dir, "data_set_description.xml")
  fn.data.set <- file.path(dir, "data_set.ARFF")
  fn.data.splits <- file.path(dir, "data_splits.ARFF")
  
  messagef("Downloading task %i from OpenML repository.", id)
  messagef("Intermediate files (XML and ARFF) will be stored in : %s", dir)

  downloadFile(api.fun = "openml.tasks.search", file = fn.task, task.id = id)  
  task <- parseOpenMLTask(fn.task)
  
  if (fetch.data.set.description) {
    dsd <- parseOpenMLDataSetDescription(fn.data.set.desc)
    downLoadOpenMLDataSet(id, fn.data.set)
    task@task.data.splits <- parseOpenMLDataSplits(fn.data.splits)
  }
  
  if (fetch.data.set) {
    downLoadOpenMLDatasplits(id, fn.data.splits)
    task@task.data.set <- parseOpenMLDataSet(fn.data.set)
  }
  
  if (clean.up) {
    unlink(fn.task)
    unlink(fn.data.set.desc)
    unlink(fn.data.set)
    unlink(fn.data.splits)
    unlink(fn.task)
    messagef("All intermediate XML and ARFF files are now removed.")
  }
  return(task)
}

parseOpenMLTask <- function(file) {
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
  dsd <- NULL
  
  # prediction
  ns.preds.features <- getNodeSet(doc, "/oml:task/oml:output/oml:predictions/oml:feature")
  preds.features <- lapply(ns.preds.features, function(x) xmlGetAttr(x, "type"))
  names(preds.features) <- sapply(ns.preds.features, function(x) xmlGetAttr(x, "name"))
  task.preds <- list(
    name = xmlValue(getNodeSet(doc, "/oml:task/oml:output/oml:predictions/oml:name")[[1]]), 
    format = xmlValue(getNodeSet(doc, "/oml:task/oml:output/oml:predictions/oml:format")[[1]]),
    features = preds.features
  )
  
  data.splits <- data.frame() 
  
  task = OpenMLTask(
    task.id = task.id,
    task.type = task.type,
    task.pars = parameters,
    task.data.set = dsd,
    task.data.splits = data.splits,
    task.preds = task.preds
  )
  convertOpenMLTaskSlots(task)
}


convertOpenMLTaskSlots = function(task) {
  # parameters
  convpars <- function(name, fun) 
    if(!is.null(task@task.pars[[name]]))
      task@task.pars[[name]] <<- fun(task@task.pars[[name]])
  convpars("number_repeats", as.integer)
  convpars("number_folds", as.integer)
  convpars("evaluation_measure", function(x) strsplit(x, split=",")[[1]])
  
  return(task)
}



