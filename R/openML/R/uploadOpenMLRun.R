#' Upload an OpenML run to the server.
#' 
#' Share a run of an implementation on a given OpenML task.
#' 
#' @param description [???]\cr 
#'   An XML run description file. Should contain the task and implementation id
#'   and optionally any parameter settings that are specific for this run.
#' @param output.files [\code{character(1)}]\cr
#'   A file containing the output files required by the task type used. 
#'   For supervised classification, this will be a table with predictions.
#' @param session.hash [\code{character(1)}]\cr
#'   A session token returned by \code{\link{authenticateUser}}.
#' @param show.info [\code{logical(1)}]\cr
#'   Verbose output on console?
#'   Default is \code{TRUE}.
#' @export

# FIXME: Is 'description' a path to a file or the content of an XML file? 

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

