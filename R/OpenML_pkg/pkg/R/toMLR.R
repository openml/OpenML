toMLR = function(task) {
  if (task$type == "Classification")
    mlr.task = makeClassifTask(data = dsd$data, target = task$target.feature)
  mlr.resampling = if (task$eval.method == "cross_validation")
    makeResampleDesc("CV", folds =task$eval.method.args$nr.folds)
  mlr.resampling = makeResampleInstance()  
}

