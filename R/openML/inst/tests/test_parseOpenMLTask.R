context("parseOpenMLDataSetDescription")

test_that("parseOpenMLDataSetDescription", {
  fn = file.path(xml.example.dir, "task.xml")
  task = parseOpenMLDataSetDescription(fn, fetch.data.set.description = FALSE)
  print(task)
})  