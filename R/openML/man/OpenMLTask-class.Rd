\name{OpenMLTask-class}
\Rdversion{1.1}
\docType{class}
\alias{OpenMLTask}
\alias{OpenMLTask-class}
\alias{show,OpenMLTask-method}

\title{Class "OpenMLTask" }

\description{
  This class of objects contains the information describing a openML task.
}

\section{Objects from the Class}{
  Objects can be created by calls of the form \code{OpenMLTask(...)}.
  The objects contain information on ... .
}
\section{Slots}{
  \describe{
    \item{\code{task.id}:}{Object of class \code{numeric} with the
      openML ID of the task.
    }
    \item{\code{task.type}:}{Object of class \code{character} with the
      type of task.
    }
    \item{\code{task.pars}:}{Object of class \code{list} with a set of
      parameters specific to the task type.
    }
    \item{\code{task.data.set}:}{Object of class union \code{OptionalOpenMLDataSet}
      containing either an object of class \code{OpenMLDataSet} with the
  data set associated with the task, or the value \code{NULL}.
    }
    \item{\code{task.data.splits}:}{Object of class \code{data.frame}
      containing the splits of the data set for the task.
    }
    \item{\code{task.preds}:}{Object of class \code{list}
      containing information on the format of the predictions for the
      particular task.
    }
  }
}


\section{Methods}{
  \describe{
    \item{show}{\code{signature(object = "OpenMLTask")}: method used to
      show the contents of a OpenMLTask object.  }
  }
}

\references{
  \url{http://www.dcc.fc.up.pt/~ltorgo/openML}  
}
\author{ Luis Torgo \email{ltorgo@dcc.fc.up.pt}, Bernd Bischl \email{}
  and Paula Branco \email{paobranco@gmail.com} }


\seealso{
  \code{\linkS4class{OpenMLDataSetDescription}}
}

\examples{
showClass("OpenMLTask")
}
\keyword{classes}
