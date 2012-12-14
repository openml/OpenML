#' Reads an XML task file from disk an produces an S3 object which basically contains the
#' same information as the XML.
#' @param file [\code{character(1)}]\cr
#'   Path to XML file.
#' @return [\code{Task}]. 
#'   S3 object as a list.
#' @export   
readTaskFile = function(file) {
  doc = xmlParse(file)
  type = xmlValue(getNodeSet(doc, "/oml:task/oml:task-type/oml:prediction/oml:prediction-type")[[1]])
  data.set.id = xmlValue(getNodeSet(doc, "/oml:task/oml:task-type/oml:prediction/oml:data-set/oml:data-set-id")[[1]])
  target.feature= xmlValue(getNodeSet(doc, "/oml:task/oml:task-type/oml:prediction/oml:data-set/oml:target-feature")[[1]])
  neval = getNodeSet(doc, "/oml:task/oml:task-type/oml:prediction/oml:evaluation-method")[[1]]
  nevalmeth = xmlChildren(neval)[[1]]
  eval.method = xmlName(nevalmeth)
  nevalmeth.childs = xmlChildren(nevalmeth)
  if (eval.method == "cross-validation") {
    eval.method.args = list(
      number.of.repeats = as.integer(xmlValue(nevalmeth.childs[["number-of-repeats"]])),
      number.of.folds = as.integer(xmlValue(nevalmeth.childs[["number-of-folds"]]))
    )  
  }
  ns = names(nevalmeth.childs)
  reps = nevalmeth.childs[ns == "repeat"]
  repeats = lapply(reps, function(r) {
    chs = xmlChildren(r)
    ns = names(chs)
    folds = chs[ns == "fold"]
    lapply(folds, function(f) {
      obs1 = xmlChildren(xmlChildren(f)[["fold-train"]])[["observations"]]
      obs2 = xmlChildren(xmlChildren(f)[["fold-test"]])[["observations"]]
      list(
        train = as.integer(strsplit(xmlValue(obs1), split = " ")[[1]]) + 1L,
        test = as.integer(strsplit(xmlValue(obs2), split = " ")[[1]]) + 1L
      )  
    })
  })
  structure(list(
    type = type,
    data.set.id = data.set.id, 
    target.feature = target.feature,
    eval.method = eval.method,
    eval.method.args = eval.method.args, 
    repeats = repeats
  ), class = "Task")
}

#' @S3method print Task
print.Task = function(x, ...) {
  catf("Type:             %s", x$type)
  catf("Target:           %s", x$target.feature)
  if (x$eval.method == "cross-validation")
  catf("Eval method:      %s (%i, %i)", x$eval.method, x$eval.method.args$number.of.repeats, 
    x$eval.method.args$number.of.folds)
  else
  catf("Eval method:      %s", x$eval.method)
}

