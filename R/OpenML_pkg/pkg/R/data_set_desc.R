
readDataSetDesc = function(file) {
  doc = xmlParse(file)
  
  structure(list(
    id = xmlValue(getNodeSet(doc, "/DataSetDesc/id")[[1]]),
    name = xmlValue(getNodeSet(doc, "/DataSetDesc/name")[[1]]),
    url = xmlValue(getNodeSet(doc, "/DataSetDesc/url")[[1]]),
    author = xmlValue(getNodeSet(doc, "/DataSetDesc/author")[[1]]),
    license = xmlValue(getNodeSet(doc, "/DataSetDesc/license")[[1]]),
    row.id.attribute = xmlValue(getNodeSet(doc, "/DataSetDesc/row_id_attribute")[[1]]),
    md5.checksum = xmlValue(getNodeSet(doc, "/DataSetDesc/md5_checksum")[[1]])
  ), class = "DataSetDesc")
}

print.DataSetDesc = function(x, ...) {
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
  checkArg(dsd, "DataSetDesc")
  data = getURL(dsd$url)
  data = read.arff(textConnection(data))
  dsd$data = data 
  return(dsd)
}  

library(rjson)
library(XML)
require(RCurl)
library(RWeka)

dsd = readDataSetDesc("/home/bischl/cos/OpenML/data_set_desc.xml")
print(dsd)
catf("")
dsd = retrieveData(dsd)
print(dsd)
