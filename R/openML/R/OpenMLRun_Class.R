setClass("OpenMLRun", representation(
  task.id = "character",
  implementation.id = "character",
  parameter.settings = "list"
))


# ***** Constructor *****
OpenMLRun <- function(task.id, implementation.id, parameter.settings=list()) {
  new("OpenMLRun",
    task.id = task.id,
    implementation.id = implementation.id,
    parameter.settings = parameter.settings
  )
}

# ***** Methods *****

setMethod("show", "OpenMLRun", function(object) {
  catf('** Information on an OpenML Run **\n')
  catf('Task ID           :: %s', object@task.id)
  catf('Implementation ID :: %s', object@implementation.id)
  #FIXME reimplent
  #if (length(object@parameter.settings)) {
  #  cat('Parameter Settings used on the Run:\n')
  #  for(i in 1:length(object@parameter.settings))
  #    print(object@parameter.settings[i])
  #}
  #cat('\n')
})




