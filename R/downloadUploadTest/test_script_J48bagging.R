library(mlr)

lrn <- makeLearner("classif.J48")
bagging <- makeBaggingWrapper(lrn, bag.iters = 500)
impl <- OpenMLImplementation(
  name = "J48-forest",
  version = "1.0",
  description = "A bagging of 500 J48 decision trees.",
  parameter = makeImplementationParameterList(bagging),
  components = list(OpenMLImplementation(
    name = "J48", 
    version = "1.0", 
    description = "J48 decision tree from package RWeka",
    parameter = makeImplementationParameterList(lrn)))
)

implXML <- writeOpenMLImplementationXML(impl, file="R/downloadUploadTest/J48forest.xml")
session.hash <- authenticateUser("dominik.kirchhoff@tu-dortmund.de", "testpasswort")

uploadOpenMLImplementation("R/downloadUploadTest/J48forest.xml", 
                           sourcefile = "R/downloadUploadTest/J48forest_sourcefile.R", 
                           session.hash = session.hash) 
# Uploading implementation to server.
# Downloading response to: C:\Users\Dom\AppData\Local\Temp\RtmpodPygH\file52c7c6a31e1
# Implementation successfully uploaded. Implementation ID: J48-forest(1.0)

# Download the implementation again:
impl_dl <- downloadOpenMLImplementation("J48-forest(1.0)")