\name{downloadOpenMLTask}
\alias{downloadOpenMLTask}

\title{
  Download an OpenML task from the repository.
}
\description{
 This function downloads an OpenML task and all associated files from the OpenML repository,
 intermediatly stores them on disk and creates an S4 object which completely specifies the task.
}

\usage{
downloadOpenMLTask(id, dir)
}

\arguments{
  \item{id}{
    ID number of task on OpenML server, used to retrieve the task.
  }
  \item{dir}{
    Directory where downloaded files from the server are stored. 
    Default is \code{tempdir()}.
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

