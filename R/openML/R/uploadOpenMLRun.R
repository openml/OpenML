#' @export
uploadOpenMLRun <- function(description, output.files, session.hash, show.info = TRUE) {
  file <- tempfile()
  if (show.info) {
    messagef("Uploading run to server.")
    messagef("Downloading response to: %s", file)
  }
  
  url <- getServerFunctionURL("openml.run.upload")
  params <- list(
    description = description,
    output_files = output.files,
    session_hash = session.hash
  )
  content <- postForm(url, .params = params, .checkParams = FALSE)
  write(content, file = file)
  doc = parseXMLResponse(file, "Uploading run", "response")
  if (show.info) 
    messagef("Run successfully uploaded.")
}

