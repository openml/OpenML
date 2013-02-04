toMLR <- function(task) {
  requirePackages("mlr", why="toMLR")
  task.type <- taskType(task)
  data.set.desc <- task@task.data.desc
  data <- sourceData(task)
  target <- targetFeatures(task)
  estim.proc <- task@task.estimation.procedure
  if (task.type == "Supervised Classification") {
    mlr.task <- makeClassifTask(data = data, target = target)
  }
  mlr.rin <- createMLRResampleInstance(estim.proc, mlr.task)
  mlr.measures <- createMLRMeasures(task@task.evaluation.measures)
  list(mlr.task = mlr.task, mlr.rin = mlr.rin, mlr.measures = mlr.measures)
}

createMLRResampleInstance <- function(estim.proc, mlr.task) {
  type <- estim.proc@type
  n.repeats <- estim.proc@parameters[["number_repeats"]]
  n.folds <- estim.proc@parameters[["number_folds"]]
  data.splits <- estim.proc@data.splits
  # FIXME : more resampling
  if (type == "cross_validation") {
    if (n.repeats == 1L)
      mlr.rdesc <- makeResampleDesc("CV", iters = n.folds, stratify = TRUE)
    else 
      mlr.rdesc <- makeResampleDesc("RepCV", reps = n.repeats, folds = n.folds, stratify = TRUE)
  } else {
    stopf("Unsupported estimation procedure type: %s", type)
  }
  mlr.rin = makeResampleInstance(mlr.rdesc, task = mlr.task)  
  iter = 1L
  #print(table(data.splits$rep, data.splits$fold, data.splits$type))
  for (r in 1:n.repeats) {
    for (f in 1:n.folds) {
      d = subset(data.splits, rep ==  r & data.splits$fold == f)
      mlr.rin$train.inds[[iter]] = subset(d, type == "TRAIN")$rowid 
      mlr.rin$test.inds[[iter]] = subset(d, type == "TEST")$rowid
      iter = iter + 1L
    }
  }
  return(mlr.rin)
}

createMLRMeasures <- function(measures) {
  lapply(measures, function(m) {
    switch(m, 
      predictive_accuracy = acc,
      stopf("Unsupported evaluation measure: %s", m)     
    )
  })
}
