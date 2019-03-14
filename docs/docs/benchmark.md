# Benchmarking suites

Machine learning research depends on objectively interpretable, comparable, and reproducible algorithm benchmarks. OpenML aims to facilitate the creation of curated, comprehensive _suites_ of machine learning tasks, covering precise sets of conditions.

Seamlessly integrated into the OpenML platform, benchmark suites standardize the setup, execution, analysis, and reporting of benchmarks. Moreover, they make benchmarking a whole lot easier:

- all datasets are uniformly formatted in standardized data formats
- they can be easily downloaded programmatically through [APIs and client libraries](APIs)
- they come with machine-readable [meta-information](https://www.openml.org/search?type=measure&q=+measure_type%3Adata_quality), such as the occurrence of missing values, to train algorithms correctly
- standardized train-test splits are provided to ensure that results can be objectively compared
- results can be shared in a reproducible way through the [APIs](APIs)
- results from other users can be easily downloaded and reused


## Software interfaces
To use OpenML Benchmark suites, you can use bindings in several programming languages. These all interface with the OpenML REST API. The default endpoint for this is `https://www.openml.org/api/v1/`, but this can change when later versions of the API are released. To use the code examples below, you only need a recent version of one of the following libraries:
  
* [OpenML Java ApiConnector](https://mvnrepository.com/artifact/org.openml/apiconnector) (version `1.0.22` and up).
* [OpenML Weka](https://search.maven.org/search?q=a:openmlweka) (version `0.9.6` and up). This package adds a Weka Integration.
* [OpenML Python](https://pypi.org/project/openml/) (version `0.8.0` and up)
* [OpenML R](https://cran.r-project.org/web/packages/OpenML/index.html) (version `1.8` and up)


## Using OpenML Benchmark Suites
Below are walk-through instructions for common use cases, as well as code examples. These illustrations use the reference [OpenML-CC18](https://docs.openml.org/benchmark/#openml-cc18) benchmark suite, but you can replace it with any other benchmark suite. Note that a benchmark suite is a set of OpenML `tasks`, which envelop not only a specific dataset, but also the train-test splits and (for predictive tasks) the target feature.

??? note "Terminology and current status"
    Benchmark suites are sets of OpenML tasks that you can create and manage yourself. At the same time, it is often useful to also share the set of experiments (runs) with the ensuing benchmarking results. For legacy reasons, such sets of tasks or runs are called `studies` in the OpenML REST API. In the OpenML bindings (Python, R, Java,...) these are called either `sets` or `studies`.

    When benchmarking, you will probably use two types of sets:

    * Sets of tasks. These can be created, edited, downloaded or deleted via the OpenML API. Website forms will be added soon. Also the set of underlying datasets can be easily retrieved via the API.
    * Sets of runs. Likewise, these can be created, edited, downloaded or deleted via the OpenML API. On the website, these are currently simply called 'studies'. Also the set of underlying tasks, datasets and flows can be easily retrieved. It is possible to link a set of runs to a benchmark study, aimed to collect future runs on that specific set of tasks. Additional information on these will be provided in a separate page.

### Listing the benchmark suites
The current list of benchmark suites is explicitly listed on the bottom of this page. The list of all sets of tasks can also be fetched programmatically. This list includes the suite's ID (and optionally an alias), which can be used to fetch further details.

Via the REST API, the list is returned in XML or JSON
??? note "REST (under development)"
    ``` 
    https://www.openml.org/api/v1/xml/study/list/main_entity_type/task
    ```
  
??? note "Python example (requires the development version)"
    ```python
    import openml
    
    # using the main entity type task, only benchmark suites are returned
    # each benchmark suite has an ID, some also have an alias. These can be
    # used to obtain the full details. 
    studies = openml.study.list_studies(main_entity_type='task')
    ```

??? note "Java example"
    ```java
    public void listBenchmarksuites() throws Exception {
        OpenmlConnector openml = new OpenmlConnector();
        Map<String, String> filters = new TreeMap<String, String>();
		filters.put("status", "all");
		filters.put("main_entity_type", "task");
		filters.put("limit", "20");
		StudyList list = openml.studyList(filters);
    }
    ```
    
??? note "R example"
    ``` 
    TODO
    ```

### Fetching details
Using the ID or alias of a benchmark suite, you can retrieve a description and the full list of tasks and the underlying datasets.

Via the REST API, a list of all tasks and dataset IDs is returned in XML or JSON
??? note "REST"
    ``` 
    https://www.openml.org/api/v1/xml/study/OpenML-CC18
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
    public void downloadDatasets() throws Exception {
        OpenmlConnector openml = new OpenmlConnector();
        Study benchmarksuite = openml.studyGet("OpenML-CC18", "tasks");
        for (Integer taskId : benchmarksuite.getTasks()) { // iterate over all tasks
            Task t = openml.taskGet(taskId); // download the OpenML task
            // note that InstanceHelper is part of the OpenML-weka package
            Instances d = InstancesHelper.getDatasetFromTask(openml, t); // obtain the dataset
        }
    }
    ```

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

First, the list of tasks is downloaded as already illustrated above. Next, a specific algorithm (or pipeline) can be run on each of them. The OpenML API will automatically evaluate the algorithm using the pre-set train-test splits and store the predictions and scores in a run object. This run object can then be immediately published, pushing the results to the OpenML server, so that they can be compared against all others on the same benchmark set. Uploading results requires an OpenML API key, which can be found in your account details after logging into the OpenML website.

??? note "Python example"
    ```python
    import openml
    import sklearn
    openml.config.apikey = 'FILL_IN_OPENML_API_KEY' # set the OpenML Api Key
    benchmark_suite = openml.study.get_study('OpenML-CC18','tasks') # obtain the benchmark suite
    # build a sklearn classifier
    clf = sklearn.pipeline.make_pipeline(sklearn.preprocessing.Imputer(),
                                         sklearn.tree.DecisionTreeClassifier())
    for task_id in benchmark_suite.tasks: # iterate over all tasks
      task = openml.tasks.get_task(task_id) # download the OpenML task
      X, y = task.get_X_and_y() # get the data (not used in this example)
      # run classifier on splits (requires API key)
      run = openml.runs.run_model_on_task(clf, task)
      score = run.get_metric_score(sklearn.metrics.accuracy_score) # print accuracy score
      print('Data set: %s; Accuracy: %0.2f' % (task.get_dataset().name,score.mean()))
      run.publish() # publish the experiment on OpenML (optional)
      print('URL for run: %s/run/%d' %(openml.config.server,run.run_id))
    ```

??? note "Java example"
    ```java
    public static void runTasksAndUpload() throws Exception {
      OpenmlConnector openml = new OpenmlConnector();
      openml.setApiKey("FILL_IN_OPENML_API_KEY");
      // obtain the benchmark suite
      Study benchmarksuite = openml.studyGet("OpenML-CC18", "tasks");
      Classifier tree = new REPTree(); // build a Weka classifier
      for (Integer taskId : benchmarksuite.getTasks()) { // iterate over all tasks
        Task t = openml.taskGet(taskId); // download the OpenML task
        Instances d = InstancesHelper.getDatasetFromTask(openml, t); // obtain the dataset
        int runId = RunOpenmlJob.executeTask(openml, new WekaConfig(), taskId, tree);
        Run run = openml.runGet(runId);   // retrieve the uploaded run
      }
    }
    ```

??? note "R example"
    ```r
    library(OpenML)
    setOMLConfig(apikey = 'FILL_IN_OPENML_API_KEY')
    lrn = makeLearner('classif.rpart') # construct a simple CART classifier
    task.ids = getOMLStudy('OpenML-CC18')$tasks$task.id # obtain the list of suggested tasks
    for (task.id in task.ids) { # iterate over all tasks
      task = getOMLTask(task.id) # download single OML task
      data = as.data.frame(task) # obtain raw data set
      run = runTaskMlr(task, learner = lrn) # run constructed learner
      upload = uploadOMLRun(run) # upload and tag the run
    }
    ```

### Retrieving runs on a benchmarking suites:
Once a benchmark suite has been created, the listing functions can be used to 
obtain all results on the benchmark suite. Note that there are several other
ways to select and bundle runs together. This will be featured in 
a separate article on reproducible benchmarks. 

??? note "REST (TODO)"
    ``` 
    https://www.openml.org/api/v1/xml/run/list/study/OpenML-CC18
    ```
    
??? note "Python example"
    ```python
    benchmark_suite = openml.study.get_study('OpenML-CC18', 'tasks')
    runs = openml.runs.list_runs(task=benchmark_suite.tasks, limit=1000)
    ```
    
??? note "Java example"
    ```java
    
	public void downloadResultsBenchmarkSuite()  throws Exception {
		Study benchmarkSuite = openml.studyGet("OpenML100", "tasks");
		
		Map<String, List<Integer>> filters = new TreeMap<String, List<Integer>>();
		filters.put("task", Arrays.asList(benchmarkSuite.getTasks()));
		RunList rl = openml.runList(filters, 200, null);
		
	    assertTrue(rl.getRuns().length > 0); 
    }
    ```
    
??? note "R example"
    ``` 
    TODO
    ```

### Creating new benchmark suites
Additional OpenML benchmark suites can be created by defining the precise set of tasks, as well as a textual description. New datasets first need to be <a href="https://www.openml.org/new/data">registered on OpenML</a> and tasks need to be created on them.

We have provided [a GitHub repository](https://github.com/openml/benchmark-suites) with additional tools and scripts to build new benchmark studies, e.g. to select all datasets adhering to strict conditions, and to analyse bencharking results.

??? note "Python example"
    ```python
    import openml
    
    # find 250 tasks that we are interested in, e.g., the tasks that have between
    # 100 and 10000 instances and between 4 and 20 attributes
    tasks = openml.tasks.list_tasks(number_instances='100..10000', number_features='4..20', size=250)
    task_ids = list(tasks.keys())
    
    # create the benchmark suite
    # the arguments are the alias, name, description, and list of task_ids, respectively.
    study = openml.study.create_benchmark_suite(None, "MidSize Suite", "illustrating how to create a benchmark suite", task_ids)
    study_id = study.publish()
    ```

??? note "Java example"
    ```java
    public void createBenchmarkSuite() throws Exception {
        OpenmlConnector openml = new OpenmlConnector("FILL_IN_OPENML_API_KEY");
        // find 250 tasks that we are interested in, e.g., the tasks that have between
	    // 100 and 10000 instances and between 4 and 20 attributes
		Map<String, String> filtersOrig = new TreeMap<String, String>();
	    filtersOrig.put("number_instances", "100..10000");
	    filtersOrig.put("number_features", "4..20");
	    filtersOrig.put("limit", "250");
	    Tasks tasksOrig = client_write_test.taskList(filtersOrig);
	    
	    // create the study
	    Study study = new Study(null, "test", "test", null, tasksOrig.getTaskIds(), null);
	    int studyId = openml.studyUpload(study);
    }
    ```
    
??? note "R example"
    ``` 
    TODO
    ```

### Updating a benchmark suite
You can add tasks to a benchmark suite, or remove them.

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

??? note "Java example"
    ```java
    public void attachDetachStudy()  throws Exception {
        OpenmlConnector openml = new OpenmlConnector("FILL_IN_OPENML_API_KEY");
        // find 250 tasks that we are interested in, e.g., the tasks that have between
	    // 100 and 10000 instances and between 4 and 20 attributes
		Map<String, String> filtersOrig = new TreeMap<String, String>();
	    filtersOrig.put("number_instances", "100..10000");
	    filtersOrig.put("number_features", "4..20");
	    filtersOrig.put("limit", "250");
	    Tasks tasksOrig = openml.taskList(filtersOrig);
	    
	    // create the study
	    Study study = new Study(null, "test", "test", null, tasksOrig.getTaskIds(), null);
	    int studyId = openml.studyUpload(study);
	    
	    // until the benchmark suite is activated, we can also add some more tasks. Search for the letter dataset:
	    Map<String, String> filtersAdd = new TreeMap<String, String>();
	    filtersAdd.put("data_name", "letter");
	    filtersAdd.put("limit", "1");
	    Tasks tasksAdd = openml.taskList(filtersAdd);
	    openml.studyAttach(studyId, Arrays.asList(tasksAdd.getTaskIds()));
	    
	    // or even remove these again
	    openml.studyDetach(studyId, Arrays.asList(tasksAdd.getTaskIds()));
	    
	    // download the study
	    Study studyDownloaded = openml.studyGet(studyId);
	    assertArrayEquals(tasksOrig.getTaskIds(), studyDownloaded.getTasks());
    }
    ```
    
??? note "R example"
    ``` 
    TODO
    ```

## Further code examples and use cases

As mentioned above, we host [a GitHub repository](https://github.com/openml/benchmark-suites) with additional tools and scripts to easily create and use new benchmark studies. It includes:

* A Jupyter Notebook that builds a new benchmark suite with datasets that adhere to strict and complex conditions, as well as automated tests to remove tasks that are too easy for proper benchmarking.
* A Jupyter Notebook that shows how to pull in the latest state-of-the-art results for any of the benchmark suites
* A Jupyter Notebook that does a detailed analysis of all results in a benchmark suite, and an example run on the OpenML-CC18. It includes a wide range of plots and rankings to get a deeper insight into the benchmark results.
* Scripts in Python and R to facilitate common subtasks.

We very much welcome new scripts and notebooks, or improvements to the existing ones, that help others to create benchmark suites and analyse benchmarking results.


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

