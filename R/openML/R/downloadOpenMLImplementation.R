#' Download an OpenML implementation from the OpenML server through a server API call.
#' 
#' Retrieves an implementation for a given id. 
#' 
#' @param id [\code{character(1)}]\cr
#'   The implementation id.
#' @param dir [\code{character(1)}]\cr 
#'   Directory where downloaded files from the repository are stored. 
#'   Default is current directory.
#' @param download.source.binary [\code{logical(1)}]\cr    
#'   Should source / binary files of the implementation also be downloaded?
#'   They will also be stored in \code{dir}.
#'   Default is \code{TRUE}.
#' @param show.info [logical(1)]\cr
#'   Verbose output on console? 
#'   Default is \code{TRUE}.
#' @return [\code{\link{OpenMLImplementation}}].
#' @export

downloadOpenMLImplementation <- function(id, dir = getwd(), download.source.binary = TRUE, show.info = TRUE) {
  checkArg(id, "character", len = 1L, na.ok = FALSE)
  checkArg(dir, "character", len = 1L, na.ok = FALSE)
  checkArg(download.source.binary, "logical", len = 1L, na.ok = FALSE)
  fn.impl.xml <- file.path(dir, sprintf("%s.xml", id))  
  downloadAPICallFile(api.fun = "openml.implementation.get", file = fn.impl.xml, implementation_id = id, show.info = show.info)  
  impl <- parseOpenMLImplementation(fn.impl.xml)
  if (download.source.binary) {
    if (impl@source.url != "") {
      if (show.info)
        messagef("Downloading implementation source file.")
      # take 2nd from last element before "/download"
      # shoud be stored filename
      #FIXME is thsi correct?
      fn.impl.src <- rev(str_split(impl@source.url, "/")[[1]])[2]
      fn.impl.src <- file.path(dir, fn.impl.src)  
      downloadBinaryFile(url = impl@source.url, file = fn.impl.src, show.info = show.info)
    }
    if (impl@binary.url != "") {
      if (show.info)
        messagef("Downloading implementation binary file.")
      fn.impl.bin <- rev(str_split(impl@binary.url, "/")[[1]])[2]
      fn.impl.bin <- file.path(dir, fn.impl.bin)  
      downloadBinaryFile(url = impl@binary.url, file = fn.impl.bin, show.info = show.info)
    }
  }
  return(impl)
}

parseOpenMLImplementation <- function(file) {
  checkArg(file, "character", len = 1L, na.ok = FALSE)
  doc <- parseXMLResponse(file, "Getting implementation", "implementation")
  args <- list()
  args[["id"]] <- xmlRValS(doc, "/oml:implementation/oml:id")
  args[["name"]] <- xmlRValS(doc, "/oml:implementation/oml:name")
  args[["version"]] <- xmlRValS(doc, "/oml:implementation/oml:version")
  args[["description"]] <- xmlRValS(doc, "/oml:implementation/oml:description")
  args[["creator"]] <- xmlOValS(doc, "/oml:implementation/oml:creator")
  args[["contributor"]] <- xmlOValS(doc, "/oml:implementation/oml:contributor")
  args[["licence"]] <- xmlOValS(doc, "/oml:implementation/oml:licence")
  args[["language"]] <- xmlOValS(doc, "/oml:implementation/oml:description")
  args[["full.description"]] <- xmlOValS(doc, "/oml:implementation/oml:full_description")
  args[["installation.notes"]] <- xmlOValS(doc, "/oml:implementation/oml:installation_notes")
  args[["dependencies"]] <- xmlOValS(doc, "/oml:implementation/oml:dependencies")
  #FIXME: add components and parameters and bin ref
  args[["collection.date"]] <- xmlOValS(doc, "/oml:implementation/oml:collection_date")
  args[["source.url"]] <- xmlOValS(doc, "/oml:implementation/oml:source_url")
  args[["binary.url"]] <- xmlOValS(doc, "/oml:implementation/oml:binary_url")
  args[["binary.format"]] <- xmlOValS(doc, "/oml:implementation/oml:binary_format")
  args[["binary.format"]] <- xmlOValS(doc, "/oml:implementation/oml:binary_format")
  args[["source.md5"]] <- xmlOValS(doc, "/oml:implementation/oml:source_md5")
  args[["binary.md5"]] <- xmlOValS(doc, "/oml:implementation/oml:binary_md5")
  
  impl <- do.call(OpenMLImplementation, args)
  convertOpenMLImplementation(impl)
}

convertOpenMLImplementation <- function(impl) {
  impl
}
