# FIXME do this on CRAN?
context("download all implementations")

test_that("download all implementations", {
  # FIXME extend test to all implementations on openml server
  # get them from sql
  
  impls = c(
    "aBoostM1(1.24.2.3)",
    "weka.AdditiveRegression(1.17)",
    "weka.ADTree(1.2)",
    "weka.AODE(1.8.2.3)",
    "weka.ARFFLoader(1.22)",
    "weka.AttributeSelectedClassifier(1.16.2.4)",
    "weka.AttributeSelection(1.3)",
    "weka.Bagging(1.31.2.2)",
    "weka.BayesNet(1.21.2.4)",
    "weka.BestFirst(1.29)",
    "weka.BVDecomposeSegCVSub(1.7)"
  )
  
  for (i in seq_along(impls)) {
    id = impls[i]
    print(id)
    impl = downloadOpenMLImplementation(id=id, show.info=TRUE)
  }
})  