downloadOpenMLDataSet <- function(url, file, show.info) {
  checkArg(url, "character", len = 1L, na.ok = FALSE)
  checkArg(file, "character", len = 1L, na.ok = FALSE)
  downloadBinaryFile(url, file, show.info)
}

parseOpenMLDataSet = function(dsd, file) {
  checkArg(dsd, "OpenMLDataSetDescription")  
  checkArg(file, "character", len = 1L, na.ok = FALSE)
  ds <- read.arff(file)
  convertOpenMLDataSet(dsd, ds)
}


convertOpenMLDataSet <- function(dsd, ds) {
  #FIXME what is missing value for row_id? check defaults?
  # remove rowid from data and set as rownames  
  if (dsd@row.id.attribute != "") {
    rowid <- ds[, dsd@row.id.attribute]
    ds[, dsd@row.id.attribute] <- NULL
  } else{
    rowid <- as.character(0:(nrow(ds)-1))
  }
  setRowNames(ds, as.character(rowid))
}

