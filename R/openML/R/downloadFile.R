downloadAPICallFile = function(api.fun, file, ...) {
  checkArg(api.fun, "character", len = 1L, na.ok = FALSE)
  checkArg(file, "character", len = 1L, na.ok = FALSE)
  url = getServerFunctionURL(api.fun, ...)
  downloadBinaryFile(url, file)
}

downloadBinaryFile= function(url, file) {
  checkArg(url, "character", len = 1L, na.ok = FALSE)
  checkArg(file, "character", len = 1L, na.ok = FALSE)
  messagef("Downloading file: %s from:\n  %s", file, url)
  content = getURL(url)
  write(content, file = file)
  invisible(NULL)
}