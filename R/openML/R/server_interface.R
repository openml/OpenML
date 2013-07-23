
OPEN_ML_SERVER_API_URL <- "http://expdb.cs.kuleuven.be/expdb/api"

#' Generate a URL to download files from the server API.
#' 
#' 
#' 
#' @param fname [\code{character(1)}]\cr
#'   Name of API function to call on server.
#' @param ... [any]\cr
#'   Arguments for API call.
#' @return [\code{character(1)}].
#' @seealso \code{\link{downloadAPICallFile}}, \code{\link{downloadBinaryFile}}
#' 
# FIXME use getForm?
getServerFunctionURL <- function(fname, ...) {
  url <- sprintf("%s/?f=%s", OPEN_ML_SERVER_API_URL, fname)
  args <- list(...)
  args <- collapse(paste(names(args), args, sep="="), sep="&")
  paste(url, args, sep="&")
}