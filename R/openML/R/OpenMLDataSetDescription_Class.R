#' OpenMLDataSetDescription
#'
#' This class of objects contains the information describing an openML data set.
#' 
#' Objects can be created by calls of the form \code{OpenMLDataSetDescription(...)}.
#' The objects contain information on ... .
#'
#'@section Slots: 
#'  \describe{
#'    \item{\code{id}}{[\code{integer(1)}]\cr
#'    The OpenML ID number of the data set.}
#'    \item{\code{name}}{[\code{character}]\cr 
#'    The name of the data set.}
#'    \item{\code{version}}{[\code{character}]\cr 
#'    The version of the data set.}
#'    \item{\code{creator}}{[\code{character}]\cr 
#'    The creator's name.}
#'    \item{\code{contributor}}{[\code{character}]\cr 
#'    The contributor's name}
#'    \item{\code{collection.date}}{[\code{POSIXt}]\cr 
#'    The date and the time when the data set has been collected.}
#'    \item{\code{upload.date}}{[\code{POSIXt}]\cr 
#'    The date and the time when the data set has been uploaded.}
#'    \item{\code{description}}{[\code{character}]\cr 
#'    The description of the data set.}
#'    \item{\code{language}}{[\code{character}]\cr 
#'    The language.}
#'    \item{\code{format}}{[\code{character}]\cr 
#'    The format of the data set.}
#'    \item{\code{licence}}{[\code{character}]\cr 
#'    The licence of the data set.}
#'    \item{\code{url}}{[\code{character}]\cr 
#'    The url of the data set.}
#'    \item{\code{row.id.attribute}}{[\code{character}]\cr 
#'    The row id attribute.}
#'    \item{\code{md5.checksum}}{[\code{character}]\cr 
#'    The md5 checksum}
#'    \item{\code{data.set}}{[\code{character}]\cr 
#'    The data set.}
#'  }
#'
#' @name OpenMLDataSetDescription 
#' @rdname OpenMLDataSetDescription
#' @aliases OpenMLDataSetDescription-class
#' @exportClass OpenMLDataSetDescription

#FIXME:  parse date as Date and change slot type
#FIXME:  check that slots are in same order (here and other classes) as in XSD
#FIXME:  upload.date is of class POSIXt, but collection.date is of class character
setClass("OpenMLDataSetDescription", representation(
  id = "numeric",
  name = "character",
  version = "character",
  creator = "character",
  contributor = "character",
  collection.date = "character",
  upload.date = "POSIXt",
  description = "character",
  language = "character",
  format = "character",
  licence = "character",
  url = "character",
  row.id.attribute = "character",
  md5.checksum = "character",
  data.set = "data.frame"
))

#' OptionalOpenMLDataSetDescription
#'
#' Either an object of class \code{\link{OpenMLDataSetDescription}} or \code{NULL}.
#' 
#' @seealso \code{\link{OpenMLDataSetDescription}}, \code{\link{OpenMLTask}}
#' @name OptionalOpenMLDataSetDescription
#' @rdname OptionalOpenMLDataSetDescription
#' @aliases OptionalOpenMLDataSetDescription-class
#' @exportClass OptionalOpenMLDataSetDescription

setClassUnion("OptionalOpenMLDataSetDescription",
              c("OpenMLDataSetDescription","NULL"))

# ***** Constructor *****
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

# ***** Methods *****

setMethod("show", "OpenMLDataSetDescription",	function(object) {
  # incorrect indentation to see aligment!
	catf('\nDataset %s :: (openML ID = %i, version = %s)', object@name, object@id, object@version)
	catf('\tCreator          : %s', object@creator)
	if (length(object@contributor) > 0)
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
	catf(printStrToChar(object@data.set))
})

