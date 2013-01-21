toMLR = function(task) {
  dsd = readOpenMLDataSetDescription("/home/bischl/cos/OpenML/data_set_description.xml")
  dsd = retrieveData(dsd)
  requirePackages("mlr", why="toMLR")
  if (task$type == "classification")
    mlr.task = makeClassifTask(data = dsd$data, target = task$target.feature)
  if (task$eval.method == "cross_validation") {
    if (task$eval.method.args$number.of.repeats == 1L)
      mlr.rdesc = makeResampleDesc("CV", iters = task$eval.method.args$number.of.folds)
    else 
      mlr.rdesc = makeResampleDesc("RepCV", reps = task$eval.method.args$number.of.repeats, 
        folds = task$eval.method.args$number.of.folds)
  }
  mlr.rin = makeResampleInstance(mlr.rdesc, size = nrow(dsd$data))  
  iter = 1L
  for (i in seq_along(task$repeats)) {
    r = task$repeats[[i]]
    for (j in seq_along(r)) {
      mlr.rin$train.inds[[iter]] = r[[j]]$train
      mlr.rin$test.inds[[iter]] = r[[j]]$test
      iter = iter + 1L
    }
  }
  list(mlr.task = mlr.task, mlr.rin = mlr.rin)
}

