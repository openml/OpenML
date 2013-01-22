toMLR <- function(task) {
  requirePackages("mlr", why="toMLR")
  task.type <- taskType(task)
  data.set.desc <- task@task.data.desc
  data <- task@task.data.desc@data.set
  target <- targetFeature(task)
  expset <- expSettings(task)
  if (task.type == "Supervised Classification") {
    mlr.task <- makeClassifTask(data = data, target = target)
  }
  mlr.rin <- createMLRResampleInstance(expset, mlr.task)
  list(mlr.task = mlr.task, mlr.rin = mlr.rin)
}

createMLRResampleInstance <- function(expset, mlr.task) {
  type <- expset@type
  n.repeats <- expset@n.repeats
  n.folds <- expset@n.folds
  data.splits <- expset@data.splits

  # FIXME : more resampling
  if (type == "cross-validation") {
    if (n.repeats == 1L)
      mlr.rdesc <- makeResampleDesc("CV", iters = n.folds, stratify = TRUE)
    else 
      mlr.rdesc <- makeResampleDesc("RepCV", reps = n.repeats, folds = n.folds, stratify = TRUE)
  }
  mlr.rin = makeResampleInstance(mlr.rdesc, task = mlr.task)  
  
  iter = 1L
  for (r in 1:n.repeats) {
    for (f in 1:n.folds) {
      d = subset(data.splits, rep ==  r & data.splits$fold == f)
      mlr.rin$train.inds[[iter]] = subset(d, type == "TRAIN")$rowid + 1
      mlr.rin$test.inds[[iter]] = subset(d, type == "TEST")$rowid + 1
      iter = iter + 1L
    }
  }
  return(mlr.rin)
}
