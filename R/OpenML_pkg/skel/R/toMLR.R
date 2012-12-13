toMLR = function(task) {
  dsd = readDataSetDescription("/home/bischl/cos/OpenML/data_set_desc.xml")
  dsd = retrieveData(dsd)
  requirePackages("mlr", why="toMLR")
  if (task$type == "classification")
    mlr.task = makeClassifTask(data = dsd$data, target = task$target.feature)
  if (task$eval.method == "cross-validation") {
    if (task$eval.method.args$number.of.repeats == 1L)
      mlr.rdesc = makeResampleDesc("CV", iters = task$eval.method.args$number.of.folds)
    else 
      mlr.rdesc = makeResampleDesc("RepCV", reps = task$eval.method.args$number.of.repeats, 
        folds = task$eval.method.args$number.of.folds)
  }
  mlr.rin = makeResampleInstance(mlr.rdesc, size = nrow(dsd$data))  
  list(mlr.task = mlr.task, mlr.rin = mlr.rin)
}

