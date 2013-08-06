
xmlNs <- function(doc, path, optional) {
  ns <- getNodeSet(doc, path)
  if (length(ns) == 0L) {
    if (optional)
      NULL
    else
      stopf("Required XML node not found: %s", path)
  } else {
    ns
  }
}

xmlVal <- function(doc, path, optional, fun) {
  ns <- xmlNs(doc, path, optional)
	# path not found, also cant be no optional, otherwise exception in call before
	if (is.null(ns))
	  return(NULL)		
  if (length(ns) == 1L) {
    fun(xmlValue(ns[[1]]))
  } else {
    stopf("Multiple XML nodes found: %s", path)
  }
}

xmlOValS <- function(doc, path) {
  xmlVal(doc, path, TRUE, as.character)
}

xmlOValI <- function(doc, path) {
  xmlVal(doc, path, TRUE, as.integer)
}

xmlOValR <- function(doc, path) {
  xmlVal(doc, path, TRUE, as.numeric)
}

xmlOValD <- function(doc, path) {
  xmlVal(doc, path, FALSE, as.Date)
}

xmlRValS <- function(doc, path) {
  xmlVal(doc, path, FALSE, as.character)
}

xmlRValI <- function(doc, path) {
  xmlVal(doc, path, FALSE, as.integer)
}

xmlRValR <- function(doc, path) {
  xmlVal(doc, path, FALSE, as.numeric)
}

xmlRValD <- function(doc, path) {
  xmlVal(doc, path, FALSE, function(x) as.POSIXct(x, tz="CET"))
}

xmlValsMultNs <- function(doc, path, fun, val) {
  ns <- getNodeSet(doc, path)
  vapply(ns, function(x) fun(xmlValue(x)), val)
}

xmlValsMultNsS <- function(doc, path, fun) {
  xmlValsMultNs(doc, path, as.character, character(1))
}

expectXMLType <- function(file, doc, type) {
  r <- xmlRoot(doc)
  rootname <- xmlName(r)
  if (rootname != type) 
    stopf("Expected to find XML type %s, not %s, in file %s", type, rootname, file)
}

isErrorXML <- function(doc) {
  r <- xmlRoot(doc)
  rootname <- xmlName(r)
  if (rootname == "error") {
    code <- xmlRValI(doc, "/oml:error/oml:code")
    msg <- xmlRValS(doc, "/oml:error/oml:message")
    return(list(code = code, msg = msg))
  } else {
    return(NULL)    
  }
}

checkAndHandleErrorXML <- function(file, doc, prefix.msg) {
  z <- isErrorXML(doc)
  if (!is.null(z)) {
    stopf("Error in server / XML response for: %s\n%s\nFile: %s", prefix.msg, z$msg, file)
  }
}

parseXMLResponse <- function(file, msg, type) {
  doc <- try(xmlParse(file))
  if (is.error(doc)) {
    stopf("Error in parsing XML for type %s in file: %s", type, file)
  }
  checkAndHandleErrorXML(file, doc, msg)
  expectXMLType(file, doc, type)
  return(doc)
}

# FIXME: add expectXMLType (otheriwise error) for the parser functions
