#' Download a task from the OpenML repository.
#'
#' This function downloads an OpenML task and all associated files from the OpenML repository,
#' intermediately stores the files on disk and creates an S4 object which completely specifies the task.
#'
#' Usually there is no reason to set the \code{fetch.*} arguments to \code{FALSE}, as you want all information
#' completely encapsulated in the task object.
#'
#' @param id [\code{\link{integer(1)}}]\cr 
#'   ID number of task on OpenML server, used to retrieve the task. 
#' @param dir [\code{\link{integer(1)}}]\cr 
#'   Directory where downloaded files from the repository are stored. 
#'   Default is the path of the per-session temporary directory \code{\link{tempdir()}}.
#' @param clean.up [\code{\link{logical(1)}}]\cr 
#'   Should the downloaded files be removed from disk at the end?
#'   Default is \code{TRUE}.
#' @param fetch.data.set.description [\code{\link{logical(1)}}]\cr 
#'   Should the data set description also be downloaded? 
#'   Default is \code{TRUE}.
#' @param fetch.data.set [\code{\link{logical(1)}}]\cr 
#'   Should the data set also be downloaded? 
#'   Default is \code{TRUE}.
#' @param fetch.data.splits [\code{\link{logical(1)}}]\cr 
#'   Should the data splits (for resampling) also be downloaded? 
#'   Default is \code{TRUE}.
#' @return \code{\linkS4class{OpenMLTask}} object.
#' @export
#' @seealso \code{\link{taskType}}, \code{\link{targetFeature}}, \code{\link{expSettings}}, \code{\link{evaluationMeasures}}

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
  
  downloadAPICallFile(api.fun = "openml.tasks.search", file = fn.task, task.id = id)  
  task <- parseOpenMLTask(fn.task)
  
  if (fetch.data.set.description) {
    downloadOpenMLDataSetDescription(task@task.data.desc.id, fn.data.set.desc)
    task@task.data.desc <- parseOpenMLDataSetDescription(fn.data.set.desc)
  }
  
  if (fetch.data.set) {
    downloadOpenMLDataSet(task@task.data.desc@url, fn.data.set)
    task@task.data.desc@data.set <- parseOpenMLDataSet(task@task.data.desc, fn.data.set)
  }
  
  if (fetch.data.splits) {
    downloadOpenMLDataSplits(task@task.estimation.procedure@data.splits.url, fn.data.splits)
    task@task.estimation.procedure@data.splits <- parseOpenMLDataSplits(task@task.data.desc@data.set, fn.data.splits)
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
  
  getParams <- function(path) {
    ns.parameters <- getNodeSet(doc, paste(path, "oml:parameter", sep ="/"))
    parameters <- lapply(ns.parameters, function(x) xmlValue(x))
    names(parameters) <- sapply(ns.parameters, function(x) xmlGetAttr(x, "name"))
    parameters
  }
  
  # task
  task.id <- xmlRValI(doc, "/oml:task/oml:task_id")
  task.type <- xmlRValS(doc, "/oml:task/oml:task_type")
  targets <- xmlValsMultNsS(doc, "/oml:task/oml:input/oml:data_set/oml:target_feature")
  params <- getParams("oml:task")
  
  # data set description
  data.desc.id <- xmlRValI(doc, "/oml:task/oml:input/oml:data_set/oml:data_set_id")
  data.desc <- NULL
  
  # prediction
  ns.preds.features <- getNodeSet(doc, "/oml:task/oml:output/oml:predictions/oml:feature")
  preds.features <- lapply(ns.preds.features, function(x) xmlGetAttr(x, "type"))
  names(preds.features) <- sapply(ns.preds.features, function(x) xmlGetAttr(x, "name"))
  task.preds <- list(
    format = xmlRValS(doc, "/oml:task/oml:output/oml:predictions/oml:format"),
    features = preds.features
  )
  
  # estimation procedure
  estim.proc <- OpenMLEstimationProcedure(
    type = xmlRValS(doc, "/oml:task/oml:input/oml:estimation_procedure/oml:type"), 
    data.splits.url  = xmlRValS(doc, "/oml:task/oml:input/oml:estimation_procedure/oml:data_splits_url"),
    data.splits = data.frame(),
    parameters = getParams("/oml:task/oml:input/oml:estimation_procedure")
  )
  
  # measures
  measures <- xmlValsMultNsS(doc, "/oml:task/oml:input/oml:evaluation_measures/oml:evaluation_measure")
  
  task = OpenMLTask(
    task.id = task.id,
    task.type = task.type,
    task.target.features = targets,
    task.pars = params,
    task.data.desc.id = data.desc.id,
    task.data.desc = data.desc,
    task.estimation.procedure = estim.proc,
    task.preds = task.preds,
    task.evaluation.measures = measures
  )
  convertOpenMLTaskSlots(task)
}

convertParam <- function(params, name, fun) {
  if(!is.null(params[[name]]))
    params[[name]] <- fun(params[[name]])
  return(params)
}

convertOpenMLTaskSlots = function(task) {
  # convert estim params to correct types
  p <- task@task.estimation.procedure@parameters
  p <- convertParam(p, "number_repeats", as.integer)
  p <- convertParam(p, "number_folds", as.integer)
  task@task.estimation.procedure@parameters <- p
  
  #task@task.evaluation.measures <- strsplit(task@task.evaluation.measures, split=",")[[1]]
  return(task)
}



#FIXME: respect row id attribute