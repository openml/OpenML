

downloadOpenMLDataSplits = function(id, file) {
  id = convertInteger(id)
  checkArg(id, "integer", len = 1L, na.ok = FALSE)
  checkArg(file, "character", len = 1L, na.ok = FALSE)
  url = getServerFunctionURL("openml.data.description", data.id = id)
  text = getURL(url)
  cat(file = file, text)
  invisible(NULL)
}

parseOpenMLDataSplits = function(file, fetch.data.set) {
  checkArg(file, "character", len = 1L, na.ok = FALSE)  
  d <- read.arff(file)
  colnames(d)[colnames(d) == "repeat"] <- "rep" 
  d
}
