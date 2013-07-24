downloadOpenMLImplementation = function(id, file, show.info = TRUE) {
  checkArg(id, "character", len = 1L, na.ok = FALSE)
  checkArg(file, "character", len = 1L, na.ok = FALSE)
  downloadAPICallFile(api.fun = "openml.implementation.get", file = file, implementation_id = id, show.info = show.info)  
  #parseOpenMLImplementation(file)
}

parseOpenMLImplementation = function(file) {
  checkArg(file, "character", len = 1L, na.ok = FALSE)
  doc <- xmlParse(file)
  
  args = list()
  args[["id"]] <- xmlRValI(doc, "/oml:data_set_description/oml:id")
  args[["name"]] <- xmlRValS(doc, "/oml:data_set_description/oml:name")
  args[["version"]] <- xmlRValS(doc, "/oml:data_set_description/oml:version")
  args[["description"]] <- xmlRValS(doc, "/oml:data_set_description/oml:description")
  args[["format"]] <- xmlRValS(doc, "/oml:data_set_description/oml:format")
  args[["creator"]] <- xmlValsMultNsS(doc, "/oml:data_set_description/oml:creator")
  args[["contributor"]] <- xmlValsMultNsS(doc, "/oml:data_set_description/oml:contributor")
  args[["collection.date"]] <- xmlOValS(doc, "/oml:data_set_description/oml:collection_date")
  args[["upload.date"]] <- xmlRValD(doc, "/oml:data_set_description/oml:upload_date")
  args[["language"]] <- xmlOValS(doc, "/oml:data_set_description/oml:language")
  args[["licence"]] <- xmlOValS(doc, "/oml:data_set_description/oml:licence")
  args[["url"]] <- xmlRValS(doc, "/oml:data_set_description/oml:url")
  args[["row.id.attribute"]] <- xmlOValS(doc, "/oml:data_set_description/oml:row_id_attribute")
  args[["md5.checksum"]] <- xmlRValS(doc, "/oml:data_set_description/oml:md5_checksum")
  args[["data.set"]] <- data.frame()
  
  impl <- do.call(OpenMLImplementation, args)
  convertOpenMLImplementation(impl)
}

convertOpenMLImplementation <- function(impl) {
  impl
}
