#' OpenMLRunParameter
#'
#' This class of objects contains information on the setting of a single parameter of an OpenML run.
#' 
#' Objects can be created by calls of the form \code{OpenMLRunParameter(...)}.
#' The objects contain information on ... .
#'
#'@section Slots: 
#'  \describe{
#'    \item{\code{name}}{[\code{character}]\cr
#'    The name of the parameter.}
#'    \item{\code{value}}{[\code{character}]\cr
#'    The value of the parameter.}
#'    \item{\code{component}}{[\code{character}]\cr
#'    The implementation name of a component, if the parameter belongs to this component. 
#'    This name must match a component of the implementation.}
#'  }
#' @name OpenMLRunParameter
#' @rdname OpenMLRunParameter
#' @aliases OpenMLRunParameter-class
#' @exportClass OpenMLRunParameter

setClass("OpenMLRunParameter", representation(
  name = "character",
  value = "character",
  component = "character"
))

# ***** Constructor *****
OpenMLRunParameter <- function(name, value, component = character(0)) {
  new("OpenMLRunParameter", 
    name = name,
    value = value,
    component = component
  )
}

# ***** Methods *****

# show
setMethod("show", "OpenMLRunParameter", function(object) {
  s <- if(length(object@component))
    sprintf(' (parameter of component %s)', object@component)
  else
    ""
  # FIXME does this work for arbitary values? unit test this
  catf("%s %s = %s", s, object@name, object@value)
})
