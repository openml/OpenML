setClass("OpenMLRunParameter", representation(
  name = "character",
  value = "character",
  component = "character"
))

# ***** Constructor *****
OpenMLRunParameter <- function(name, value, component = "") {
  new("OpenMLRunParameter", 
    name = name,
    value = value,
    component = component
  )
}

# ***** Methods *****

# show
setMethod("show", "OpenMLRunParameter", function(object) {
  s <- if (object@component != "")
    sprintf(' (parameter of component %s)', object@component)
  else
    ""
  # FIXME does this work for arbitary values? unit test this
  catf("%s %s = %s", s, object@name, object@value)
})
