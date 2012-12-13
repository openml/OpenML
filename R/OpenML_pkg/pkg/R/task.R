readTaskFile = function(file) {
  doc = xmlParse(file)
  neval = getNodeSet(doc, "/task/evaluation/evaluation_method")[[1]]
  nevalmeth = xmlChildren(neval)[[1]]
  eval.method = xmlName(nevalmeth)
  if (eval.method == "cross_validation") {
    eval.method.args = list(
      nr.folds = as.integer(xmlGetAttr(nevalmeth, "nr_folds")), 
      nr.repeats = as.integer(xmlGetAttr(nevalmeth, "nr_repeats"))
    )  
  }
  
  structure(list(
    type = xmlValue(getNodeSet(doc, "/task/type")[[1]]),
    target = xmlValue(getNodeSet(doc, "/task/data_set/target_feature")[[1]]),
    eval.method = eval.method,
    eval.method.args = eval.method.args
  ), class = "Task")
}

print.Task = function(x, ...) {
  catf("Type:             %s", x$type)
  catf("Target:           %s", x$target)
  if (x$eval.method == "cross_validation")
  catf("Eval method:      %s (%i, %i)", x$eval.method, x$eval.method.args$nr.repeats, x$eval.method.args$nr.folds)
  else
  catf("Eval method:      %s", x$eval.method)
}

