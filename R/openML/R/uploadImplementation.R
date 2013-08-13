#' Upload an OpenML implementation to the server.
#' 
#' @param description [???]\cr 
#'   An XML implementation description file. Should at least contain a name and a description.
#' @param sourcefile [\code{character(1)}]\cr
#'   The source code of the implementation. If multiple files, please zip them. 
#'   Either source or binary is required.
#' @param binaryfile [\code{character(1)}]\cr
#'   The binary of the implementation. If multiple files, please zip them. 
#'   Either source or binary is required.
#' @param session.hash [\code{character(1)}]\cr
#'   A session token returned by \code{\link{authenticateUser}}.
#' @param show.info [\code{logical(1)}]\cr
#'   Verbose output on console?
#'   Default is \code{TRUE}.
#' @export

# FIXME: Is 'description' a path to a file or the content of an XML file? 
uploadOpenMLImplementation <- function(description, sourcefile, binaryfile, session.hash, 
  show.info = TRUE) {
  
  file <- tempfile()
   if (show.info) {
     messagef("Uploading implementation to server.")
     messagef("Downloading response to: %s", file)
   }
  
  url <- getServerFunctionURL("openml.implementation.upload")
  
# FIXME: Either source or binary file.
  params <- list(
    description = description,
    sourcefile = sourcefile,
#    binaryfile = binaryfile,
    session_hash = session.hash
  )
  content <- postForm(url, .params = params, .checkParams = FALSE)
  write(content, file = file)
  doc <- parseXMLResponse(file, "Uploading implementation", "response")
  if (show.info) 
    messagef("Implementation successfully uploaded.")
}

# 
# POST description (Required)
# An XML file containing the implementation meta data
# POST source
# The source code of the implementation. If multiple files, please zip them. Either source or binary is required.
# POST binary
# The binary of the implementation. If multiple files, please zip them. Either source or binary is required.
# POST session_hash (Required)
# The session hash, provided by the server on authentication (1 hour valid)