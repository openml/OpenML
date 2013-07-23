if (interactive()) {

context("download helpers")

test_that("download helpers", {
  file = tempfile()
  expect_false(file.exists(file))
  downloadBinaryFile("http://www.statistik.tu-dortmund.de/bischl.html", file)
  expect_true(file.exists(file))
  
  file = tempfile()
  expect_false(file.exists(file))
  downloadAPICallFile("openml.data.description", file = file, data.id = 1) 
  expect_true(file.exists(file))
})  

}