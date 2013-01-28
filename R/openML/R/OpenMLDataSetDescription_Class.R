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

#FIXME:  parse date as Date and change slot type
#FIXME:  check that slots are in same order (here and other classes) as in XSD

setClass("OpenMLDataSetDescription",
         representation(id="numeric",
                        name="character",
                        version="character",
                        creator="character",
                        contributor="character",
                        collection.date="character",
                        upload.date="POSIXt",
                        description="character",
                        language="character",
                        format="character",
                        licence="character",
                        url="character",
                        row.id.attribute="character",
                        md5.checksum="character",
                        data.set="data.frame"
                        ))

setClassUnion("OptionalOpenMLDataSetDescription",
              c("OpenMLDataSetDescription","NULL"))

## ============================================================
## CONSTRUCTOR FUNCTION
## ============================================================
OpenMLDataSetDescription <- function(id,
                                     name,version,
                                     creator,contributor="",
																		 collection.date="", upload.date,
                                     description,language="",format,licence="",
                                     url,row.id.attribute="",md5.checksum="",
                                     data.set)
{
  new("OpenMLDataSetDescription",
      id=id,name=name,version=version,
      creator=creator,contributor=contributor,
			collection.date=collection.date, upload.date=upload.date,
      description=description,language=language,format=format,
      licence=licence,url=url,
      row.id.attribute=row.id.attribute,md5.checksum=md5.checksum,
      data.set=data.set
      )
}



## ============================================================
## METHODS
## ============================================================


## ------------------------------------------------------------
## show method

setMethod("show", "OpenMLDataSetDescription",
	function(object) {
	  # incorrect indentation to see aligment!
		catf('\nDataset %s :: (openML ID = %i, version = %s)', object@name, object@id, object@version)
		catf('\tCreator          : %s', object@creator)
		if (object@contributor != '')
		catf('\tContributor      : %s', object@contributor)
		catf('\tCollection Date  : %s', object@collection.date)
		catf('\tUpload Date      : %s', object@upload.date)
		if (object@licence != '')
		catf('\tLicence          : %s', object@licence)
		catf('\tURL              : %s', object@url)
		if (object@language != '')
		catf('\tLanguage         : %s', object@language)
		catf('\tFormat           : %s', object@format)
		if (object@row.id.attribute != '')
		catf('\tRow Id Attr.  	 : %s', object@row.id.attribute)
		if (object@md5.checksum != '')
		catf('\tmd5 Check Sum    : %s', object@md5.checksum)
		catf('\tDescription :')
		cat(collapse(paste('\t\t', strwrap(object@description), '\n'), sep=''))
		cat('\n')
		catf('\tData :')
		x <- printToChar(str(object@data.set), collapse=NULL)
		catf('\t\t%s', x[-length(x)])
	}
)


