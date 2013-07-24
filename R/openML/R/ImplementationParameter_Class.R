setClass("ImplementationParameter", representation(
  name = "character",
  data.type = "character",
  default.value = "character",
  description = "character"
))

ImplementationParameter <- function(name, data.type = "", default.value = "", description = "") {
  new("ImplementationParameter", 
    name = name,
    data.type = data.type,
    default.value = default.value,
    description = description
 )
}

# show
setMethod("show", "ImplementationParameter", function(object) {
  cat(object@name)
  if (object@data.type != "") cat(' : ',object@data.type)
  if (object@default.value != "") cat(' (default value = ',object@default.value,' )')
  if (object@description != "") cat('\n   Description : ',object@description)
  cat('\n')
})
