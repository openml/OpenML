downloadFile = function(api.fun, file, ...) {
  messagef("Downloading file: %s", file)
  url = getServerFunctionURL(api.fun, ...)
  text = getURL(url)
  cat(file = file, text)
  invisible(NULL)
}