\name{downloadOpenMLTask}
\alias{downloadOpenMLTask}

\title{
  Download a task from the OpenML repository.
}
\description{
 This function downloads an OpenML task and all associated files from the OpenML repository,
 intermediately store the files on disk and creates an S4 object which completely specifies the task.
}

\details{
 Usually there is no reason to set the \code{fetch.*} arguments to \code{FALSE}, as you want all information
 completely encapsulated in the task object.
}

\usage{
  downloadOpenMLTask(id, dir = tempdir(), clean.up = TRUE, fetch.data.set.description = TRUE, fetch.data.set = TRUE, fetch.data.splits = TRUE)
}

\arguments{
  \item{id}{
    ID number of task on OpenML server, used to retrieve the task.
  }
  \item{dir}{
    Directory where downloaded files from the repository are stored. 
  }
  \item{clean.up}{
    Should the downloaded files be removed from disk at the end?
  }
  \item{fetch.data.set.description}{
    Should the data set description also be downloaded? 
  }
  \item{fetch.data.set}{
    Should the data set also be downloaded? 
  }
  \item{fetch.data.splits}{
    Should the data splits (for resampling) also be downloaded? 
  }
}

\value{
  \code{\linkS4class{OpenMLTask}} object.
}

\seealso{
\code{\link{taskType}}, \code{\link{targetFeature}}, \code{\link{expSettings}}, \code{\link{evaluationMeasures}}
}

\examples{
  \dontrun{
  ## Download task and access relevant information to start running experiments
  task <- downloadOpenMLTask(id = 1)
  task
  taskType(task)
  targetFeature(task)
  evaluationMeasures(task)
  expSettings(task)
  head(expSettings@data.splits(task))
  }
}

