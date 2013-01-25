xmlVal = function(doc, path, optional, fun) {
  n = getNodeSet(doc, path)
  if (length(n) == 0L) {
    if (optional)
      NULL
    else
      stopf("Required XML node not found: %s", path)
  } else if (length(n) == 1L) {
    fun(xmlValue(n))
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

xmlRValS = function(doc, path) {
  xmlVal(doc, path, FALSE, as.character)
}

xmlRValI = function(doc, path) {
  xmlVal(doc, path, FALSE, as.integer)
}

xmlRValR = function(doc, path) {
  xmlVal(doc, path, FALSE, as.numeric)
}
