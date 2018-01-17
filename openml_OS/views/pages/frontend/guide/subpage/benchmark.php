<div class="page-header">
  <h1>Benchmarking</h1>
</div>
<p>OpenML significantly simplifies benchmarking by:</p>
</li><li>offering a large set of datasets in standardized data formats
</li><li>easily download through a wide range of <a href="guide/api">APIs and existing client libraries</a>
</li><li>machine-readable meta-information on all the datasets
</li><li>sharing of banchmarking results in a reproducible way through the <a href="guide/api">APIs</a>, enabling large scale comparisons
</li></ul>
<br>
<p>In addition, OpenML offers <b>benchmarking suites</b>: curated, comprehensive sets of machine learning datasets,
covering a wide spectrum of domains and statistical propertie and standardized evaluation procedures (defined in tasks).
This makes benchmarking results more comparable, more complete, and allows more standardized analysis of algorithms under different conditions.
</p>

<h2>OpenML100</h2>
As a first such suite, we propose the OpenML100, <a href="https://www.openml.org/search?q=tags.tag%3AOpenML100&type=data&table=1&size=100">a machine learning benchmark suite of 100 classification datasets</a> carefully
curated from the thousands of datasets available. We selected classification datasets for this benchmarking suite to satisfy the following requirements:
<ul><li>the number of observations are between 500 and 100000 to focus on medium-sized datasets, that are not too small for proper training and not too big for practical experimentation,
</li><li>the number of features does not exceed 5000 features to keep the runtime of algorithms low
</li><li>the target attribute has at least two classes
</li><li>the ratio of the minority class and the majority class is above 0.05 to eliminate highly imbalanced datasets that would obfuscate a clear analysis.
</li></ul>

We excluded datasets which:
<ul><li>cannot be randomized via a 10-fold cross-validation due to grouped samples,
</li><li>have an unknown origin or no clearly defined task,
</li><li>are variants of other datasets (e.g. binarized regression tasks),
</li><li>include sparse data (e.g., text mining data sets).
</li></ul>

<h2>Code examples</h2>
The code below demonstrates how OpenML benchmarking suites, in this case the OpenML100, can be conveniently imported for benchmarking using the Python, Java and R APIs.
The OpenML100 tasks are downloaded through the <a href="s/14">study with the same name</a>,
which contains the 100 tasks and also holds all benchmarking results obtained on them.
The code also shows how to access the raw data set (although this is not needed to train a model),
fit a simple classifier on the defined data splits, and finally publish runs on the OpenML server.

<h5>Python (with scikit-learn)</h5>
<div class="codehighlight"><pre>
<code class="python">import openml
import sklearn
benchmark_suite = openml.study.get_study('OpenML100','tasks') # obtain the benchmark suite
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
</code></pre></div>

<h5>Java (with WEKA)</h5>
<div class="codehighlight"><pre>
<code class="java">public static void runTasksAndUpload() throws Exception {
  OpenmlConnector openml = new OpenmlConnector();
  // obtain the benchmark suite
  Study benchmarksuite = openml.studyGet("OpenML100", "tasks");
  Classifier tree = new REPTree(); // build a Weka classifier
  for (Integer taskId : benchmarksuite.getTasks()) { // iterate over all tasks
    Task t = openml.taskGet(taskId); // download the OpenML task
    Instances d = InstancesHelper.getDatasetFromTask(openml, t); // obtain the dataset
    openml.setApiKey("FILL_IN_OPENML_API_KEY");
    int runId = RunOpenmlJob.executeTask(openml, new WekaConfig(), taskId, tree);
    Run run = openml.runGet(runId);   // retrieve the uploaded run
  }
}

</code></pre></div>

<h5>R (with mlr)</h5>
<div class="codehighlight"><pre>
<code class="r">library(OpenML)
lrn = makeLearner('classif.rpart') # construct a simple CART classifier
task.ids = getOMLStudy('OpenML100')$tasks$task.id # obtain the list of suggested tasks
for (task.id in task.ids) { # iterate over all tasks
  task = getOMLTask(task.id) # download single OML task
  data = as.data.frame(task) # obtain raw data set
  run = runTaskMlr(task, learner = lrn) # run constructed learner
  setOMLConfig(apikey = 'FILL_IN_OPENML_API_KEY')
  upload = uploadOMLRun(run) # upload and tag the run
}
</code></pre></div>

<h2>Creating new benchmark suites</h2>
The set of datasets on OpenML.org can <a href="new/data">easily be extended</a>, and additional OpenML benchmark suites,
e.g., for regression and time-series data, can easily be created by defining sets of datasets according to specific needs.
This is done by:
<ul><li><a href="new/study">Creating a new study</a> with the name of the benchmark as the alias.
</li><li>Adding new tasks to the study by <i>tagging</i> them with the name of the benchmark.
</ul>
