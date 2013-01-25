downloadOpenMLDataSet = function(dsd, file) {
  checkArg(dsd, "OpenMLDataSetDescription")
  checkArg(file, "character", len = 1L, na.ok = FALSE)
  url = getServerFunctionURL("openml.data.description", data.id = id)
  text = getURL(url)
  cat(file = file, text)
  invisible(NULL)
}

parseOpenMLDataSet = function(dsd, file) {
  checkArg(dsd, "OpenMLDataSetDescription")  
  checkArg(file, "character", len = 1L, na.ok = FALSE)
  ds <- read.arff(file)
  convertOpenMLDataSet(dsd, ds)
}


convertOpenMLDataSet <- function(dsd, ds) {
  #FIXME what is missing value for row_id? check defaults?
  print(dsd)
  # remove rowid from data and set as rownames  
  if (dsd@row.id.attribute != "") {
    rowid <- ds[, dsd@row.id.attribute]
    ds[, dsd@row.id.attribute] <- NULL
  } else{
    rowid <- as.character(0:(nrow(ds)-1))
  }
  setRowNames(ds, as.character(rowid))
}

