\name{OpenMLDataSetDescription-class}
\Rdversion{1.1}
\docType{class}
\alias{OpenMLDataSetDescription}
\alias{OpenMLDataSetDescription-class}
\alias{show,OpenMLDataSetDescription-method}

\title{Class "OpenMLDataSetDescription" }

\description{
  This class of objects contains the information describing a openML Data Set.
}

\section{Objects from the Class}{
  Objects can be created by calls of the form \code{OpenMLDataSetDescription(...)}.
  The objects contain information on ... .
}
\section{Slots}{
  \describe{
    \item{\code{id}:}{Object of class \code{numeric} with the openML ID of the data set.
    }
    \item{\code{name}:}{Object of class \code{character} with the name of the data set.
    }
    \item{\code{version}:}{Object of class \code{character} with the version of the data set.
    }
    \item{\code{creator}:}{Object of class \code{character} containing the creator name.
    }
    \item{\code{contributor}:}{Object of class \code{list} containing the contributors names.
    }
    \item{\code{date}:}{Object of class \code{character} containing information on the date.
    }
    \item{\code{description}:}{Object of class \code{character} containing the data set description.
    }
    \item{\code{language}:}{Object of class \code{character} with the language.
    }
    \item{\code{format}:}{Object of class \code{character} with the data set format.
    }
    \item{\code{licence}:}{Object of class \code{character} with de license.
    }
    \item{\code{url}:}{Object of class \code{character} with de data set url.
    }
    \item{\code{row.id.attribute}:}{Object of class \code{character} containing the data set row id attribute.
    }
    \item{\code{md5.checksum}:}{Object of class \code{character} containing the md5 checksum.
    }
    \item{\code{data.set}:}{Object of class \code{data.frame} containing the data set.
    }
 
 
 
  }
}


\section{Methods}{
  \describe{
    \item{show}{\code{signature(object = "OpenMLDataSetDescription")}: method used to
      show the contents of a OpenMLDataSetDescription object.  }
  }
}

\references{
  \url{http://www.dcc.fc.up.pt/~ltorgo/openML}  
}
\author{ Luis Torgo \email{ltorgo@dcc.fc.up.pt}, Bernd Bischl \email{}
  and Paula Branco \email{paobranco@gmail.com} }


\seealso{
  \code{\linkS4class{OpenMLTask}}
}

\examples{
showClass("OpenMLDataSetDescription")
}
\keyword{classes}
