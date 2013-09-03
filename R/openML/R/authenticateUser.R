#' Authenticate at server.
#'
#' Required if you want to upload anything. 
#'
#' @param username [\code{character(1)}]\cr 
#'   Your username / email at OpenML server.
#' @param password [\code{character(1)}]\cr 
#'   Your password at OpenML server.
#' @param show.info [\code{logical(1)}]\cr 
#'   Verbose output on console?
#'   Default is \code{TRUE}.
#' @return [\code{character(1)}]. Session hash for further communication.
#' @export
authenticateUser <- function(username, password, show.info = TRUE) {
  checkArg(username, "character", len = 1L, na.ok = FALSE)
  checkArg(password, "character", len = 1L, na.ok = FALSE)
  checkArg(show.info, "logical", len = 1L, na.ok = FALSE)
  file <- tempfile()
  if (show.info) {
    messagef("Authenticating user at server: %s", username)
    messagef("Downloading response to: %s", file)
  }
  url <- getServerFunctionURL("openml.authenticate")
  md5 <- digest(password, algo="md5", serialize=FALSE)
  params <- list(username = username, password = md5)
  content <- postForm(url, .params = params, .checkParams = FALSE)
  write(content, file = file)
  parseAuthenticateUserResponse(file)
}
  

parseAuthenticateUserResponse = function(file, show.info = TRUE) {
  checkArg(file, "character", len = 1L, na.ok = FALSE)
  doc <- parseXMLResponse(file, "Authenticating user", "authenticate")  
  session.hash <- xmlRValS(doc, "/oml:authenticate/oml:session_hash")
  valid.until <- xmlRValS(doc, "/oml:authenticate/oml:valid_until")
  if (show.info)
    messagef("Retrieved session hash. Valid until: %s", valid.until)
  return(session.hash)
}
