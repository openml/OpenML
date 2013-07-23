# FIXME dont do this on CRAN!

context("download all tasks")

test_that("download all tasks", {
  # FIXME extend test to all task on openml server
  # FIXME: problem in task 6, 8
  ids = 1:3
  for (i in ids) {
    #print(i)
    task = downloadOpenMLTask(id=i, show.info = FALSE)
    tf = task@task.target.features
    expect_true(is.character(tf) && length(tf) == 1 && !is.na(tf))
    ds = task@task.data.desc@data.set
    expect_true(is.data.frame(ds) && nrow(ds) > 1  && ncol(ds) > 1 )
  }
})  