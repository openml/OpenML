
xmlNs = function(doc, path, optional) {
  ns = getNodeSet(doc, path)
  if (length(ns) == 0L) {
    if (optional)
      NULL
    else
      stopf("Required XML node not found: %s", path)
  } else {
    ns
  }
}

xmlVal = function(doc, path, optional, fun) {
  ns = xmlNs(doc, path, optional)
	# path not found, also cant be no optional, otherwise exception in call before
	if (is.null(ns))
	  return(NULL)		
  if (length(ns) == 1L) {
    fun(xmlValue(ns[[1]]))
  } else {
    stopf("Multiple XML nodes found: %s", path)
  }
}

xmlOValS = function(doc, path) {
  xmlVal(doc, path, TRUE, as.character)
}

xmlOValI = function(doc, path) {
  xmlVal(doc, path, TRUE, as.integer)
}

xmlOValR = function(doc, path) {
  xmlVal(doc, path, TRUE, as.number)
}

xmlOValD = function(doc, path) {
  xmlVal(doc, path, FALSE, as.Date)
}

xmlRValS = function(doc, path) {
  xmlVal(doc, path, FALSE, as.character)
}

xmlRValI = function(doc, path) {
  xmlVal(doc, path, FALSE, as.integer)
}

xmlRValR = function(doc, path) {
  xmlVal(doc, path, FALSE, as.numeric)
}

xmlRValD = function(doc, path) {
  xmlVal(doc, path, FALSE, function(x) as.POSIXct(x, tz="CET"))
}

xmlValsMultNs = function(doc, path, fun, val) {
  ns = getNodeSet(doc, path)
  vapply(ns, function(x) fun(xmlValue(x)), val)
}

xmlValsMultNsS = function(doc, path, fun) {
  xmlValsMultNs(doc, path, as.character, character(1))
}
