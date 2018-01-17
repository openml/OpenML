          <div class="page-header">
            <h1>OpenML API's</h1>
          </div>
	<p>OpenML offers a range of APIs to download and upload OpenML datasets, tasks, run algorithms on them, and share the results.</p>
  <br>
  <ul class="apilist"><li>
  <div class="col-sm-2"><a target="_blank" href="api_docs"><img class="toolcircle" src="img/rest.png" alt="icon"></a></div>
  <p>The REST API allows you to talk directly to the OpenML server from any programming environment.<br>
  <a target="_blank" href="guide/rest" class="btn btn-raised btn-info">API tutorial</a>
  <a target="_blank" href="api_docs" class="btn btn-raised btn-info">API Documentation</a>
  <a target="_blank" href="api_data_docs" class="btn btn-raised btn-info">File API Documentation</a></p>

  </li><li>
  <div class="col-sm-2"><a target="_blank" href="https://openml.github.io/openml-python"><img class="toolcircle" src="img/python.png" alt="icon"></a></div>
  <p>Download datasets into Python scripts, build models using Python machine learning libraries (e.g., <i>scikit-learn</i>), and share the results online, all in a few lines of code.<br>
  <a target="_blank" href="https://openml.github.io/openml-python" class="btn btn-raised btn-info">Documentation</a>
  <a target="_blank" href="https://github.com/openml/openml-python/blob/master/examples/OpenML_Tutorial.ipynb" class="btn btn-raised btn-info">Jupyter Notebook</a>
  <a target="_blank" href="https://github.com/openml/openml-python/blob/cheatsheet/examples/OpenML_Cheat_Sheet.pdf" class="btn btn-raised btn-info">Cheat sheet</a>
  <a target="_blank" class="btn btn-raised btn-info" href="https://everware.ysda.yandex.net/hub/oauth_login?repourl=https://github.com/openml/study_example_python&OPENMLKEY=<?php echo (isset($this->api_key) ? $this->api_key : '');?>">Online demo</a>
</p>


    </li><li>
  <div class="col-sm-2"><a href="https://openml.github.io/openml-r"><img class="toolcircle" src="img/R.png" alt="icon"></a></div>
  <p>Download datasets into R scripts, build models using R machine learning packages (e.g. <i>mlr</i>), and share the results online, again in a few lines of code.<br>
  <a target="_blank" href="https://openml.github.io/openml-r" class="btn btn-raised btn-info">Documentation</a>
  <a target="_blank" href="http://openml.github.io/openml-r/vignettes/OpenML.html" class="btn btn-raised btn-info">Tutorial</a>
  <a target="_blank" href="https://github.com/openml/openml-r/blob/master/vignettes/openml-cheatsheet.pdf" class="btn btn-raised btn-info">Cheat sheet</a>
</p>

    </li><li>
  <div class="col-sm-2"><a href="docs"><img class="toolcircle" src="img/java.png" alt="icon"></a></div>
  <p>If you are building machine learning systems in Java, there is also an API for that.<br>
  <a href="https://github.com/openml/OpenML/wiki/Java-API" class="btn btn-raised btn-info">Tutorial</a>
  <a href="https://www.openml.org/docs" class="btn btn-raised btn-info">Java Docs</a></p>

    </li><li>
  <div class="col-sm-2"><a href="https://github.com/openml/openml-dotnet"><img class="toolcircle" src="img/c++.png" alt="icon"></a></div>
  <p>The C++ library is under development, but already contains most of the functions available.<br>
  <a href="https://github.com/openml/OpenML/wiki/.Net-API" class="btn btn-raised btn-info">Tutorial</a>
  <a href="https://github.com/openml/openml-dotnet" class="btn btn-raised btn-info">GitHub repo</a>

</li></ul>

  <h2>Easy authentication</h2>
  <p>In the interest of open science, we allow you to freely download all public resources, also through the APIs (rate limits apply when necessary).
  Uploading and sharing new datasets, tasks, flows and runs (or accessing any shared/private resources) is also very easy, and requires only the API key that you can find <a href="u#!api">in your profile</a> (after logging in).</p>
  <p>If you use any of the language-specific APIs, you only need to store this key in a config file and forget about it.</p>
  <p>For authenticating to the REST API, you can send your api key using Basic Auth, or by adding <code>?api_key='your key'</code> to your calls. If you are logged into OpenML.org, this will be done automatically.</p>


<!--
  <p>This example runs an scikit-learn algorithm on an <a href="t/10">OpenML task</a>.</p>
  <div class="codehighlight"><pre><code class="python">
    from sklearn import ensemble
    from openml import tasks,flows,runs
    import xmltodict

    # Download task, run learner, publish results
    task = tasks.get_task(10)
    clf = ensemble.RandomForestClassifier()
    flow = flows.sklearn_to_flow(clf)
    run = runs.run_flow_on_task(task, flow)
    run.publish()

    print("Uploaded run with id %s. Check it at www.openml.org/r/%s" %(run.run_id,run.run_id))
  </code></pre></div> -->
