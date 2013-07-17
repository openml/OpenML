\name{targetFeature}
\alias{targetFeature}

\title{
Obtain the name of the target feature of a OpenML task
}
\description{
  This function returns a character string with the name of the target
  feature associated with an OpenML task.
}
\usage{
targetFeature(obj)
}

\arguments{
  \item{obj}{
    A \code{OpenMLTask} object containing the information of the OpenML task.
  }
}

\value{
  A character string with the name of the target feature.
}

\seealso{
\code{\link{expSettings}}, \code{\link{evaluationMeasures}}, \code{\link{taskType}}
}
\examples{
\dontrun{
## Downloading an OpenML task using its id
task <- downloadTask(123)
## Checking the task type
targetFeature(task)
}

}
% Add one or more standard keywords, see file 'KEYWORDS' in the
% R documentation directory.
\keyword{ models }

