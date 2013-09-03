#' Upload an OpenML implementation to the server.
#' 
#' @param description [\code{\link{OpenMLImplementation}}]\cr 
#'   An OpenMLImplementation object. Should at least contain a name and a description.
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
uploadOpenMLImplementation <- function(description, sourcefile, binaryfile, session.hash, 
  show.info = TRUE) {
  
  file <- tempfile()
  
  #writeOpenMLImplementationXML(description, file)
  
   if (show.info) {
     messagef("Uploading implementation to server.")
     messagef("Downloading response to: %s", file)
   }
  
  url <- getServerFunctionURL("openml.implementation.upload")
  #FIXME: handle binary
  response <- postForm(url, 
    session_hash = session.hash,
    description = fileUpload(filename = description),
    source = fileUpload(filename = sourcefile)
  )
  write(response, file = file)
  #FIXME: not very elegant, the XMLResponse can be of type "response" or "upload_implementation"...
  doc <- try(parseXMLResponse(file, "Uploading implementation", "upload_implementation"), silent = TRUE)
  if(is.error(doc))
    doc <- parseXMLResponse(file, "Uploading implementation", "response")
  
  if (show.info) {
    messagef("Implementation successfully uploaded. Implementation ID: %s", 
             xmlOValS(doc, "/oml:upload_implementation/oml:id"))
  }    
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