\name{OpenMLExpSetts-class}
\Rdversion{1.1}
\docType{class}
\alias{OpenMLExpSetts}
\alias{OpenMLExpSetts-class}
\alias{show,OpenMLExpSetts-method}

\title{Class "OpenMLExpSetts" }

\description{
  This class of objects contains the information on an experimental
  method.
}

\section{Objects from the Class}{
  Objects can be created by calls of the form \code{OpenMLExpSetts(...)}.
  The objects contain information on ... .
}
\section{Slots}{
  \describe{
    \item{\code{type}:}{Object of class \code{character} with the type
  of evaluation method.
    }
    \item{\code{n.folds}:}{Object of class \code{numeric} with the
  number of folds of the evaluation procedure (for most methods this is
  1, but for cross validation this will typically be a larger number).
    }
    \item{\code{n.repeats}:}{Object of class \code{numeric} with the
  number of times the experimental procedure is to be repeated with
  different randomization.
    }
    \item{\code{data.splits}:}{Object of class \code{data.frame}
  containing the actual data splits to be used on each fold and each
  repetition. This is a data frame with columns: \code{type} with
  possible values \code{TRAIN} or \code{TEST}; \code{rowid}; \code{fold}
  ; and \code{repeat}
    }
  }
}


\section{Methods}{
  \describe{
    \item{show}{\code{signature(object = "OpenMLExpSetts")}: method used to
      show the contents of a OpenMLExpSetts object.  }
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
showClass("OpenMLExpSetts")
}
\keyword{classes}
