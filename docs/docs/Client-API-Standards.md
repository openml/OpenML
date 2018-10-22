This page defines a minimal standard to adhere in programming APIs.

## Configuration file ##

The configuration file resides in a directory `.openml` in the home directory of the user and is called config. It consists of `key = value` pairs which are seperated by newlines. The following keys are defined:

  * apikey:
    * required to access the server
  * server:
    * default: `http://www.openml.org`
  * verbosity:
    * 0: normal output
    * 1: info output
    * 2: debug output
  * cachedir:
    * if not given, will default to `file.path(tempdir(), "cache")`.
  * arff.reader:
    * `RWeka`: This is the standard Java parser used in Weka.
    * `farff`: The [farff package](http://www.github.com/mlr-org/farff) lives below the mlr-org and is a newer, faster parser without Java.

## Caching ##

### Cache invalidation ###

All parts of the entities which affect experiments are immutable. The entities dataset and task have a flag `status` which tells the user whether they can be used safely.

### File structure ###

Caching should be implemented for

  * datasets
  * tasks
  * splits
  * predictions

and further entities might follow in the future. The cache directory `$cache` should be specified by the user when invoking the API. The structure in the cache directory should be as following:

  * One directory for the following entities:
    * `$cache/datasets`
    * `$cache/tasks`
    * `$cache/runs`
  * For every dataset there is an extra directory for which the name is the dataset ID, e.g. `$cache/datasets/2` for the dataset anneal.ORIG
    * The dataset should be called `dataset.arff`
    * Every other file should be named by the API call which was used to obtain it. The XML returned by invoking `openml.data.qualities` should therefore be called qualities.xml.
  * For every task there is an extra directory for which the name is the task ID, e.g. `$cache/tasks/1`
    * The task file should be called `task.xml`.
    * The splits accompanying a task are stored in a file `datasplits.arff`.
  * For every run there is an extra directory for which the name is the run ID, e.g. `$cache/run/1`
    * The predictions should be called `predictions.arff`.