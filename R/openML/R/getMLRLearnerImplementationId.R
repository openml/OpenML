getMLRLearnerImplementationId <- function(learner, register=TRUE, session.hash){
  
}

# FIXME: no clean handling of sourcefiles.
registerMLRLearnerImplementation <- function(learner, session.hash, sourcefile,...){
  name <- learner$id
  description <- sprintf("MLR learner object %s from package %s.", name, learner$package)
  desc <- OpenMLImplementation(name = name, description = description, ...)
  uploadOpenMLImplementation(description = desc, sourcefile = sourcefile, session.hash = session.hash)
}