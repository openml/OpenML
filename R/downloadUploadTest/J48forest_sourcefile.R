library(mlr)
lrn <- makeLearner("classif.J48")
bagging <- makeBaggingWrapper(lrn, bag.iters = 500)