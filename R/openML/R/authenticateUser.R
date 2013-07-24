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
#' @return [\code{character}]. Session hash for further communication.
#' @export
authenticateUser <- function(username, password, show.info = TRUE) {
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
  

parseAuthenticateUserResponse = function(file) {
  checkArg(file, "character", len = 1L, na.ok = FALSE)
  doc <- xmlParse(file)
  r <- xmlRoot(doc)
  rootname <- xmlName(r)
  if (rootname == "error") {
    code <- xmlRValI(doc, "/oml:error/oml:code")
    msg <- xmlRValS(doc, "/oml:error/oml:message")
    stopf("Could not authenticate user: %s", msg)
  } else if (rootname == "authenticate") {
    session.hash <- xmlRValS(doc, "/oml:authenticate/oml:session_hash")
    valid.until <- xmlRValS(doc, "/oml:authenticate/oml:valid_until")
    messagef("Retrieved session hash. Valid until: %s", valid.until)
  } else {
    stop("Unknown server reponse!")
    print(doc)
  }
  return(session.hash)
}
