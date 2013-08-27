
writeOpenMLImplementationXML = function(description, file) {
  doc <- newXMLDoc()
  top <- newXMLNode("oml:implementation", parent = doc, namespace = c(oml = "http://www.openml.org/implementation"))
  
  mynode <- function(name, val, parent = top)
    newXMLNode(name, as.character(val), parent = parent, namespace = "oml")
  
  mynode("name", description@name)
  mynode("version", description@version)
  mynode("description", description@description)
  mynode("creator", description@creator)
  mynode("contributor", description@contributor)
  mynode("licence", description@licence)
  mynode("language", description@language)
  mynode("full_description", description@full.description)
  mynode("installation_notes", description@installation.notes)
  mynode("dependencies", description@dependencies)
  #mynode("bibliographical_reference", description@bibliographical.reference)
  #mynode("parameter", description@parameter)
  #mynode("components", description@components)
  mynode("source_format", description@source.format)
  mynode("binary_format", description@binary.format)
  mynode("source_md5", description@source.md5)
  mynode("binary_md5", description@binary.md5)
  print(doc)
  # FIXME: add this later
  #comp <- newXMLNode("components", parent = top, namespace = "oml")
  #imp <- newXMLNode("implementation", parent = comp, namespace = "oml")
  #addChildren(imp, mynode("name", "C"), mynode("version", "1.0"))
  saveXML(top, file = file)
}


# # 
# cat(saveXML(doc))
#  cat("\n")
# # 
# # 
# # # 
# # # iter = 1L
# # # for (i in 1:reps) {
# # #   for (j in 1:folds) {
# # #     top = newXMLNode("a")
# # #     newXMLNode("", attrs = c(x = 1, y = 'abc'), parent = top)
# # #     newXMLNode("c", "With some text", parent = top)
# # #     print(top)
# # #     saveXML(top, file = "bla.xml")
# # #     
# # #     iter = iter + 1L
# # #   }
# # # }
