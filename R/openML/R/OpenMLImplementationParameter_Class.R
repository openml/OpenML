#' OpenMLImplementationParameter
#'
#' This class of objects contains information on an implementation parameter.
#' 
#' Objects can be created by calls of the form \code{OpenMLImplementationParameter(...)}.
#' The objects contain information on ... .
#'
#'@section Slots: 
#'  \describe{
#'    \item{\code{name}}{[\code{character}]\cr
#'    The name of the parameter.}
#'    \item{\code{data.type}}{[\code{character}]\cr
#'    The data type of the parameter. Should be either integer, numeric, string, vector, matrix, object.}
#'    \item{\code{default.value}}{[\code{character}]\cr
#'    The default value of the parameter. Optional, but highly encouraged.}
#'    \item{\code{description}}{[\code{character}]\cr
#'    A description of what this parameter does.}
#'  }
#' @name OpenMLImplementationParameter
#' @rdname OpenMLImplementationParameter
#' @aliases OpenMLImplementationParameter-class
#' @exportClass OpenMLImplementationParameter

setClass("OpenMLImplementationParameter", representation(
  name = "character",
  data.type = "character",
  default.value = "character",
  description = "character"
))

# ***** Constructor *****
OpenMLImplementationParameter <- function(
  name, 
  data.type = character(0), 
  default.value = character(0), 
  description = character(0)) {
  
  new("OpenMLImplementationParameter", 
      name = name,
      data.type = data.type,
      default.value = default.value,
      description = description
  )
}

# ***** Methods *****

# show
setMethod("show", "OpenMLImplementationParameter", function(object) {  
  catf("Parameter %s", object@name)  
  if(length(object@data.type))
    catf("  type    :: %s", object@data.type)
  if(length(object@default.value))
    catf("  default :: %s", object@default.value)
  if(length(object@description))
    catf("\n%s", object@description)
})
