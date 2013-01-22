context("parseOpenMLDataSetDescription")

test_that("parseOpenMLDataSetDescription", {
  parseOpenMLDataSetDescription(file.path(xml.example.dir, "dataset.xml"))
})  