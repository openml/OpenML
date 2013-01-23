
OPEN_ML_SERVER_API_URL = "http://expdb.cs.kuleuven.be/expdb/api"

# FIXME use getForm?
getServerFunctionURL <- function(fname, ...) {
  url <- sprintf("%s/?f=%s", OPEN_ML_SERVER_API_URL, fname)
  args <- list(...)
  args <- collapse(paste(names(args), args, sep="="), sep="&")
  paste(url, args, sep="&")
}