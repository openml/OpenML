# Download a file from the OpenML server through a server API call.
# 
# Gets the URL for the API call from \code{\link{getServerFunctionURL}}.
# Then retrieves the file from the URL by \code{\link{downloadBinaryFile}}.
# 
# @param api.fun [\code{character(1)}]\cr
#   Name of API function to call on server.
# @param file [\code{character(1)}]\cr
#   The destination path.
# @param ... [any]\cr
#   Arguments for API call.
#   Passed through to \code{\link{getServerFunctionURL}}.
# @param show.info [\code{logical(1)}]\cr
#   Verbose output on console? 
#   Default is \code{TRUE}.
# @return [\code{invisible(NULL)}].
downloadAPICallFile <- function(api.fun, file, ..., show.info = TRUE) {
  checkArg(api.fun, "character", len = 1L, na.ok = FALSE)
  checkArg(file, "character", len = 1L, na.ok = FALSE)
  url <- getServerFunctionURL(api.fun, ...)
  downloadBinaryFile(url, file, show.info)
}

# Download a binary file from a given URL.
# 
# Combination of \code{\link{getURL}} and \code{\link{write}}.
# No real errorhandling, if something goes wrong, function will stop.
# 
# @param url [\code{character(1)}]\cr
#   The source URL.
# @param file [\code{character(1)}]\cr
#   The destination path.
# @param show.info [\code{logical(1)}]\cr
#   Verbose output on console? 
#   Default is \code{TRUE}.
# @return [\code{invisible(NULL)}].
downloadBinaryFile <- function(url, file, show.info = TRUE) {
  checkArg(url, "character", len = 1L, na.ok = FALSE)
  checkArg(file, "character", len = 1L, na.ok = FALSE)
  checkArg(show.info, "logical", len = 1L, na.ok = FALSE)
  if (show.info)
    messagef("Downloading file: %s from:\n  %s", file, url)
  content <- getURL(url)
  write(content, file = file)
  invisible(NULL)
}