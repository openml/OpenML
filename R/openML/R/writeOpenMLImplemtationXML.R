
writeOpenMLImplementationXML = function(description) {
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
  
  comp <- newXMLNode("components", parent = top, namespace = "oml")
  imp <- newXMLNode("implementation", parent = comp, namespace = "oml")
  addChildren(imp,
              mynode("name", "C"),
              mynode("version", "1.0"))

  
#   mynode("format", "xml")
#   mynode("licence", "public domain<<")
#   mynode("language", "English")
#   mynode("date", as.Date(Sys.time()))
#   
#   mynode("description", "bla bla")
#   mynode("readme", "bla bla")
#   
#   mynode("operating_system", Sys.info()["sysname"])
#   mynode("programming_language", "R")
  print(top)
}


# library(XML)
# 
# 
# doc = newXMLDoc()
# top = newXMLNode("top", parent = doc, namespace = c(oml = "http://www.openml.org/implmentation"))
# 
# mynode = function(name, val, parent = top)
#   newXMLNode(name, as.character(val), parent = parent, namespace = "oml")
# 
# mynode("format", "xml")
# mynode("licence", "public domain<<")
# mynode("language", "English")
# mynode("date", as.Date(Sys.time()))
# 
# mynode("description", "bla bla")
# mynode("readme", "bla bla")
# 
# mynode("operating_system", Sys.info()["sysname"])
# mynode("programming_language", "R")
# 
# # 
# # 
# # 
# # 
# # 
# # 
# # #saveXML(top, file="bla.xml")
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
# # # 
# # 
# # # doc = newXMLDoc()
# # # 
# # # # Simple creation of an XML tree using these functions
# # # top = newXMLNode("a")
# # # newXMLNode("b", attrs = c(x = 1, y = 'abc'), parent = top)
# # # newXMLNode("c", "With some text", parent = top)
# # # d = newXMLNode("d", newXMLTextNode("With text as an explicit node"), parent = top)
# # # newXMLCDataNode("x <- 1\n x > 2", parent = d)
# # # 
# # # newXMLPINode("R", "library(XML)", top)
# # # newXMLCommentNode("This is a comment", parent = top)
# # # 
# # # o = newXMLNode("ol", parent = top)
# # # 
# # # kids = lapply(letters[1:3],
# # #               function(x)
# # #                 newXMLNode("li", x))
# # # addChildren(o, kids)
# # # 
# # # cat(saveXML(top))