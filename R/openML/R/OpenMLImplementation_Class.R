################################################################# 
# THIS FILE DEFINES CLASS OpenMLImplementation AND THE          #
# RESPECTIVE METHODS                                            #
#
#################################################################
# Authors : L. Torgo, B. Bischl and P. Branco   Date: Jan 2013  #
# License: GPL (>= 2)                                           #
#################################################################


# ==============================================================
# CLASS: ImplementationParameter
# ==============================================================
# Luis Torgo, Bernd Bischl and Paula Branco, Jan 2013
# ==============================================================


# --------------------------------------------------------------
# class def
setClass("ImplementationParameter",
         representation(
                        name="character",
                        data.type="character",
                        default.value="character",
                        description="character"
                       )
           )

ImplementationParameter <- function(name,
                                    data.type="",
                                    default.value="",
                                    description="")
  new("ImplementationParameter",
      name=name,data.type=data.type,
      default.value=default.value,description=description)


# show
setMethod("show","ImplementationParameter",
          function(object) {
            cat(object@name)
            if (object@data.type != "") cat(' : ',object@data.type)
            if (object@default.value != "") cat(' (default value = ',object@default.value,' )')
            if (object@description != "") cat('\n   Description : ',object@description)
            cat('\n')
          })

# ==============================================================
# CLASS: OpenMLImplementation
# ==============================================================
# Luis Torgo, Bernd Bischl and Paula Branco, Jan 2013
# ==============================================================


# --------------------------------------------------------------
# class def
setClass("OpenMLImplementation",
         representation(id="character",
                        name="character",
                        version="character",
                        creator="character",
                        contributor="character",
                        date="character",
                        licence="character",
                        language="character",
                        description="character",
                        full.description="character",
                        installation.notes="character",
                        dependencies="character",
                        programming.language="character",
                        operating.system="character",
                        bib.citation="character",
                        bib.url="character",
                        implements="character",
                        parameter="list",
                        components="list",
                        source.format="character",
                        binary.format="character",
                        source.md5="character",
                        binary.md5="character"
                        )
           )


# --------------------------------------------------------------
# constructor function
OpenMLImplementation <- function(id="",
                                 name="",
                                 version="",
                                 creator=character(),
                                 contributor=character(),
                                 date=as.character(Sys.time()),
                                 licence="",
                                 language="English",
                                 description,
                                 full.description="",
                                 installation.notes="",
                                 dependencies="",
                                 programming.language="R",
                                 operating.system=R.version$os,
                                 bib.citation="",
                                 bib.url="",
                                 implements="",
                                 parameter=list(),
                                 components=list(),
                                 source.format="R script",
                                 binary.format="",
                                 source.md5="",
                                 binary.md5=""
                                 )
{
  new("OpenMLImplementation",
      id=id,
      name=name,
      version=version,
      creator=creator,
      contributor=contributor,
      date=date,
      licence=licence,
      language=language,
      description=description,
      full.description=full.description,
      installation.notes=installation.notes,
      dependencies=dependencies,
      programming.language=programming.language,
      operating.system=operating.system,
      bib.citation=bib.citation,
      bib.url=bib.url,
      implements=implements,
      parameter=parameter,
      components=components,
      source.format=source.format,
      binary.format=binary.format,
      source.md5=source.md5,
      binary.md5=binary.md5
      )
}



# --------------------------------------------------------------
# Methods:


# show
# Note: The data splits and the predictions are not shown
setMethod("show","OpenMLImplementation",
          function(object) {
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
          }
          )



# --------------------------------------------------------------
# Accessor functions
