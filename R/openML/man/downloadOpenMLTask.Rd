\name{downloadOpenMLTask}
\alias{downloadOpenMLTask}

\title{
  Download an OpenML task from the repository.
}
\description{
 This function downloads an OpenML task and all associated files from the OpenML repository,
 intermediately stores them on disk and creates an S4 object which completely specifies the task.
}

\usage{
  downloadOpenMLTask(id, dir = tempdir(), clean.up = TRUE, fetch.data.set.description = TRUE, fetch.data.set = TRUE, fetch.data.splits = TRUE)
}

\arguments{
  \item{id}{
    ID number of task on OpenML server, used to retrieve the task.
  }
  \item{dir}{
    Directory where downloaded files from the reposity are stored. 
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
  \code{OpenMLTask} object.
}

\examples{
  \dontrun{
  downloadOpenMLTask(id = 1)
  }
}

