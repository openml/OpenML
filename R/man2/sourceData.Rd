\name{sourceData}
\alias{sourceData}

\title{
Obtain the source data associated with an OpenML task
}
\description{
  This function returns a data frame containing the source data
  associated with an OpenML task.
}
\usage{
sourceData(obj)
}

\arguments{
  \item{obj}{
    A \code{OpenMLTask} object containing the information of the OpenML task.
  }
}

\value{
  A data frame containing the source data.
}

\seealso{
\code{\link{expSettings}}, \code{\link{evaluationMeasures}}, \code{\link{targetFeature}}
}
\examples{
\dontrun{
## Downloading an OpenML task using its id
task <- downloadTask(123)
## Checking the task type
sourceData(task)
}

}
% Add one or more standard keywords, see file 'KEYWORDS' in the
% R documentation directory.
\keyword{ models }

