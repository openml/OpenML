#' @export
uploadOpenMLImplementation <- function(description, sourcefile, binaryfile, session.hash, 
                                       show.info = TRUE) {
  
  file <- tempfile()
   if (show.info) {
     messagef("Uploading run to server.")
     messagef("Downloading response to: %s", file)
   }
  
  url <- getServerFunctionURL("openml.implementation.upload")
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