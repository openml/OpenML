
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
  xmlVal(doc, path, FALSE, as.Date)
}

xmlValsMultNs = function(doc, path, fun) {
  ns = getNodeSet(doc, path)
  sapply(ns, function(x) fun(xmlValue(x)))
}

xmlValsMultNsS = function(doc, path, fun) {
  xmlValsMultNs(doc, path, as.character)
}

