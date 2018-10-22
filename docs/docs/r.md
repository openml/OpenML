          <div class="page-header">
            <h1>R API</h1>
          </div>
	<p>The OpenML R package allows you to connect to the OpenML server from R scripts.
  This means that you can download and upload data sets and tasks, run R implementations, upload your results,
  and download all experiment results directly via R commands.</p>

  <p>It is also neatly integrated into <a href="https://github.com/mlr-org/mlr">mlr</a> (Machine Learning in R),
  which provides a unified interface to a large number of machine learning algorithms in R. As such, you can
  easily run and compare many R algorithms on all OpenML datasets, and analyse all combined results.</p>

  <p>All in a few lines of R.</p>

  <h2 id="r-demo">Demo</h2>
  <p>You can try it out yourself in a Jupyter Notebook running in the everware cloud. You'll need an OpenML account as well as a <a href="www.github.com">GitHub</a> account for this service to work properly. It may take a few minutes to spin up.</p>
  <p><a target="_blank" class="loginfirst btn btn-success" href="https://everware.rep.school.yandex.net/hub/oauth_login?repourl=https://github.com/openml/study_example&OPENMLKEY=<?php echo $this->api_key;?>">Launch demo</a></p>

  <h2 id="r-download">Example</h2>
  <p>This example runs an mlr algorithm on an <a href="t/10">OpenML task</a>. The first time, you need to set your <a href="u#!api">API key</a> on your machine.</p>
  <div class="codehighlight"><pre><code class="r">
  library(mlr)
  library(OpenML)
  setOMLConfig(apikey = qwertyuiop1234567890) # Only the first time

  task = getOMLTask(10)
  lrn = makeLearner("classif.rpart")
  res = runTaskMlr(task, lrn)
  run.id = uploadOMLRun(res)
  </code></pre></div>

  <p>You can of course do many experiments at once:</p>
  <div class="codehighlight"><pre><code class="r">
  # A list of OpenML task ID's
  task.ids = c(10,39)

  # A list of MLR learners
  learners = list(
      makeLearner("classif.rpart"),
      makeLearner("classif.randomForest")
      )

  # Loop
  for (lrn in learners) {
    for (id in task.ids) {
      task = getOMLTask(id)
      res = runTaskMlr(task, lrn)
      run.id = uploadOMLRun(res)
    }
  }
  </code></pre></div>

	<h2 id="r-download">Download</h2>
	The openML package can be downloaded from <a href="https://github.com/openml/openml-r"> GitHub</a>. It will also be available from CRAN in the near future.

	<h2 id="r-start">Tutorial</h2>
	See <a href="http://openml.github.io/openml-r" target="_blank">the tutorial</a> for the most important functions and examples of standard use cases.

	<h2 id="r-reference">Reference</h2>
	Full documentation on the packages is available from <a href="http://www.rdocumentation.org/packages/OpenML" target="_blank">R Documentation</a>.

	<h2 id="r-issues">Issues</h2>
	Having questions? Did you run into an issue? Let us know via the <a href="https://github.com/openml/r/issues"> OpenML R issue tracker</a>.
