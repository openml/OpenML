# Benchmarking suites

OpenML offers <b>benchmarking suites</b>: curated, comprehensive sets of machine learning datasets,
covering a wide spectrum of domains and statistical properties and standardized evaluation procedures (defined in tasks).
This makes benchmarking results more comparable, more complete, and allows more standardized analysis of algorithms under different conditions.

This significantly simplifies and standardizes benchmarking by:
- offering a large set of datasets in standardized data formats
- easily download through a wide range of [APIs](APIs) and existing client libraries
- machine-readable meta-information on all the datasets
- sharing of benchmarking results in a reproducible way through the [APIs](APIs)

## Using OpenML Benchmark Suites
Below are detailed instructions for common use cases, as well as code examples. These illustrations are done using the reference 'OpenML-CC18' benchmark suite, but you can simply replace this with any other benchmark suite. In all examples, OpenML does not only return the datasets, but also the train-test splits and (for predictive tasks) the target feature. These are enveloped as _tasks_ in OpenML. Hence, you will usually receive a list of tasks in which you can find the data itself. 

### Listing and viewing the benchmark suites online
For now, these are explictly listed below. We will add them to the OpenML search engine soon.
Each benchmarking suite points to an OpenML study with the exact list of datasets and tasks.

Via the REST API:
- Get study by name (suite name)
- Get all tasks in that study by API call /study/name/<studyname> --> list of task ids
- List all studies with type=suite /study/list/type/suite (future work)

### Fetching the datasets programmatically
You can fetch and iterate through the benchmark datasets with a few lines of code. See the code examples below.

In Python, the data is returned as X, y numpy arrays:
!!! Python
  ``` python
  import openml
  benchmark_suite = openml.study.get_study('OpenML-CC18','tasks') # obtain the benchmark suite
  for task_id in benchmark_suite.tasks: # iterate over all tasks
    task = openml.tasks.get_task(task_id) # download the OpenML task
    X, y = task.get_X_and_y() # get the data
  ```

In Java, the data is returned as a WEKA Instances object:
!!! Java
  ``` java
  OpenmlConnector openml = new OpenmlConnector();
  Study benchmarksuite = openml.studyGet("OpenML-CC18", "tasks");
  for (Integer taskId : benchmarksuite.getTasks()) { // iterate over all tasks
    Task t = openml.taskGet(taskId); // download the OpenML task
    Instances d = InstancesHelper.getDatasetFromTask(openml, t); // obtain the dataset
  ```
 
In R, the data is returned as an R dataframe:
!!! R (with mlr)
  ``` r
  library(OpenML)
  task.ids = getOMLStudy('OpenML-CC18')$tasks$task.id # obtain the list of suggested tasks
  for (task.id in task.ids) { # iterate over all tasks
    task = getOMLTask(task.id) # download single OML task
    data = as.data.frame(task) # obtain raw data set
  ```

### Running and sharing benchmarks
The code below demonstrates how OpenML benchmarking suites can be conveniently imported for benchmarking using the Python, Java and R APIs.

The OpenML100 tasks are downloaded through the <a href="https://www.openml.org/s/14" target="_blank">study with the same name</a>,
which contains the 100 tasks and also holds all benchmarking results obtained on them. The code also shows how to access the raw data set (although this is not needed to train a model),
fit a simple classifier on the defined data splits, and finally publish runs on the OpenML server.

!!! Python (with scikit-learn) 
  ``` python
  import openml
  import sklearn
  benchmark_suite = openml.study.get_study('OpenML-CC18','tasks') # obtain the benchmark suite
  # build a sklearn classifier
  clf = sklearn.pipeline.make_pipeline(sklearn.preprocessing.Imputer(),
                                       sklearn.tree.DecisionTreeClassifier())
  for task_id in benchmark_suite.tasks: # iterate over all tasks
    task = openml.tasks.get_task(task_id) # download the OpenML task
    X, y = task.get_X_and_y() # get the data (not used in this example)
    openml.config.apikey = 'FILL_IN_OPENML_API_KEY' # set the OpenML Api Key
    # run classifier on splits (requires API key)
    run = openml.runs.run_model_on_task(task,clf)
    score = run.get_metric_score(sklearn.metrics.accuracy_score) # print accuracy score
    print('Data set: %s; Accuracy: %0.2f' % (task.get_dataset().name,score.mean()))
    run.publish() # publish the experiment on OpenML (optional)
    print('URL for run: %s/run/%d' %(openml.config.server,run.run_id))
    ```

!!! Java (with WEKA)
  ``` java
  public static void runTasksAndUpload() throws Exception {
    OpenmlConnector openml = new OpenmlConnector();
    // obtain the benchmark suite
    Study benchmarksuite = openml.studyGet("OpenML-CC18", "tasks");
    Classifier tree = new REPTree(); // build a Weka classifier
    for (Integer taskId : benchmarksuite.getTasks()) { // iterate over all tasks
      Task t = openml.taskGet(taskId); // download the OpenML task
      Instances d = InstancesHelper.getDatasetFromTask(openml, t); // obtain the dataset
      openml.setApiKey("FILL_IN_OPENML_API_KEY");
      int runId = RunOpenmlJob.executeTask(openml, new WekaConfig(), taskId, tree);
      Run run = openml.runGet(runId);   // retrieve the uploaded run
    }
  }
  ```

!!! R (with mlr)
  ``` r
  library(OpenML)
  lrn = makeLearner('classif.rpart') # construct a simple CART classifier
  task.ids = getOMLStudy('OpenML-CC18')$tasks$task.id # obtain the list of suggested tasks
  for (task.id in task.ids) { # iterate over all tasks
    task = getOMLTask(task.id) # download single OML task
    data = as.data.frame(task) # obtain raw data set
    run = runTaskMlr(task, learner = lrn) # run constructed learner
    setOMLConfig(apikey = 'FILL_IN_OPENML_API_KEY')
    upload = uploadOMLRun(run) # upload and tag the run
  }
```

### Retrieving all runs on a benchmarking suites:
- API call /study/name/<studyname> --> list of run ids
  
## Creating new benchmark suites
The set of datasets on OpenML.org can <a href="https://www.openml.org/new/data">easily be extended</a>, and additional OpenML benchmark suites,
e.g., for regression and time-series data, can easily be created by defining sets of datasets according to specific needs.
This is done by:
<ul>
<li><a href="new/study">Creating a new study</a> with the name of the benchmark as the alias.</li>
<li>Adding new tasks to the study by <i>tagging</i> them with the name of the benchmark.</li>
</ul>

Detailed steps:
- Create a new study with name and description
- Set flag to "private" - we dont want people adding tasks here
- Set "suite" flag
- Find the study tag study_X
- Tag all tasks with study_X
- You can add other tags as well. For each you can set:
    - User IDs (who can extend study with that tag)
    - Beginning/end dates (only stuff tagged within those dates are part of the study)
- You do not have to tag any runs with study_X, these will be linked automatically
  
### Changing a benchmark suite
- If the study is private, only the owner can do that
- You can create a new suite with a similar set of tasks if you want


## Existing suites

### OpenML-CC18
https://www.openml.org/s/99

### OpenML100  
https://www.openml.org/s/14

The OpenML100 was a predecessor of the OpenML-CC18, consisting of <a href="https://www.openml.org/search?q=tags.tag%3AOpenML100&type=data&table=1&size=100" target="_blank">100 classification datasets</a>. We recommend that you use the OpenML-CC18 instead, because the OpenML100 suffers from some teething issues in the design of benchmark suites. For instance, it contains several datasets that are too easy to model with today's machine learning algorithms, as well as datasets that represent time series analysis problems. These do not invalidate benchmarks run on the OpenML100, but may obfuscate the interpretation of results. The 'OpenML-CC18' handle is also more descriptive and allows easier versioning.

For reference, the OpenML100 included datasets satisfying the following requirements:
<ul><li>the number of observations are between 500 and 100000 to focus on medium-sized datasets, that are not too small for proper training and not too big for practical experimentation,
</li><li>the number of features does not exceed 5000 features to keep the runtime of algorithms low
</li><li>the target attribute has at least two classes
</li><li>the ratio of the minority class and the majority class is above 0.05 to eliminate highly imbalanced datasets that would obfuscate a clear analysis.
</li></ul>

It excluded datasets which:
<ul><li>cannot be randomized via a 10-fold cross-validation due to grouped samples,
</li><li>have an unknown origin or no clearly defined task,
</li><li>are variants of other datasets (e.g. binarized regression tasks),
</li><li>include sparse data (e.g., text mining data sets).
</li></ul>
