# Generate an XML file for an OpenMLRun object.
#
# @param description [\code{\link{OpenMLRun}}]\cr
#   OpenML run description object.
# @param file [\code{character(1)}]\cr
#   Destination path where the XML file should be saved.
# @return [\code{invisible(NULL)}].


# <?xml version="1.0" encoding="UTF-8"?>
#   <oml:run xmlns:oml="http://open-ml.org/openml">
#   <oml:task_id>1</oml:task_id>
#   <oml:implementation_id>knime.user.myworkflow_1.2</oml:implementation_id>
#   <oml:parameter_setting>
#   <oml:name>C</oml:name>
#   <oml:value>0.01</oml:value>
#   <oml:component>knime.component</oml:component> <!-- optional -->
#   </oml:parameter_setting>
#   <oml:parameter_setting>
#   <oml:name>C</oml:name>
#   <oml:value>0.01</oml:value>
writeOpenMLRunXML <- function(description, file = character(0)) {
  checkArg(description, "OpenMLRun", s4 = TRUE)
  checkArg(file, "character")
  
  doc <- newXMLDoc()
  top <- newXMLNode("oml:run", parent = doc, namespace = c(oml = "http://www.openml.org/run"))
  # FIXME check against carefully against schema 
  mynode <- function(name, val, parent = top) {
    if(length(val)) 
      newXMLNode(name, as.character(val), parent = parent, namespace = "oml")
  }
  
  mynode("task_id", description@task.id)
  mynode("implementation_id", description@implementation.id)
  
  if(length(description@parameter.settings)) {
    for(i in seq_along(description@parameter.settings)) {
      par.setting <- newXMLNode("parameter_setting", parent = top, namespace = "oml")
      mynode("name", description@parameter.settings[[i]]@name, parent = par.setting)
      mynode("value", description@parameter.settings[[i]]@value, parent = par.setting)
      mynode("component", description@parameter.settings[[i]]@component, parent = par.setting)
    }
  }
  print(doc)
  # FIXME: Remove if(length(file)) later.
  if(length(file))
    saveXML(top, file = file)
}
