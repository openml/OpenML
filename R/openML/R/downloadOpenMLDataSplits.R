downloadOpenMLDataSplits = function(url, file) {
  checkArg(url, "character", len = 1L, na.ok = FALSE)
  checkArg(file, "character", len = 1L, na.ok = FALSE)
  downloadBinaryFile(url, file)
}

parseOpenMLDataSplits = function(ds, file) {
  checkArg(ds, "data.frame")
  checkArg(file, "character", len = 1L, na.ok = FALSE)  
  splits <- read.arff(file)
	convertOpenMLDataSplits(ds, splits)
}

convertOpenMLDataSplits = function(ds, splits) {
  # 'repeat' is a BAD col. name in R
  colnames(splits)[colnames(splits) == "repeat"] <- "rep" 
	# all counters in OpenML (server) are 0-based, R is 1-based
  ri <- splits$rowid
	rns <- rownames(ds)
	# FIXME: possibly inefficient
	splits$rowid <- sapply(ri, function(x) which(x == rns))
  splits$rep <- splits$rep + 1
	splits$fold <- splits$fold + 1
  return(splits)
}