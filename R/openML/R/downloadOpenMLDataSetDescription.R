#' Download and store XML data set description file from OpenML server.
#'
#' @param id [\code{integer(1)}]\cr
#'   Data set id.
#' @param file [\code{character(1)}]\cr
#'   Path where XML file shall be stored.
#' @return [Nothing].
#' @export   
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
  
#   representation(id="numeric",
#                  name="character",
#                  version="character",
#                  creator="character",
#                  contributor="character",
#                  date="Date",
#                  description="character",
#                  language="character",
#                  format="character",
#                  licence="character",
#                  url="character",
#                  row.id.attribute="character",
#                  md5.checksum="character",
#                  data.set="data.frame"
#   ))

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
    url = xmlValue(getNodeSet(doc, "/oml:data_set_description/oml:url")[[1]]),
    row.id.attribute = xmlValue(getNodeSet(doc, "/oml:data_set_description/oml:row_id_attribute")[[1]]),
    md5.checksum = xmlValue(getNodeSet(doc, "/oml:data_set_description/oml:md5_checksum")[[1]]),
    data.set = data.frame()
  )
}


#' Get data of data set description object from URL.
#'
#' Retrieves data of a data set description from the URL in the description.
#' The data is expected to be an ARFF file, which is downloaded an read and returned as
#' a \code{data.frame}. The data is stored under element \code{data}
#' in the data set description object. 
#' @param dsd [\code{DataSetDescription}]\cr
#'   Description object.
#' @return [\code{DataSetDescription}]. 
#'   Changed object.   
#' @export   
retrieveData = function(dsd)  {
  checkArg(dsd, "OpenMLDataSetDescription")
  data = getURL(dsd$url)
  data = read.arff(textConnection(data))
  dsd$data = data 
  return(dsd)
}  

