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
writeOpenMLRunXML <- function(description, file) {
  doc <- newXMLDoc()
  top <- newXMLNode("oml:run", parent = doc, namespace = c(oml = "http://www.openml.org/run"))
  # FIXME check against carefully against schema 
  mynode <- function(name, val, parent = top)
    newXMLNode(name, as.character(val), parent = parent, namespace = "oml")
  
  mynode("task_id", description@task.id)
  mynode("implementation_id", description@implementation.id)
  saveXML(top, file = file)
}
