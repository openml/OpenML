downloadFile = function(api.fun, file, ...) {
  url = getServerFunctionURL(api.fun, ...)
  text = getURL(url)
  cat(file = file, text)
  invisible(NULL)
}