The Java API allows you connect to OpenML from Java applications.

## Java Docs

[Read the full Java Docs](https://openml.github.io/java/)

## Download
Stable releases of the Java API are available from [Maven Central](https://search.maven.org/search?q=a:apiconnector)
Or, you can check out the developer version from [GitHub](https://github.com/openml/java)

Include the jar file in your projects as usual, or [install via Maven](http://maven.apache.org/guides/getting-started/maven-in-five-minutes.html).

## Quick Start
* Create an <code>OpenmlConnector</code> instance with your authentication details. This will create a client with all OpenML functionalities.
> OpenmlConnector client = new OpenmlConnector("api_key")

All functions are described in the [Java Docs](https://www.openml.org/docs).

### Downloading
To download data, flows, tasks, runs, etc. you need the unique id of that resource. The id is shown on each item's webpage and in the corresponding url. For instance, let's download [Data set 1](d/1). The following returns a DataSetDescription object that contains all information about that data set.

```
DataSetDescription data = client.dataGet(1);
```

You can also [search](search) for the items you need online, and click the icon to get all id's that match a search.

### Uploading

To upload data, flows, runs, etc. you need to provide a description of the object. We provide wrapper classes to provide this information, e.g. `DataSetDescription`, as well as to capture the server response, e.g. `UploadDataSet`, which always includes the generated id for reference:

```
DataSetDescription description = new DataSetDescription( "iris", "The famous iris dataset", "arff", "class");
UploadDataSet result = client.dataUpload( description, datasetFile );
int data_id = result.getId();
```

More details are given in the corresponding functions below. Also see the [Java Docs](docs) for all possible inputs and return values.

### Data download

#### `dataGet(int data_id)`

Retrieves the description of a specified data set.

```
DataSetDescription data = client.dataGet(1);
String name = data.getName();
String version = data.getVersion();
String description = data.getDescription();
String url = data.getUrl();
```

#### `dataFeatures(int data_id)`

Retrieves the description of the features of a specified data set.

```
DataFeature reponse = client.dataFeatures(1);
DataFeature.Feature[] features = reponse.getFeatures();
String name = features[0].getName();
String type = features[0].getDataType();
boolean	isTarget = features[0].getIs_target();
```

#### `dataQuality(int data_id)`

Retrieves the description of the qualities (meta-features) of a specified data set.

```
    DataQuality response = client.dataQuality(1);
    DataQuality.Quality[] qualities = reponse.getQualities();
    String name = qualities[0].getName();
    String value = qualities[0].getValue();
```

#### `dataQuality(int data_id, int start, int end, int interval_size)`

For data streams. Retrieves the description of the qualities (meta-features) of a specified portion of a data stream.

```
    DataQuality qualities = client.dataQuality(1,0,10000,null);
```

#### `dataQualityList()`

Retrieves a list of all data qualities known to OpenML.

```
    DataQualityList response = client.dataQualityList();
    String[] qualities = response.getQualities();
```

### Data upload

#### `dataUpload(DataSetDescription description, File dataset)`

Uploads a data set file to OpenML given a description. Throws an exception if the upload failed, see [openml.data.upload](#openml_data_upload) for error codes.

```
    DataSetDescription dataset = new DataSetDescription( "iris", "The iris dataset", "arff", "class");
    UploadDataSet data = client.dataUpload( dataset, new File("data/path"));
    int data_id = result.getId();
```


#### `dataUpload(DataSetDescription description)`

Registers an existing dataset (hosted elsewhere). The description needs to include the url of the data set. Throws an exception if the upload failed, see [openml.data.upload](#openml_data_upload) for error codes.

```
    DataSetDescription description = new DataSetDescription( "iris", "The iris dataset", "arff", "class");
    description.setUrl("http://datarepository.org/mydataset");
    UploadDataSet data = client.dataUpload( description );
    int data_id = result.getId();
```

### Flow download

#### `flowGet(int flow_id)`

Retrieves the description of the flow/implementation with the given id.

```
    Implementation flow = client.flowGet(100);
    String name = flow.getName();
    String version = flow.getVersion();
    String description = flow.getDescription();
    String binary_url = flow.getBinary_url();
    String source_url = flow.getSource_url();
    Parameter[] parameters = flow.getParameter();
```

### Flow management

#### `flowOwned()`

Retrieves an array of id's of all flows/implementations owned by you.

```
    ImplementationOwned response = client.flowOwned();
    Integer[] ids = response.getIds();
```

#### `flowExists(String name, String version)`

Checks whether an implementation with the given name and version is already registered on OpenML.

```
    ImplementationExists check = client.flowExists("weka.j48", "3.7.12");
    boolean exists = check.exists();
    int flow_id = check.getId();
```

#### `flowDelete(int id)`

Removes the flow with the given id (if you are its owner).

```
    ImplementationDelete response = client.openmlImplementationDelete(100);
```

### Flow upload

#### `flowUpload(Implementation description, File binary, File source)`

Uploads implementation files (binary and/or source) to OpenML given a description.

```
    Implementation flow = new Implementation("weka.J48", "3.7.12", "description", "Java", "WEKA 3.7.12")
    UploadImplementation response = client.flowUpload( flow, new File("code.jar"), new File("source.zip"));
    int flow_id = response.getId();
```

### Task download

#### `taskGet(int task_id)`

Retrieves the description of the task with the given id.

```
    Task task = client.taskGet(1);
    String task_type = task.getTask_type();
    Input[] inputs = task.getInputs();
    Output[] outputs = task.getOutputs();
```

#### `taskEvaluations(int task_id)`

Retrieves all evaluations for the task with the given id.

```
    TaskEvaluations response = client.taskEvaluations(1);
    Evaluation[] evaluations = response.getEvaluation();
```

#### `taskEvaluations(int task_id, int start, int end, int interval_size)`

For data streams. Retrieves all evaluations for the task over the specified window of the stream.

```
    TaskEvaluations response = client.taskEvaluations(1);
    Evaluation[] evaluations = response.getEvaluation();
```

### Run download

#### `runGet(int run_id)`

Retrieves the description of the run with the given id.

```
    Run run = client.runGet(1);
    int task_id = run.getTask_id();
    int flow_id = run.getImplementation_id();
    Parameter_setting[] settings = run.getParameter_settings()
    EvaluationScore[] scores = run.getOutputEvaluation();
```

### Run management

#### `runDelete(int run_id)`

Deletes the run with the given id (if you are its owner).

```
    RunDelete response = client.runDelete(1);
```

### Run upload

#### `runUpload(Run description, Map<String,File> output_files)`

Uploads a run to OpenML, including a description and a set of output files depending on the task type.

```
    Run.Parameter_setting[] parameter_settings = new Run.Parameter_setting[1];
    parameter_settings[0] = Run.Parameter_setting(null, "M", "2");
    Run run = new Run("1", null, "100", "setup_string", parameter_settings);
    Map outputs = new HashMap<String,File>();
    outputs.add("predictions",new File("predictions.arff"));
    UploadRun response = client.runUpload( run, outputs);
    int run_id = response.getRun_id();
```
