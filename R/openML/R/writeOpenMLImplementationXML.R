# Generate an XML file for an OpenMLImplementation object.
#
# @param description [\code{\link{OpenMLImplementation}}]\cr
#   OpenML implementation description object.
# @param file [\code{character(1)}]\cr
#   Destination path where the XML file should be saved.
# @return [\code{invisible(NULL)}].
writeOpenMLImplementationXML <- function(description, file = character(0)) {
  checkArg(description, "OpenMLImplementation", s4 = TRUE)
  checkArg(file, "character")
  
  doc <- newXMLDoc()
  top <- newXMLNode("oml:implementation", parent = doc, namespace = c(oml = "http://www.openml.org/implementation"))
  
  addNodes <- function(description, doc, parent = top) {  
    mynode <- function(name, val, parent = top){
      if(length(val)) 
        newXMLNode(name, as.character(val), parent = parent, namespace = "oml")
    }
      
    mynode("name", description@name, parent)
    mynode("version", description@version, parent)
    mynode("description", description@description, parent)
    mynode("creator", description@creator, parent)
    mynode("contributor", description@contributor, parent)
    mynode("licence", description@licence, parent)
    mynode("language", description@language, parent)
    mynode("full_description", description@full.description, parent)
    mynode("installation_notes", description@installation.notes, parent)
    mynode("dependencies", description@dependencies, parent)
    #mynode("bibliographical_reference", description@bibliographical.reference)
    if(length(description@parameter)) {
      for(i in seq_along(description@parameter)) {
        par <- newXMLNode("parameter", parent = top, namespace = "oml")
        mynode("name", description@parameter[[i]]@name, parent = par)
        mynode("data_type", description@parameter[[i]]@data.type, parent = par)
        mynode("default_value", description@parameter[[i]]@default.value, parent = par)
        mynode("description", description@parameter[[i]]@description, parent = par)
      }
    } 
    mynode("source_format", description@source.format, parent)
    mynode("binary_format", description@binary.format, parent)
    mynode("source_md5", description@source.md5, parent)
    mynode("binary_md5", description@binary.md5, parent)
    return(doc)
  }
  
  doc <- addNodes(description, doc, top)
  
  if(length(description@components)) {
    comp <- newXMLNode("components", parent = top, namespace = "oml")
    for(i in seq_along(description@components)) {
      sub.impl <- newXMLNode("implementation", parent = comp, namespace = "oml")
      doc <- addNodes(description@components[[i]], doc, parent = sub.impl)
    }
  }
  print(doc)
  #FIXME: remove if(length(file)) later.
  if(length(file))
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
