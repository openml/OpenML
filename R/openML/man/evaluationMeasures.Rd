\name{evaluationMeasures}
\alias{evaluationMeasures}

\title{
Obtain the evaluation measures to estimate on a task.
}
\description{
  This function returns a character vector with the name of the
  evaluation measures that are defined for a given OpenML task.
}
\usage{
evaluationMeasures(obj)
}

\arguments{
  \item{obj}{
    A \code{OpenMLTask} object containing the information of the OpenML task.
  }
}

\value{
  A character vector with the names of the evaluation measures.
}

\seealso{
\code{\link{expSettings}}, \code{\link{taskType}}, \code{\link{targetFeature}}
}
\examples{
\dontrun{
## Downloading an OpenML task using its id
task <- downloadTask(123)
## Checking the task type
evaluationMeasures(task)
}

}
% Add one or more standard keywords, see file 'KEYWORDS' in the
% R documentation directory.
\keyword{ models }

