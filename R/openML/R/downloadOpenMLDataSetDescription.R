downloadOpenMLDataSetDescription = function(id, file) {
  id = convertInteger(id)
  checkArg(id, "integer", len = 1L, na.ok = FALSE)
  checkArg(file, "character", len = 1L, na.ok = FALSE)
  url = getServerFunctionURL("openml.data.description", data.id = id)
  text = getURL(url)
  cat(file = file, text)
  invisible(NULL)
}

parseOpenMLDataSetDescription = function(file) {
  checkArg(file, "character", len = 1L, na.ok = FALSE)
  doc = xmlParse(file)
  url = xmlValue(getNodeSet(doc, "/oml:data_set_description/oml:url")[[1]])
  data = getURL(url)
  data = read.arff(textConnection(data))
  
  OpenMLDataSetDescription(
    id = as.integer(xmlValue(getNodeSet(doc, "/oml:data_set_description/oml:id")[[1]])),
    name = xmlValue(getNodeSet(doc, "/oml:data_set_description/oml:name")[[1]]),
    version = xmlValue(getNodeSet(doc, "/oml:data_set_description/oml:version")[[1]]),
    creator = xmlValue(getNodeSet(doc, "/oml:data_set_description/oml:creator")[[1]]),
    contributor = xmlValue(getNodeSet(doc, "/oml:data_set_description/oml:contributor")[[1]]),
    date = xmlValue(getNodeSet(doc, "/oml:data_set_description/oml:date")[[1]]),
    description = xmlValue(getNodeSet(doc, "/oml:data_set_description/oml:description")[[1]]),
    language = xmlValue(getNodeSet(doc, "/oml:data_set_description/oml:language")[[1]]),
    format = xmlValue(getNodeSet(doc, "/oml:data_set_description/oml:format")[[1]]),
    licence = xmlValue(getNodeSet(doc, "/oml:data_set_description/oml:licence")[[1]]),
    url = url,
    row.id.attribute = xmlValue(getNodeSet(doc, "/oml:data_set_description/oml:row_id_attribute")[[1]]),
    md5.checksum = xmlValue(getNodeSet(doc, "/oml:data_set_description/oml:md5_checksum")[[1]]),
    data.set = data
  )
}

