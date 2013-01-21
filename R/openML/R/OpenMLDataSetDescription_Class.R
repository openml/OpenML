################################################################# 
# THIS FILE DEFINES CLASS OpenMLDataSetDescription AND THE      #
# RESPECTIVE METHODS                                            #
#################################################################
# Authors : L. Torgo, B. Bischl and P. Branco   Date: Jan 2013  #
# License: GPL (>= 2)                                           #
#################################################################




# ==============================================================
# CLASS DEFINITION
# ==============================================================
# Luis Torgo, Bernd Bischl and Paula Branco, Jan 2013
# ==============================================================

setClass("OpenMLDataSetDescription",
         representation(id="numeric",
                        name="character",
                        version="character",
                        creator="character",
                        contributor="character",
                        date="Date",
                        description="character",
                        language="character",
                        format="character",
                        license="character",
                        url="character",
                        row.id.attribute="character",
                        md5.checksum="character",
                        data.set="data.frame"
                        ))



## ============================================================
## CONSTRUCTOR FUNCTION
## ============================================================
OpenMLDataSetDescription <- function(id,
                          name,version,
                          creator,contributor="",date,
                          description,language="",format,license="",
                          url,row.id.attribute="",md5.checksum="", data.set)
{
  new("OpenMLDataSetDescription",
      id=id,name=name,version=version,
      creator=creator,contributor=contributor,date=as.Date(date),
      description=description,language=language,format=format,
      license=license,url=url,
      row.id.attribute=row.id.attribute,md5.checksum=md5.checksum, data.set=data.set
      )
}



## ============================================================
## METHODS
## ============================================================


## ------------------------------------------------------------
## show method

setMethod("show","OpenMLDataSetDescription",
          function(object) {
            cat('\nDataset :: ',object@name,
                ' (openML ID = ',object@id,
                ', version = ',object@version,')\n')
            cat('\tCreator     : ',object@creator,'\n')
            if (object@contributor != '')
              cat('\tContributor : ',object@contributor,'\n')
            cat('\tDate        : ',object@date,'\n')
            if (object@license != '')
              cat('\tLicense     : ',object@license,'\n')
            cat('\tURL         : ',object@url,'\n')
            if (object@language != '')
              cat('\tLanguage    : ',object@language,'\n')
            cat('\tFormat      : ',object@format,'\n')
            if (object@row.id.attribute != '')
              cat('\tRow Id Attr.  = ',object@row.id.attribute,'\n')
            if (object@md5.checksum != '')
              cat('\tmd5 Check Sum = ',object@md5.checksum,'\n')
            cat('\tDescription :\n')
            cat(unlist(strsplit(object@description, split=" ")), fill=40)
            cat('\n')
            
          }
          )


