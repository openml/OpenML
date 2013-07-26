#' @export
uploadOpneMLImplementation = function(description, sourcefile, binaryfile, session.hash) {
    
  
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