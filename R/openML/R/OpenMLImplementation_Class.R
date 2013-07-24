
# --------------------------------------------------------------
# class def
setClass("OpenMLImplementation", representation(
  id = "character",
  name = "character",
  version = "character",
  description = "character",
  creator = "character",
  contributor = "character",
  licence = "character",
  language = "character",
  full.description = "character",
  date = "character",
  installation.notes = "character",
  dependencies = "character",
  #FIXME add bib ref
  #bibliographical.reference = "character",
  #FIXME add paramater
  #FIXME add components
#  parameter = "list",
#  components = "list",
  source.format = "character",
  binary.format = "character",
  source.md5 = "character",
  binary.md5 = "character"
  
#  programming.language = "character",
#  operating.system = "character",
#  bib.url = "character",
#  implements = "character",
))


# --------------------------------------------------------------
# constructor function
OpenMLImplementation <- function(
  id = "",
  name = "",
  version = "",
  description = "",
  creator = character(),
  contributor = character(),
  licence = "",
  language = "English",
  full.description = "",
  date = as.character(Sys.time()),
  installation.notes = "",
  dependencies = character(),
  source.format = "R",
  # FIXME: why do we specify this? We can see this from the 
  # user provided file anyway?
  binary.format = "zip",
  source.md5 = "",
  binary.md5 = ""
  #programming.language = "R",
  #operating.system = R.version$os,
  #bib.citation="",
  #bib.url = "",
  #implements = "",
  #parameter=list(),
  #components=list(),
) {
  new("OpenMLImplementation",
    id = id,
    name = name,
    version = version,
    description = description,
    creator = creator,
    contributor = contributor,
    licence = licence,
    language = language,
    full.description = full.description,
    date = date,
    installation.notes = installation.notes,
    dependencies = dependencies,
    source.format = source.format,
    binary.format = binary.format,
    source.md5 = source.md5,
    binary.md5 = binary.md5
#    programming.language = programming.language,
 #   operating.system = operating.system,
#    bib.citation = bib.citation,
#    bib.url = bib.url,
#    implements = implements,
#    parameter = parameter,
#    components = components,
  )
}

# ***** Methods *****

# show
setMethod("show", "OpenMLImplementation", function(object) {
  ## General implementation info
  cat('\n** Implementation Information ** \n')
  if (object@id != "") cat('ID      :: ',object@id,'\n')
  if (object@name != "") cat('Name    :: ',object@name,'\n')
  if (object@version != "") cat('Version :: ',object@version,'\n')
  
  ## Authors and contributors
  if (length(object@creator)) {
    cat('\nCreator(s) ::')
    for(i in 1:length(object@creator))
      cat(' ',object@creator[i])
  }
  if (length(object@contributor)) {
    cat('\nContributor(s) ::')
    for(i in 1:length(object@contributor))
      cat(' ',object@contributor[i])
  }
  
  ## Other info
  cat('\nDate :: ',object@date)
  if (object@licence != "") cat('\nLicence :: ',object@licence)
  if (object@language != "") cat('\nLanguage :: ',object@language)
  
  ## Implementation specific info
  cat('\n\nDescription of the implementation :\n')
  cat(object@description,'\n')
  if (object@full.description != "")
    cat('\nFull description :: \n',object@full.description)
  if (object@installation.notes != "")
    cat('\nInstallation notes :: \n',object@installation.notes)
  if (object@dependencies != "")
    cat('\nDependencies :: ',object@dependencies)
  if (object@programming.language != "")
    cat('\nProgramming language :: ',object@programming.language)
  if (object@operating.system != "")
    cat('\nOperating system :: ',object@operating.system)
  
  ## Bibliographic info
  if (object@bib.citation != "")
    cat('\nBibliographic citation :: ',object@bib.citation)
  if (object@bib.url != "")
    cat('\nBibliographic URL :: ',object@bib.url)
  
  ## More implementation details
  if (object@implements != "")
    cat('\nImplements :: ',object@implements)
  
  ## Implementation parameters
  if (length(object@parameter)) {
    cat('\nImplementation parameters :\n')
    for(i in 1:length(object@parameter)) print(object@parameter[i])
  }
  
  ## Implementation components
  if (length(object@components)) {
    cat('\nDescription of Implementation Components ::\n')
    for(i in 1:length(object@components)) print(object@components[i])
  }
  
  ## The implementation source information
  if (object@source.format != "")
    cat('\nSource format :: ',object@source.format)
  if (object@binary.format != "")
    cat('\nBinary format :: ',object@binary.format)
  if (object@source.md5 != "")
    cat('\nSource MD5 :: ',object@source.md5)
  if (object@binary.md5 != "")
    cat('\nBinary MD5 :: ',object@binary.md5)
  cat('\n\n')
})
  

