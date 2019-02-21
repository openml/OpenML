# Benchmarking suites

OpenML offers <b>benchmarking suites</b>: curated, comprehensive sets of machine learning datasets,
covering a wide spectrum of domains and statistical properties. This makes benchmarking results more comparable,
more interpretable, and allows more standardized analysis of algorithms under different conditions.

Benchmarking suites make benchmarking a whole lot easier:
- all datasets are uniformly formatted in standardized data formats
- they can be easily downloaded programmatically through [APIs and client libraries](APIs)
- they come with machine-readable [meta-information](https://www.openml.org/search?type=measure&q=+measure_type%3Adata_quality), such as the occurrence of missing values, to train algorithms correctly
- standardized train-test splits are provided to make results more comparable
- previous benchmarks (run by others) can be easily downloaded and reused
- new benchmarking results can be shared in a reproducible way through the [APIs](APIs)

## Using OpenML Benchmark Suites
Below are detailed instructions for common use cases, as well as code examples. These illustrations use the reference 'OpenML-CC18' benchmark suite, but you can replace this with any other benchmark suite. In all examples, OpenML does not only return the datasets, but also the train-test splits and (for predictive tasks) the target feature. These are enveloped as _tasks_ in OpenML. Hence, you will usually receive a list of tasks in which you can find the data itself.

**Additional resources:**  
Additional scripts and notebooks can be found in the associated [GitHub repo](https://github.com/openml/benchmark-suites)

### Listing the benchmark suites
For now, these are explicitly listed below. We will add them to the OpenML search engine soon.
Each benchmarking suite points to an OpenML study with the exact list of datasets and tasks.  
**TODO: List all studies with type=suite /study/list/type/suite (future work)**  


### Fetching the datasets
You can fetch and iterate through the benchmark datasets with a few lines of code. See the code examples below.

Via the REST API, a list of all tasks and dataset IDs is returned
!!! example
    ``` REST
    https://www.openml.org/api/v1/study/name/OpenML-CC18
    ```

In Python, the data is returned as X, y numpy arrays:  
!!! example
    ``` python
    import openml
    benchmark_suite = openml.study.get_study('OpenML-CC18','tasks') # obtain the benchmark suite
    for task_id in benchmark_suite.tasks: # iterate over all tasks
      task = openml.tasks.get_task(task_id) # download the OpenML task
      X, y = task.get_X_and_y() # get the data
    ```

In Java, the data is returned as a WEKA Instances object:  
!!! example
    ``` java
    OpenmlConnector openml = new OpenmlConnector();
    Study benchmarksuite = openml.studyGet("OpenML-CC18", "tasks");
    for (Integer taskId : benchmarksuite.getTasks()) { // iterate over all tasks
      Task t = openml.taskGet(taskId); // download the OpenML task
      Instances d = InstancesHelper.getDatasetFromTask(openml, t); // obtain the dataset
    ```
The Java implementation automatically uploads results to the server.  

In R, the data is returned as an R dataframe:  
!!! example
    ``` r
    library(OpenML)
    task.ids = getOMLStudy('OpenML-CC18')$tasks$task.id # obtain the list of suggested tasks
    for (task.id in task.ids) { # iterate over all tasks
      task = getOMLTask(task.id) # download single OML task
      data = as.data.frame(task) # obtain raw data set
    ```

### Running and sharing benchmarks
The code below demonstrates how OpenML benchmarking suites can be conveniently imported for benchmarking using the Python, Java and R APIs.

The OpenML-CC18 tasks are downloaded through the [study with the same name](https://www.openml.org/s/99),
which contains all tasks and also holds all benchmarking results obtained on them. The code also shows how to access the raw data set (although this is not needed to train a model), fit a simple classifier on the defined data splits, and finally publish runs on the OpenML server.

!!! example
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
    ``` example
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
    ``` example
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
**TODO: Add API call for listing all runs of a study /study/name/<studyname>**    
**TODO: Add Python code for listing all runs of a study**  
**TODO: Add R code for listing all runs of a study**  
**TODO: Add Java code for listing all runs of a study**  


## Creating new benchmark suites
You can [upload more datasets](https://www.openml.org/new/data) to OpenML, and create new benchmark suites out of them,
e.g., for regression and time-series data

This is done by:
* [Creating a new study](https://www.openml.org/new/study) with the name of the benchmark as the alias.
* Adding new tasks to the study by _tagging_ them with the name of the benchmark.

Step-by-steps:
- [Creating a new study](https://www.openml.org/new/study) with name and description
- Make the study private to avoid that other people add tasks **TODO: explain how**
- Indicate that this study is a benchmark suite by setting the "suite" flag  **TODO: explain how**
- Find the study tag study_X **TODO: explain how**
- Tag all tasks with study_X  **TODO: is this the correct way?**
- You do not have to tag any runs with study_X, these will be linked automatically **TODO: Is this the correct way?**

### Changing a benchmark suite
- If the study is private, only the owner can add or remove datasets (by adding or removing tags)
- You can create a new suite with a similar set of tasks if you want

## List of benchmarking suites

### OpenML-CC18
The [OpenML-CC18](https://www.openml.org/s/99) suite contains all OpenML datasets from mid-2018 that satisfy a large set of clear requirements for thorough yet practical benchmarking. It includes datasets frequently used in benchmarks published over the last years, so it can be used as a drop-in replacement for many benchmarking setups.

[List of datasets and properties](https://www.openml.org/search?q=tags.tag%3Astudy_99&type=data&table=1&size=73)

The suite is defined as the set of all verified OpenML datasets that satisfy the following requirements:  
* the number of observations are between 500 and 100000 to focus on medium-sized datasets, that are not too small and not too big,
* the number of features does not exceed 5000 features to keep the runtime of algorithms low,
* the target attribute has at least two classes
* have classes with less than 20 observations
* the ratio of the minority class and the majority class is above 0.05, to eliminate highly imbalanced datasets which require special treatment for both algorithms and evaluation measures.

We excluded datasets which:  
* are artificially generated (not to confuse with simulated)
* cannot be randomized via a 10-fold cross-validation due to grouped samples or because they are time series or data streams
* are a subset of a larger dataset
* have no source or reference available
* can be perfectly classified by a single attribute or a decision stump
* allow a decision tree to achieve 100% accuracy on a 10-fold cross-validation task
* have more than 5000 features after one-hot-encoding categorical features
* are created by binarization of regression tasks or multiclass classification tasks, or
* are sparse data (e.g., text mining data sets)

### OpenML100  
The [OpenML100](https://www.openml.org/s/14) was a predecessor of the OpenML-CC18, consisting of [100 classification datasets](https://www.openml.org/search?q=tags.tag%3AOpenML100&type=data&table=1&size=100)</a>. We recommend that you use the **OpenML-CC18** instead, because the OpenML100 suffers from some teething issues in the design of benchmark suites. For instance, it contains several datasets that are too easy to model with today's machine learning algorithms, as well as datasets that represent time series analysis problems. These do not invalidate benchmarks run on the OpenML100, but may obfuscate the interpretation of results. The 'OpenML-CC18' handle is also more descriptive and allows easier versioning.

[List of datasets and properties](https://www.openml.org/search?q=tags.tag%3Astudy_14&type=data&table=1&size=100)


For reference, the OpenML100 included datasets satisfying the following requirements:  

* the number of observations are between 500 and 100000 to focus on medium-sized datasets, that are not too small for proper training and not too big for practical experimentation
* the number of features does not exceed 5000 features to keep the runtime of algorithms low
* the target attribute has at least two classes
* he ratio of the minority class and the majority class is above 0.05 to eliminate highly imbalanced datasets that would obfuscate a clear analysis

It excluded datasets which:  

* cannot be randomized via a 10-fold cross-validation due to grouped samples
* have an unknown origin or no clearly defined task
* are variants of other datasets (e.g. binarized regression tasks)
* include sparse data (e.g., text mining data sets)
