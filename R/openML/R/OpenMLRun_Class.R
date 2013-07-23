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

RunParameter <- function(name,
                         value,
                         component="")
  new("RunParameter",name=name,value=value,component=component)



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
# Luis Torgo, Bernd Bischl and Paula Branco, Jan 2013
# ==============================================================


# --------------------------------------------------------------
# class def
setClass("OpenMLRun",
         representation(task.id="character",
                        implementation.id="character",
                        parameter.settings="list"
                        ))


# --------------------------------------------------------------
# constructor function
OpenMLRun <- function(task.id,implementation.id,parameter.settings=list())
  new("OpenMLRun",task.id=task.id,implementation.id=implementation.id,
      parameter.settings=parameter.settings)




# --------------------------------------------------------------
# Methods:


# show
setMethod("show","OpenMLRun",
          function(object) {
            ## 
            cat('\n** Information on an OpenML Run **\n\n')
            cat('Task ID           :: ',object@task.id,'\n')
            cat('Implementation ID :: ',object@implementation.id,'\n')
            if (length(object@parameter.settings)) {
              cat('Parameter Settings used on the Run:\n')
              for(i in 1:length(object@parameter.settings))
                print(object@parameter.settings[i])
            }
            cat('\n')
          }
          )



