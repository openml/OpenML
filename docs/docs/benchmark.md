# Benchmarking suites

OpenML offers <b>benchmarking suites</b>: curated, comprehensive sets of machine learning datasets,
covering a wide spectrum of domains and statistical properties. This makes benchmarking results more comparable,
more interpretable, and allows more standardized analysis of algorithms under different conditions.

Benchmarking suites make benchmarking a whole lot easier:
- all datasets are uniformly formatted in standardized data formats
- they can be easily downloaded programmatically through [APIs and client libraries](APIs)
- they come with machine-readable [meta-information](https://www.openml.org/search?type=measure&q=+measure_type%3Adata_quality), such as the occurrence of missing values, to train algorithms correctly
- standardized train-test splits are provided to make results more comparable
- results can be shared in a reproducible way through the [APIs](APIs)
- results from other users can be easily downloaded and reused

## Terminology: Studies and Benchmark Suites
OpenML offers the ability to bundle several objects on the server together on a
single page. With a slight overload of terminology, these are called `studies'.
OpenML supports two types of studies:
- benchmark suites: collection of tasks. OpenML automatically organizes all 
tasks and the correpsonding datasets on a single server page. These are the main
aim of this article.
- studies: collection of runs. OpenML automatically organizes all runs and
corresponding flows, tasks and datasets on a single server page. Studies can be
defined on top of benchmark suites. In that case, the creator intends to run 
his algorithms on the tasks defined in a benchmark suite. Additional information
of how to organize reproducible benchmarks will be provided in a separate page. 

## Using OpenML Benchmark Suites
Below are detailed instructions for common use cases, as well as code examples. These illustrations use the reference 'OpenML-CC18' benchmark suite, but you can replace this with any other benchmark suite. In all examples, OpenML does not only return the datasets, but also the train-test splits and (for predictive tasks) the target feature. These are enveloped as _tasks_ in OpenML. Hence, you will usually receive a list of tasks in which you can find the data itself.

### Listing the benchmark suites
For now, these are explicitly listed below. We will add them to the OpenML search engine soon.
Each benchmarking suite points to an OpenML study with the exact list of datasets and tasks.

**TODO: List all studies with type=suite /study/list/main_entity_type/task (future work)**

In Python, the following code returns the studies:
??? note "Python example"
    ```python
    import openml
    
    studies = openml.study.list_studies(main_entity_type='task')
    ```


### Fetching the datasets
You can fetch and iterate through the benchmark datasets with a few lines of code. See the code examples below.

Via the REST API, a list of all tasks and dataset IDs is returned
!!! note "REST"
    ``` 
    https://www.openml.org/api/v1/study/name/OpenML-CC18
    ```

In Python, the data is returned as X, y numpy arrays:
??? note "Python example"
    ```python
    import openml
    benchmark_suite = openml.study.get_study('OpenML-CC18','tasks') # obtain the benchmark suite
    for task_id in benchmark_suite.tasks: # iterate over all tasks
        task = openml.tasks.get_task(task_id) # download the OpenML task
        X, y = task.get_X_and_y() # get the data
    ```

In Java, the data is returned as a WEKA Instances object:
??? note "Java example"
    ```java
    OpenmlConnector openml = new OpenmlConnector();
    Study benchmarksuite = openml.studyGet("OpenML-CC18", "tasks");
    for (Integer taskId : benchmarksuite.getTasks()) { // iterate over all tasks
        Task t = openml.taskGet(taskId); // download the OpenML task
        Instances d = InstancesHelper.getDatasetFromTask(openml, t); // obtain the dataset
    }
    ```
The Java implementation automatically uploads results to the server.

In R, the data is returned as an R dataframe:
??? note "R example"
    ```r
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

??? note "Python example"
    ```python
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

??? note "Java example"
    ```java
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

??? note "R example"
    ```r
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

### Retrieving runs on a benchmarking suites:
Once a benchamrk suite has been created, the listing functions can be used to 
obtain all results on the benchmark suite. Note that there are several other
ways to select and bundle runs together in a study. This will be featured in 
a separate article on reproducible benchmarks. 

**TODO: Add API call for listing all runs of a study /study/name/<studyname>**

??? note "Python example"
    ```python
    benchmark_suite = openml.study.get_study('OpenML-CC18', 'tasks')
    
    runs = openml.runs.list_runs(task=benchmark_suite.tasks, limit=1000)
    ```


**TODO: Add R code for listing all runs of a study**
**TODO: Add Java code for listing all runs of a study**


## Creating new benchmark suites
The set of datasets on OpenML.org can <a href="https://www.openml.org/new/data">easily be extended</a>, and additional OpenML benchmark suites,
e.g., for regression and time-series data, can easily be created by defining sets of datasets according to specific needs.

??? note "Python example"
    ```python
    
    import openml
    
    # find 250 tasks that we are interested in, e.g., the tasks that have between
    # 100 and 10000 instances and between 4 and 20 attributes
    tasks = openml.tasks.list_tasks(number_instances='100..10000', number_features='4..20', size=250)
    task_ids = list(tasks.keys())
    
    # create the benchmark suite
    study = openml.study.create_benchmark_suite(None, "MidSize Suite", "illustrating how to create a benchmark suite", task_ids)
    study_id = study.publish()
    ```

### Updating a benchmark suite
- You can add or remove additional tasks to the benchmark suite

```python
    
    import openml
    
    # find 250 tasks that we are interested in, e.g., the tasks that have between
    # 100 and 10000 instances and between 4 and 20 attributes
    tasks = openml.tasks.list_tasks(number_instances='100..10000', number_features='4..20', size=250)
    task_ids = list(tasks.keys())
    
    # create the benchmark suite
    study = openml.study.create_benchmark_suite(None, "MidSize Suite", "illustrating how to create a benchmark suite", task_ids)
    study_id = study.publish()
    
    # download the study from the server, for verification purposes
    study = openml.study.get_study(study_id)
    
    # until the benchmark suite is activated, we can also add some more tasks. Search for the letter dataset:
    tasks_new = openml.tasks.list_tasks(data_name='letter', size=1)
    task_ids_new = list(tasks_new.keys())
    openml.study.attach_to_study(study_id, task_ids_new)
    
    # or even remove these again
    openml.study.detach_from_study(study_id, task_ids_new)
    
    # redownload the study
    study_prime = openml.study.get_study(study_id)
    
    assert(study.tasks == study_prime.tasks)
    assert(study.data == study_prime.data)
    ```

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
The OpenML100 was first published in the Arxiv preprint [OpenML Benchmarking Suites and the OpenML100](https://arxiv.org/abs/1708.03731).

[List of datasets and properties](https://www.openml.org/s/14)


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

