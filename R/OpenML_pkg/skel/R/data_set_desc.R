
readDataSetDescription = function(file) {
  doc = xmlParse(file)
  
  structure(list(
    id = xmlValue(getNodeSet(doc, "/data_set_description/id")[[1]]),
    name = xmlValue(getNodeSet(doc, "/data_set_description/name")[[1]]),
    url = xmlValue(getNodeSet(doc, "/data_set_description/url")[[1]]),
    author = xmlValue(getNodeSet(doc, "/data_set_description/author")[[1]]),
    license = xmlValue(getNodeSet(doc, "/data_set_description/license")[[1]]),
    row.id.attribute = xmlValue(getNodeSet(doc, "/data_set_description/row_id_attribute")[[1]]),
    md5.checksum = xmlValue(getNodeSet(doc, "/data_set_description/md5_checksum")[[1]])
  ), class = "DataSetDescription")
}

print.DataSetDescription = function(x, ...) {
  catf("Id:         %s", x$id)
  catf("Name:       %s", x$name)
  catf("URL:        %s", x$url)
  catf("Author:     %s", x$author)
  catf("License:    %s", x$license)
  catf("Row Id:     %s", x$row.id.attribute)
  if (is.null(x$data))
  catf("Data:       <not loaded>")
  else
  catf("Data:       (%i, %i)", nrow(x$data), ncol(x$data))
}

retrieveData = function(dsd)  {
  checkArg(dsd, "DataSetDescription")
  data = getURL(dsd$url)
  data = read.arff(textConnection(data))
  dsd$data = data 
  return(dsd)
}  

