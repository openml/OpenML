The .Net API allows you connect to OpenML from .Net applications.

## Download

Stable releases of the .Net API are available via [NuGet](https://www.nuget.org/packages/openMl). Use the NuGet package explorer in the Visual Studia, write “Install-Package openMl” to the NuGet package manager console or download the whole package from the NuGet website and add it into your project. Or, you can check out the developer version from [GitHub](https://github.com/openml/dotnet).

### Quick Start

Create an `OpenmlConnector` instance with your api key. You can find this key in your account settings. This will create a client with OpenML functionalities, The functionalities mirror the OpenMlApi and not all of them are (yet) implemented. If you need some feature, don’t hesitate contact us via our Git page.

<div class="codehighlight">

<pre>    `var connector = new OpenMlConnector("YOURAPIKEY");`</pre>

</div>

All OpenMlConnector methods are documented via the usual .Net comments.

#### Get dataset description

<div class="codehighlight">

<pre>    `var datasetDescription = connector.GetDatasetDescription(1);`</pre>

</div>

#### List datasets

<div class="codehighlight">

<pre>    `var data = connector.ListDatasets();`</pre>

</div>

#### Get run

<div class="codehighlight">

<pre>    `var run = connector.GetRun(1);`</pre>

</div>

#### List task types

<div class="codehighlight">

<pre>    `var taskTypes = connector.ListTaskTypes();`</pre>

</div>

#### Get task type

<div class="codehighlight">

<pre>    `var taskType = connector.GetTaskType(1);`</pre>

</div>

#### List evaluation measures

<div class="codehighlight">

<pre>    `var measures = connector.ListEvaluationMeasures();`</pre>

</div>

#### List estimation procedures

<div class="codehighlight">

<pre>    `var estimationProcs = connector.ListEstimationProcedures();`</pre>

</div>

#### Get estimation procedure

<div class="codehighlight">

<pre>    `var estimationProc = connector.GetEstimationProcedure(1);`</pre>

</div>

#### List data qualities

<div class="codehighlight">

<pre>    `var dataQualities = connector.ListDataQualities();`</pre>

</div>