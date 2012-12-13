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
  structure(list(
    type = type,
    data.set.id = data.set.id, 
    target.feature = target.feature,
    eval.method = eval.method,
    eval.method.args = eval.method.args
  ), class = "Task")
}

print.Task = function(x, ...) {
  catf("Type:             %s", x$type)
  catf("Target:           %s", x$target.feature)
  if (x$eval.method == "cross-validation")
  catf("Eval method:      %s (%i, %i)", x$eval.method, x$eval.method.args$number.of.repeats, 
    x$eval.method.args$number.of.folds)
  else
  catf("Eval method:      %s", x$eval.method)
}

