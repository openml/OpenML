\name{expSettings}
\alias{expSettings}

\title{
Obtain the experimental method and its settings of an OpenML task
}
\description{
  This function returns an object with the experimental methodology and
  its respective settings associates with and OpenML task.
}
\usage{
expSettings(obj)
}

\arguments{
  \item{obj}{
    A \code{OpenMLTask} object containing the information of the OpenML task.
  }
}

\value{
  An object of class \code{OpenMLExpSettings} containing the information
  on the experimental method and respective settings.
}

\seealso{
\code{\link{taskType}}, \code{\link{evaluationMeasures}}, \code{\link{targetFeature}}
}
\examples{
\dontrun{
## Downloading an OpenML task using its id
task <- downloadTask(123)
## Checking the task type
expSettings(task)
}

}
% Add one or more standard keywords, see file 'KEYWORDS' in the
% R documentation directory.
\keyword{ models }

