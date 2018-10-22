        <a href="api_docs" class="btn btn-raised btn-info pull-right">API Documentation</a>
        <div class="page-header">
          <h1 id="dev-tutorial">REST tutorial</h1>
        </div>
       <p>OpenML offers a RESTful Web API, with predictive URLs, for uploading and downloading machine learning resources. Try the <a href="api_docs">API Documentation</a> to see examples of all calls, and test them right in your browser.
       <h2>Getting started</h2>
          <p>REST services can be called using simple HTTP GET or POST actions.</p>
          <p>The REST Endpoint URL is <code class="http">https://www.openml.org/api/v1/</code></p>
          <p>The default endpoint returns data in XML. If you prefer JSON, use the endpoint <code class="http">https://www.openml.org/api/v1/json/</code><br>
          Note that, to upload content, you still need to use XML (at least for now).</p>

       <h2>Testing</h2>
          <p>For continuous integration and testing purposes, we have a test server offering the same API, but which does not affect the production server.</p>
          <p>The REST Endpoint URL is <code class="http">https://test.openml.org/api/v1/</code></p>

      <h2>Error messages</h2>
          <p>Error messages will look like this:
          <div class="codehighlight">
            <pre class="pre-scrollable"><code class="html">&lt;oml:error xmlns:oml="http://openml.org/error"&gt;
  &lt;oml:code&gt;100&lt;/oml:code&gt;
  &lt;oml:message&gt;Please invoke legal function&lt;/oml:message&gt;
  &lt;oml:additional_information&gt;Additional information, not always available. &lt;/oml:additional_information&gt;
&lt;/oml:error&gt;
</code></pre>
          </div>
          <p>All error messages are listed in the API documentation. E.g. try to get a non-existing dataset:<br>
              in XML: <a href="https://www.openml.org/api_new/v1/data/99999">https://www.openml.org/api_new/v1/data/99999</a><br>
              in JSON: <a href="https://www.openml.org/api_new/v1/json/data/99999">https://www.openml.org/api_new/v1/json/data/99999</a></p>

        <h2>Examples</h2>
        You need to be logged in for these examples to work.

        <h5 id="dev-getdata">Download a dataset</h5>
        <img src="img/api_get_dataset.png" style="display: block;margin-left:auto;margin-right:auto;width:480px;padding:10px">
        <ol>
          <li>User asks for a dataset using the <a href="api_docs/#!/data/get_data_id">/data/{id}</a> service. The <code>dataset id</code> is typically part of a task, or can be found on OpenML.org.</li>
          <li>OpenML returns a description of the dataset as an XML file (or JSON). <a href="api_new/v1/data/1" type="button" class="btn btn-primary btn-xs" target="_blank">Try it now</a></li>
          <li>The dataset description contains the URL where the dataset can be downloaded. The user calls that URL to download the dataset.</li>
          <li>The dataset is returned by the server hosting the dataset. This can be OpenML, but also any other data repository. <a href="http://www.openml.org/data/download/1/dataset_1_anneal.arff" type="button" class="btn btn-primary btn-xs" target="_blank">Try it now</a></li>
        </ol>

        <h5 id="dev-getimpl">Download a flow</h5>
        <img src="img/api_get_implementation.png" style="display: block;margin-left:auto;margin-right:auto;width:480px;padding:10px">
        <ol>
          <li>User asks for a flow using the <a href="api_docs/#!/flow/get_flow_id">/flow/{id}</a> service and a <code>flow id</code>. The <code>flow id</code> can be found on OpenML.org.</li>
          <li>OpenML returns a description of the flow as an XML file (or JSON). <a href="api/v1/flow/65" type="button" class="btn btn-primary btn-xs" target="_blank">Try it now</a></li>
          <li>The flow description contains the URL where the flow can be downloaded (e.g. GitHub), either as source, binary or both, as well as additional information on history, dependencies and licence. The user calls the right URL to download it.</li>
          <li>The flow is returned by the server hosting it. This can be OpenML, but also any other code repository. <a href="http://sourceforge.net/projects/weka/files/weka-3-4/3.4.8/weka-3-4-8a.zip/download" type="button" class="btn btn-primary btn-xs" target="_blank">Try it now</a></li>
        </ol>

        <h5 id="dev-gettask">Download a task</h5>
        <img src="img/api_get_task.png" style="display: block;margin-left:auto;margin-right:auto;width:480px;padding:10px">
        <ol>
          <li>User asks for a task using the <a href="api_docs/#!/task/get_task_id">/task/{id}</a> service and a <code>task id</code>. The <code>task id</code> is typically returned when searching for tasks.</li>
          <li>OpenML returns a description of the task as an XML file (or JSON). <a href="api/v1/task/1" type="button" class="btn btn-primary btn-xs" target="_blank">Try it now</a></li>
          <li>The task description contains the <code>dataset id</code>(s) of the datasets involved in this task. The user asks for the dataset using the <a href="api_docs/#!/data/get_data_id">/data/{id}</a> service and the <code>dataset id</code>.</li>
          <li>OpenML returns a description of the dataset as an XML file (or JSON). <a href="<?php echo BASE_URL;?>api/v1/data/61" type="button" class="btn btn-primary btn-xs" target="_blank">Try it now</a></li>
          <li>The dataset description contains the URL where the dataset can be downloaded. The user calls that URL to download the dataset.</li>
          <li>The dataset is returned by the server hosting it. This can be OpenML, but also any other data repository. <a href="api_new/v1/data/61" type="button" class="btn btn-primary btn-xs" target="_blank">Try it now</a></li></li>
          <li>The task description may also contain links to other resources, such as the train-test splits to be used in cross-validation. The user calls that URL to download the train-test splits.</li>
          <li>The train-test splits are returned by OpenML. <a href="http://www.openml.org/api_splits/get/1/Task_1_splits.arff" type="button" class="btn btn-primary btn-xs" target="_blank">Try it now</a></li>
        </ol>
