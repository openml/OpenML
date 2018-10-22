          <div class="page-header">
            <h1>.NET API</h1>
          </div>
        The .Net API allows you connect to OpenML from .Net applications.
	<h2 id="net-download">Download</h2>
	<p>Stable releases of the .Net API are available via
	<a href="https://www.nuget.org/packages/openMl">NuGet</a>.
	Use the NuGet package explorer in the Visual Studia, write “Install-Package openMl”
	to the NuGet package manager console or download the whole package from the NuGet website
	and add it into your project. Or, you can check out the developer version from
	<a href="https://github.com/openml/dotnet"> GitHub</a>.

	<h3 id="net-start">Quick Start</h3>
	<p>Create an <code>OpenmlConnector</code> instance with your api key.
	You can find this key in your account settings. This will create a client with OpenML functionalities, The functionalities mirror the OpenMlApi and not all of them are (yet) implemented. If you need some feature, don’t hesitate contact us via our Git page.</p>
	<div class="codehighlight"><pre>
    <code class="cs">var connector = new OpenMlConnector("YOURAPIKEY");</code></pre>
  </div>
	<p>All OpenMlConnector methods are documented via the usual .Net comments.</p>

	<h4>Get dataset description</h4>
	<p>
	<div class="codehighlight"><pre>
    <code class="cs">var datasetDescription = connector.GetDatasetDescription(1);</code></pre>
  </div>
	</p>

	<h4>List datasets</h4>
	<p>
	<div class="codehighlight"><pre>
    <code class="cs">var data = connector.ListDatasets();</code></pre>
  </div>
	</p>

	<h4>Get run</h4>
	<p>
	<div class="codehighlight"><pre>
    <code class="cs">var run = connector.GetRun(1);</code></pre>
  </div>
	</p>

	<h4>List task types</h4>
	<p>
	<div class="codehighlight"><pre>
    <code class="cs">var taskTypes = connector.ListTaskTypes();</code></pre>
  </div>
	</p>

	<h4>Get task type</h4>
	<p>
	<div class="codehighlight"><pre>
    <code class="cs">var taskType = connector.GetTaskType(1);</code></pre>
  </div>
	</p>

	<h4>List evaluation measures</h4>
	<p>
	<div class="codehighlight"><pre>
    <code class="cs">var measures = connector.ListEvaluationMeasures();</code></pre>
  </div>
	</p>

	<h4>List estimation procedures</h4>
	<p>
	<div class="codehighlight"><pre>
    <code class="cs">var estimationProcs = connector.ListEstimationProcedures();</code></pre>
  </div>
	</p>

	<h4>Get estimation procedure</h4>
	<p>
	<div class="codehighlight"><pre>
    <code class="cs">var estimationProc = connector.GetEstimationProcedure(1);</code></pre>
  </div>
	</p>

	<h4>List data qualities</h4>
	<p>
	<div class="codehighlight"><pre>
    <code class="cs">var dataQualities = connector.ListDataQualities();</code></pre>
  </div>
	</p>

	<h3 id="net-sql">Free SQL Query</h3>
	<h5><code>openmlFreeQuery(String sql)</code></h5>
	<p>Executes the given SQL query and returns the result in .Net format.</p>
	<div class="codehighlight"><pre>
    <code class="cs">var result=connector.ExecuteFreeQuery("SELECT name,did FROM dataset");</code></pre>
  </div>

	<h3 id="net-issues">Issues</h3>
	Having questions? Did you run into an issue? Let us know via the
	<a href="https://github.com/openml/dotnet/issues"> OpenML .Net issue tracker</a>.
