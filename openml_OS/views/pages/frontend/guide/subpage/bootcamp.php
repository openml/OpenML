<h2>OpenML Bootcamp</h2>
<p style="margin-top:20px;">
<b><i>OpenML aims to make machine learning and data analysis simple, accessible, collaborative and open,
  automating many manual tasks to create a frictionless working and learning environment.</i></b>
<p>

<p><i class="fa fa-globe fa-fw fa-lg"></i> <a href="d">Data sets</a> from various sources automatically analyzed, annotated, and organized online for easy access</p>
<p><i class="fa fa-cogs fa-fw fa-lg"></i> Integrated into <a href="guide/integrations">machine learning tools</a> for automated experimentation, logging, and <a href="f">workflow</a> sharing</p>
<p><i class="fa fa-code fa-fw fa-lg"></i> Clear <a href="guide/api">APIs</a> to integrate OpenML into your own tools and scripts</p>
<p><i class="fa fa-flask fa-fw fa-lg"></i> <a href="r">Fully reproducible and organized results</a> (e.g. models, predictions) you can build on and compare against</p>
<p><i class="fa fa-users fa-fw fa-lg"></i> Share your work with the world or within circles of trusted researchers</p>
<p><i class="fa fa-graduation-cap fa-fw fa-lg"></i> Make your work more visible and easily citable</p>
<p><i class="fa fa-bolt fa-fw fa-lg"></i> Open source tools to design and optimize workflows</p>

<p style="margin-top:20px;">In short, OpenML makes it easy to access data, connect to the right people, and automate experimentation, so that you can focus on the data science.</p>

    <!--<h3 id="g_intro">Introduction</h3>
<p>OpenML is a place where data scientists can automatically share data in fine detail, build on the results of others, and collaborate on a global scale. It allows anyone to link new data sources, and everyone able to analyse that data to share their code and results (e.g., models, predictions, and evaluations). OpenML makes sure that all shared results are stored and organized online for easy access, reuse and discussion.</p>
<p>Moreover, OpenML is integrated in many great data mining platforms, so that anyone can easily import the data into these tools, pick any algorithm or workflow to run, and automatically keep track of all obtained results. The OpenML website provides easy access to all collected data and code, compares all results obtained on the same data or algorithms, builds data visualizations, and supports online discussions.</p>
<p>-->

<h3 class="text-warning"><i class="fa fa-lightbulb-o fa-fw"></i> Concepts</h3>
<p>OpenML operates on a number of core concepts which are important to understand to use OpenML.
These are:

<dl class="bootcamplist">
<dt><a class="text-success" href="guide/bootcamp/#g_start"><i class="fa fa-database"></i> Datasets</a></dt>
<dd>Datasets are pretty straight-forward. They simply consist of a number of
rows, also called <em>instances</em>, usually in tabular form.</br><b>Example:</b> The <a href="/d/61">iris</a> dataset.</dd>
<dt><a class="text-warning" href="guide/bootcamp/#Tasks"><i class="fa fa-trophy"></i> Tasks</a></dt>

<dd>A task consists of a dataset, together with a machine learning task to
perform, such as classification or clustering and an evaluation method. For
supervised tasks, this also specifies the target column in the data.</br>
<b>Example:</b> Predicting the species from the other attributes in the iris
dataset and evaluate using 10-fold cross-validation (<a
href="https://www.openml.org/t/59">see here</a>).
</dd>

<dt><a class="text-info" href="guide/bootcamp/#Flows"><i class="fa fa-cogs"></i> Flows</a></dt>
<dd>A flow identifies a particular machine learning algorithm from a particular
library or framework such as Weka, mlr or scikit-learn.</br><b>Example:</b> <a
href="https://www.openml.org/f/65">weka's RandomForest</a></dd>
<dt><a class="text-danger" href="guide/bootcamp/#Runs"><i class="fa fa-star"></i> Runs</a></dt>

<dd>A run is a particular flow, that is algorithm, with a particular parameter
setting, applied to a particular task.</br><b>Example:</b> <a
href="https://www.openml.org/r/6466">Classifying iris with weka's
RandomForest</a></dd>
</p>


<h3 class="text-info"><i class="fa fa-key fa-fw"></i> API Keys</h3>
<p>You can download and inspect all datasets, tasks, flows and runs through the
website or the API without creating an account. However, if you want to upload
datasets or experiments, you need to <a href="/register">create an account</a>
or sign in and <a href="https://www.openml.org/#api">create an API key</a>.
This key can then be used with any of the <a
href="guide/api">OpenML interfaces</a>.


<h3 id="g_start" class="text-success"><i class="fa fa-database fa-fw"></i> Data</h3>
<p>You can upload and download datasets through the <a href="new/data"
class="loginfirst">website</a>, or <a href="guide/api">API</a>. Data hosted
elsewhere can be referenced by URL.</p>

<p>Data consists of columns, also known as features or covariates, each of
which is either numeric, nominal or a string, and has a unique name. A column
can also contain any number of missing values.</p>
 <div class="img-guide-wrapper"><img src="img/data-ss1.png" alt="dataset
properties" class="img-guide img-responsive"></div>
<p>Most datasets have a "default target attribute" which denotes the column that
is usually the target, also known as dependent variable, in supervised learning tasks. The default
target column is denoted by "(target)" in the web interface. Not all datasets
have such a column, though, and a supervised task can pick any column as the
target (as long as it is of the appropriate type).</p>
<p>Example: The default target variable for the <a
href="https://www.openml.org/d/554">MNIST</a> data is to predict the class from
pixel values, OpenML also allows you to create a task that tries to predict the
value of pixel257 given all the other pixel values and the class column. As such,
the class is also considered a feature in OpenML terminology.</p>

<p>OpenML automatically analyzes the data, checks for problems, visualizes it,
and computes <a href="search?q=+measure_type%3Adata_quality&type=measure">data
characteristics</a>, also called data qualities (including simple ones like number of features, but also
more complex statistics like kurtosis or the AUC of a decision tree of depth 3).
These data qualities can be useful to find and compare datasets.</p>


<p>Every dataset gets a dedicated page with all known information (check out
<a href="d/62">zoo</a>), including a wiki, visualizations, statistics, user
discussions, and the <i>tasks</i> in which it is used.</p>

<p><i class="fa fa-fw fa-exclamation-triangle"></i>OpenML currently
only supports uploading of ARFF files. We aim to extend this in the near
future, and allow conversions between the main data types.</p>

<h4>Technical details</h4>
<h5>Dataset ID and versions</h5>
<p>A dataset can be uniquely identified by its dataset ID, which you can find
in the URL of the dataset page, such as 62 for <a href="d/62">zoo</a>. Each
dataset also has a name, but several dataset can have the same name. When several datasets
have the same name, they are called "versions" of the same dataset (although
that is not necessarily true). The version number is assigned according to the order
of upload. Different versions of a dataset can be accessed through the drop
down menu at the top right of the dataset page.</p>
 <div class="img-guide-wrapper"><img src="img/data_version.png" alt="dataset
versions" class="img-guide img-responsive"></div>

<h5>Dataset status</h5>
<p>Each dataset has a status, which can be "active", "deactivated" or
"in_preparation". When you upload a dataset, it will be marked "in_preparation"
until it is approved by a site administrator.  Once it is approved, the dataset
will become "active". If a severe issue has been found with a dataset, it can
become "deactivated". By default, the search will only display datasets that are
"active", but you can access and download datasets with any status.</p>

<h5>Ignored features</h5>
Features in datasets can be tagged as "ignored" or "row id". Those features will not be
considered by programming interfaces, and excluded from any tasks.


<h3 class="text-warning" id="Tasks"><i class="fa fa-trophy fa-fw"></i> Tasks</h3>
<p>Tasks describe what to do with the data. OpenML covers several <a
href="search?type=task_type">task types</a>, such as classification and
clustering. You can <a href="new/task" class="loginfirst">create tasks</a>
online.</p>
<p>Tasks are little containers including the data and other information such as
train/test splits, and define what needs to be returned.</p>
<p>Tasks are machine-readable so that machine learning environments know what
to do, and you can focus on finding the best algorithm. You can run algorithms
on your own machine(s) and upload the results. OpenML evaluates and organizes
all solutions online.</p>
<div class="img-guide-wrapper"><img src="img/task-ss1.png" alt="dataset properties" class="img-guide img-responsive"></div>

<p>Tasks are <i>real-time, collaborative</i> data mining challenges (e.g. see
<a href="t/7293#!people">this one</a>): you can study, discuss and learn from
all submissions (code has to be shared), while OpenML keeps track of who was
first.</p>
<div class="img-guide-wrapper"><img src="img/task-ss2.png" alt="dataset properties" class="img-guide img-responsive"></div>
<p>More concretely, tasks specify the dataset, the kind of machine learning
task (i.e. regression), the target attribute (i.e. which column in the dataset
should be predicted), the number of splits for cross-validated evaluation and
the exact dataset splits, as well as an optional evaluation metric (i.e. mean
squared error). Given this specification, a task can be solved using any of the
<a href="guide/integrations">integrated machine learning tools</a>, like Weka,
mlr and scikit-learn.</p> <p><i class="fa fa-fw
fa-exclamation-triangle"></i>You can also supply hidden test sets for the
evaluation of solutions. Novel ways of ranking solutions will be added in the
near future.</p>

<h3 class="text-info" id="Flows"><i class="fa fa-cogs fa-fw"></i> Flows</h3>
<p>Flows are algorithms, workflows, or scripts solving tasks. You can upload
them through the <a href="new/flow" class="loginfirst">website</a>, or <a
href="guide/api">API</a>. Code hosted elsewhere (e.g., GitHub) can be
referenced by URL, though typically they are generated automatically by <a
href="guide/integrations">machine learning environments</a>.</p>
<p>Flows contain all the information necessary to apply a particular workflow
or algorithm to a new task. Usually a flow is specific to a task-type, i.e.
you can not run a classification model on a clustering task.</p>
<p>Every flow gets a dedicated page with all known information (check out <a
href="f/65">WEKA's RandomForest</a>), including a wiki, hyperparameters,
evaluations on all tasks, and user discussions.</p>
<div class="img-guide-wrapper"><img src="img/flow-ss1.png" alt="dataset
properties" class="img-guide img-responsive"></div>

<p><i class="fa fa-fw fa-exclamation-triangle"></i>Each flow specifies
requirements and dependencies, and you need to install these locally to execute
a flow on a specific task. We aim to add support for VMs so that flows can be
easily (re)run in any environment.</p>

<h3 class="text-danger" id="Runs"><i class="fa fa-star fa-fw"></i> Runs</h3>
<p>Runs are applications of flows to a specific task. They are typically
submitted automatically by <a href="guide/integrations">machine learning
environments</a> (through the OpenML API), with the goal of creating a
reproducible experiment (though exactly reproducing experiments across machines
might not be possible because of changes in numeric libraries and operating
systems).</p>
<p>OpenML organizes all runs online, linked to the underlying data, flows,
parameter settings, people, and other details. OpenML also independently
evaluates the results contained in the run given the provided predictions.</p>
<p>You can search and compare everyone's runs online, download all results into
your favorite machine learning environment, and relate evaluations to known
properties of the data and algorithms.</p>
<div class="img-guide-wrapper"><img src="img/run-ss1.png" alt="dataset
properties" class="img-guide img-responsive"></div>
<p>OpenML stores and analyzes results in fine detail, up to the level of
individual instances.</p>

<h3 id="g_plugins" class="text-success"><i class="fa fa-plug fa-fw"></i> Integrations</h3>
<p>OpenML is deeply <a href="guide/integrations">integrated in several popular
machine learning environments</a>. Given a task, these integrations will
automatically download the data into the environments, allow you to run any
algorithm/flow, and automatically upload all runs.</p>
<div class="img-guide-wrapper"><img src="img/plugins-ss1.png" alt="dataset properties" class="img-guide img-responsive"></div>


<h3 id="g_apis" class="text-warning"><i class="fa fa-rocket fa-fw"></i> Programming APIs</h3>
<p>If you want to integrate OpenML into your own tools, we offer several <a
href="guide/api">language-specific APIs</a>, so you can easily interact with
OpenML to list, download and upload datasets, tasks, flows and runs.</p>
<p>With these APIs you can download a task, run an algorithm, and upload the
results in just a few lines of code.</p>
<p>OpenML also offers a <a href="guide/rest">REST API</a> which allows you to talk to OpenML
directly.</p>

<div class="img-guide-wrapper"><img src="img/r-ss1.png" alt="dataset properties" class="img-guide img-responsive"></div>

<h3 class="text-info"><i class="fa fa-tags fa-fw"></i> Tags</h3>
<p>Datasets, tasks, runs and flows can be assigned tags, either via the web
interface or the API. These tags can be used to search and annotated datasets.
For example the tag <a
href="search?q=tags.tag%3AOpenML100&type=task">OpenML100</a> refers to
benchmark machine learning algorithms used as a benchmark suite. Anyone can add
or remove tags on any entity.</p>

<h3 class="text-muted"><i class="fa fa-folder fa-fw"></i> Studies (under construction)</h3>
<p>You can combine datasets, flows and runs into studies, to collaborate with others online, or simply keep a log of your work.</p>
<p>Each project gets its own page, which can be linked to publications so that others can find all the details online.</p>

<h3 class="text-muted"><i class="fa fa-users fa-fw"></i> Circles (under construction)</h3>
<p>You can create circles of trusted researchers in which data can be shared that is not yet ready for publication.</p>

<h3 class="text-muted"><i class="fa fa-graduation-cap fa-fw"></i> Altmetrics (under construction)</h3>
<p>To encourage open science, OpenML now includes altmetrics to track and reward scientific activity, reach and impact, and in the future will include further gamification features such as badges.</p>
<p><a href="guide/altmetrics">Learn more about altmetrics</a></p>

<h3 class="text-muted"><i class="fa fa-bolt fa-fw"></i> Jobs (under construction)</h3>
<p>OpenML can help you run large experiments. A job is a small container defining a specific flow, with specific parameters settings, to run on a specific tasks. You can generate batches of these jobs online, and you can run a helper tool on your machines/clouds/clusters that downloads these jobs (including all data), executes them, and uploads the results.</p>

</p>
