library(methods)
library(devtools)
library(testthat)

if (interactive()) {
  library(BBmisc)
  library(XML)
  library(RCurl)
  library(RWeka)
  load_all("openML")
  xml.example.dir = normalizePath("../XML/Examples")
} else {
  library(openML)  
}
test_dir("openML/inst/tests")
