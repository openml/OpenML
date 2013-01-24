\name{OpenMLEstimationProcedure-class}
\Rdversion{1.1}
\docType{class}
\alias{OpenMLEstimationProcedure}
\alias{OpenMLEstimationProcedure-class}
\alias{show,OpenMLEstimationProcedure-method}

\title{Class "OpenMLEstimationProcedure" }

\description{
  This class of objects contains the information on an experimental
  method.
}

\section{Objects from the Class}{
  Objects can be created by calls of the form \code{OpenMLEstimationProcedure(...)}.
  The objects contain information on ... .
}
\section{Slots}{
  \describe{
    \item{\code{type}:}{Object of class \code{character} with the type
  of evaluation method.
    }
    \item{\code{data.splits.id}:}{Object of class \code{numeric} with the
  OpenML ID of the data splits associated with the estimation procedure.
    }
    \item{\code{data.splits}:}{Object of class \code{data.frame}
      containing the actual data splits to be used on each fold and each
      repetition. This is a data frame with columns: \code{type} with
      possible values \code{TRAIN} or \code{TEST}; \code{rowid}; \code{fold}
      ; and \code{repeat}
    }
    \item{\code{parameters}:}{Object of class \code{list} with a set of
      parameters associated with the estimation procedure.
    }
  }
}


\section{Methods}{
  \describe{
    \item{show}{\code{signature(object = "OpenMLEstimationProcedure")}: method used to
      show the contents of a OpenMLEstimationProcedure object.  }
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
showClass("OpenMLEstimationProcedure")
}
\keyword{classes}
