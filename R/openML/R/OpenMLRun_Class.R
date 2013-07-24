################################################################# 
# THIS FILE DEFINES CLASS OpenMLRun AND THE RESPECTIVE METHODS #
#################################################################

setClass("RunParameter",
         representation(
                        name="character",
                        value="character",
                        component="character"
                       )
)

# ***** Constructor *****
RunParameter <- function(name,
                         value,
                         component="")
  new("RunParameter",name=name,value=value,component=component)


# ***** Methods *****

# show
setMethod("show","RunParameter",
          function(object) {
            cat(object@name,' = ',object@value)
            if (object@component != "")
              cat(' (parameter of component',object@component,')')
            cat('\n')
          })



# ==============================================================
# CLASS: OpenMLRun
# ==============================================================

# --------------------------------------------------------------
# class def
setClass("OpenMLRun",
         representation(task.id="character",
                        implementation.id="character",
                        parameter.settings="list"
                        ))


# ***** Constructor *****
OpenMLRun <- function(task.id,implementation.id,parameter.settings=list())
  new("OpenMLRun",task.id=task.id,implementation.id=implementation.id,
      parameter.settings=parameter.settings)


# ***** Methods *****

# show
setMethod("show","OpenMLRun",
          function(object) {
            ## 
            cat('\n** Information on an OpenML Run **\n\n')
            catf('Task ID           :: %s', object@task.id)
            catf('Implementation ID :: %s', object@implementation.id)
            if (length(object@parameter.settings)) {
              cat('Parameter Settings used on the Run:\n')
              for(i in 1:length(object@parameter.settings))
                print(object@parameter.settings[i])
            }
            cat('\n')
          })



