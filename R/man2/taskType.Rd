\name{taskType}
\alias{taskType}

\title{
Obtain the name of the OpenML task
}
\description{
  This function returns a character string with the name of the type of
  task in an OpenML task.
}
\usage{
taskType(obj)
}

\arguments{
  \item{obj}{
    A \code{OpenMLTask} object containing the information of the OpenML task.
  }
}

\value{
  A character string with the name of the task type.
}

\seealso{
\code{\link{expSettings}}, \code{\link{evaluationMeasures}}, \code{\link{targetFeature}}
}
\examples{
\dontrun{
## Downloading an OpenML task using its id
task <- downloadTask(123)
## Checking the task type
taskType(task)
}

}
% Add one or more standard keywords, see file 'KEYWORDS' in the
% R documentation directory.
\keyword{ models }

