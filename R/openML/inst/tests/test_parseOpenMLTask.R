context("parseOpenMLTask")

test_that("parseOpenMLTask", {
  fn = file.path(xml.example.dir, "task.xml")
  task = parseOpenMLTask(fn)
  capture.output(print(task))
})  