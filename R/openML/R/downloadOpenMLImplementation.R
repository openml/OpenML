downloadOpenMLImplementation = function(id, file, show.info = TRUE) {
  checkArg(id, "character", len = 1L, na.ok = FALSE)
  checkArg(file, "character", len = 1L, na.ok = FALSE)
  downloadAPICallFile(api.fun = "openml.implementation.get", file = file, implementation_id = id, show.info = show.info)  
  parseOpenMLImplementation(file)
}

parseOpenMLImplementation = function(file) {
  checkArg(file, "character", len = 1L, na.ok = FALSE)
  doc <- parseXMLResponse(file, "Getting implementation", "implementation")
  args = list()
  args[["id"]] <- xmlRValS(doc, "/oml:implementation/oml:id")
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
  args[["source.format"]] <- xmlOValS(doc, "/oml:implementation/oml:source_format")
  args[["binary.format"]] <- xmlOValS(doc, "/oml:implementation/oml:binary_format")
  args[["source.md5"]] <- xmlOValS(doc, "/oml:implementation/oml:source_md5")
  args[["binary.md5"]] <- xmlOValS(doc, "/oml:implementation/oml:binary_md5")
  
  impl <- do.call(OpenMLImplementation, args)
  convertOpenMLImplementation(impl)
}

convertOpenMLImplementation <- function(impl) {
  impl
}
