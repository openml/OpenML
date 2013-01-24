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
    downloadOpenMLDataSetDescription(task@data.desc.id, fn.data.set.desc)
    task@data.desc <- parseOpenMLDataSetDescription(fn.data.set.desc)
  }

  if (fetch.data.set) {
    downloadOpenMLDataSet(task@task.data.desc@id, fn.data.set)
    task@task.data.desc@data.set <- parseOpenMLDataSet(fn.data.set)
  }
  
  if (fetch.data.splits) {
    downloadOpenMLDataSplits(task@data.splits.id, fn.data.splits)
    task@task.data.set <- parseOpenMLDataSet(fn.data.splits)
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
  getParams <- function(path) {
    ns.parameters <- getNodeSet(doc, paste(path, "oml:parameter", sep ="/"))
    parameters <- lapply(ns.parameters, function(x) xmlValue(x))
    names(parameters) <- sapply(ns.parameters, function(x) xmlGetAttr(x, "name"))
    parameters
  }
  params <- getParams("oml:task")
  
  # data set description
  data.desc.id <- as.integer(xmlValue(getNodeSet(doc, "/oml:task/oml:input/oml:data_set/oml:data_set_id")[[1]]))
  data.splits.id <- as.integer(xmlValue(getNodeSet(doc, "/oml:task/oml:input/oml:estimation_procedure/oml:data_splits_id")[[1]]))
  data.desc <- NULL
  
  # prediction
  ns.preds.features <- getNodeSet(doc, "/oml:task/oml:output/oml:predictions/oml:feature")
  preds.features <- lapply(ns.preds.features, function(x) xmlGetAttr(x, "type"))
  names(preds.features) <- sapply(ns.preds.features, function(x) xmlGetAttr(x, "name"))
  task.preds <- list(
    name = xmlValue(getNodeSet(doc, "/oml:task/oml:output/oml:predictions/oml:name")[[1]]), 
    format = xmlValue(getNodeSet(doc, "/oml:task/oml:output/oml:predictions/oml:format")[[1]]),
    features = preds.features
  )
  
  # estimation procedure
  estim.proc <- OpenMLEstimationProcedure(
    type = xmlValue(getNodeSet(doc, "/oml:task/oml:input/oml:estimation_procedure/oml:type")[[1]]), 
    data.splits.id  = as.integer(xmlValue(getNodeSet(doc, "/oml:task/oml:input/oml:estimation_procedure/oml:data_splits_id")[[1]])), 
    data.splits = data.frame(),
    parameters = getParams("/oml:task/oml:input/oml:estimation_procedure/oml:type")
  )

  # measures
  ns.eval <- getNodeSet(doc, "/oml:task/oml:input/oml:evaluation_measures/oml:evaluation_measure")
  measures <- sapply(ns.eval, xmlValue)
  
  task = OpenMLTask(
    task.id = task.id,
    task.type = task.type,
    task.pars = params,
    task.data.desc.id = data.desc.id,
    task.data.desc = data.desc,
    task.estimation.procedure = estim.proc,
    task.preds = task.preds,
    task.evaluation.measures = measures
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



#FIXME: respect row id attribute