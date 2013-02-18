OpenML: Open Machine Learning
=============================
## Aim
OpenML aims to create a novel ecosystem for machine learning experimentation. The current generation of machine learning 
and data mining platforms offers a wide variety of algorithms to process and model all kinds of data. They also offer 
convenient ways for running many experiments to assess, select and fine-tune algorithm performance and optimize workflows. 
On the whole, however, users only have access to their own experiments: there is no global repository for machine learning 
experiments, nor a standardized way to share experiments with other users. This means that a lot of valuable knowledge 
about machine learning techniques is lost. OpenML is an experiment sharing platform to share detailed experimental 
results with the community at large and organize them for future reuse. Moreover, it will be directly integrated in 
today’s most popular data mining tools (for now: R, KNIME, RapidMiner and WEKA). Such an easy and free exchange of 
experiments has tremendous potential to speed up machine learning research, to engender larger, more detailed studies 
and to offer accurate advice to practitioners. Finally, it will also be a valuable resource for education in machine 
learning and data mining.

## How does it work?
OpenML works much like a data mining challenge platform, such as Kaggle. Users can define *TASKS* (or challenges of you will):
well-described problems to be solved by a machine learning workflow. A task contains a set of input data, some parameters,
and a set of expected results. 

For instance, a "Supervised Classification" task will contain a dataset, an estimation procedure (e.g. 10-fold cross-validation),
an evaluation measure to optimize for, and will require you to upload a workflow together with predictions for all the
input instances. It will also provide train-test splits so that you results can be compared by those of other users. See
below for a fully detailed example.

Users can also define new *TASK TYPES* for any other machine learning task, by defining what the inputs, parameters and outputs are.
However, some task types, like supervised classification, receive additional support. For instance, when uploading predictions,
OpenML will calculate a large range of different performance metrics and store those in a database.

The OpenML database stores and links all information regarding the experiments: the datasets (although large datasets will
be hosted by MLData.org), algorithms/workflows (uploaded by the user), results (predictions and computed measures), and authors.
The OpenML website (in progress, preview at http://expdb.cs.kuleuven.be/expdb/) will allow easy querying for all this information.
Based on simple keyword queries you can look for authors, datasets, algorithms. From a dataset, you can look at all results obtained
on that dataset, for an algorithm or workflow, you can look at all results of that algorithm on different algorithms.

The key point of OpenML is that all this should be made possible with minimal effort by the user. OpenML offers a 
RESTful API for finding and downloading tasks and datasets and uploading implementations and results. This will be integrated
in key machine learning toolboxes (for now: R, KNIME, RapidMiner and WEKA), so that experiments can be run and shared at 
the click of a button. Custom libraries can also link to OpenML by using the API services they need. 

Downloading of tasks and datasets is completely open, but users will need to register on the OpenML website to upload
results.

# API Overview
Also check the auto-generated (frequently refreshed) overview at: http://expdb.cs.kuleuven.be/expdb/api/.

OpenML offers a RESTful API. Interaction with the API happens as follows:
![task.search](https://raw.github.com/joaquinvanschoren/OpenML/master/Documentation/openml.general.png)
Jump to the following scenario's:
* [Getting a machine learning task](#gettask)
* [Getting a dataset](#getdata)
* [Getting an implementation](#getimpl)
* [Authenticate user](#auth)
* [Sharing a dataset](#sharedata)
* [Sharing an implementation](#shareimpl)
* [Sharing a run](#sharerun)
* [Checking an implementation](#checkimpl)

## <a id="gettask"></a> Getting a machine learning task
![task.search](https://raw.github.com/joaquinvanschoren/OpenML/master/Documentation/openml.tasks.search.png)

The user wants the workbench to load a specific task. A task_id is sent to the API, which will return a task file, or an error if the task is unknown.

#### Service: [openml.tasks.search](http://expdb.cs.kuleuven.be/expdb/api/index.php#openml_tasks_search) 
Returns a task given a task_id. [Try it!](http://expdb.cs.kuleuven.be/expdb/api/?f=openml.tasks.search&task_id=1)

#### Returned file: [Task](https://raw.github.com/joaquinvanschoren/OpenML/master/XML/Schemas/task.xsd) 
A task defines exactly what input data is given to, and what output data is expected from the implementation. See the [XSD Schema](https://raw.github.com/joaquinvanschoren/OpenML/master/XML/Schemas/task.xsd) for details.

Example:
```xml
<oml:task xmlns:oml="http://open-ml.org/openml">
  <oml:task_id>1234</oml:task_id>
	<oml:task_type>Supervised Classification</oml:task_type>
	<oml:input name="source_data">
		<oml:data_set>
			<oml:data_set_id>1</oml:data_set_id>
			<oml:target_feature>class</oml:target_feature>
		</oml:data_set>
	</oml:input>
	<oml:input name="estimation_procedure">
		<oml:estimation_procedure>
		        <oml:type>cross-validation</oml:type>
			<oml:data_splits_id>1001</oml:data_splits_id>
			<oml:parameter name="number_repeats">2</oml:parameter>
			<oml:parameter name="number_folds">10</oml:parameter>
		</oml:estimation_procedure>
	</oml:input>
	<oml:input name="evaluation_measures">  
		<oml:evaluation_measures>
			<oml:evaluation_measure>predictive_accuracy</oml:evaluation_measure>
		</oml:evaluation_measures>
	</oml:input>
	<oml:output name="predictions">
		<oml:predictions>
			<oml:name>predictions</oml:name>
			<oml:format>ARFF</oml:format>
			<oml:feature name="repeat" type="integer" required="true"/>
			<oml:feature name="fold"   type="integer" required="true"/>
			<oml:feature name="row_id" type="string" required="true"/>
			<oml:feature name="prediction" type="string" required="true"/>
			<oml:feature name="confidence.classvalue" type="numeric" required="false"/>
		</oml:predictions>
	</oml:output>
</oml:task>
```

### Getting train-test splits
![data.description](https://raw.github.com/joaquinvanschoren/OpenML/master/Documentation/openml.tasks.splits.png)

Some tasks define explicit train-test splits, so that results from different users can be compared. These splits are given in an ARFF file which can be directly downloaded using the data_splits_url given in the task description.

Example: For a repeated n-fold cross-validation procedure, the data splits file will state, for each of the n folds and m repeats, whether a certain row_id is in the train or test set:
```xml
@RELATION Iris_splits

@ATTRIBUTE type {TRAIN,TEST}
@ATTRIBUTE rowid STRING
@ATTRIBUTE fold INTEGER
@ATTRIBUTE repeat INTEGER

@DATA
TRAIN,140,9,1
TRAIN,123,9,1
TRAIN,110,9,1
TRAIN,137,9,1
TRAIN,104,9,1
...
```

***

## <a id="getdata"></a> Getting a dataset
![data.description](https://raw.github.com/joaquinvanschoren/OpenML/master/Documentation/openml.data.description.png)

Tasks contain input datasets which need to be downloaded. Sending the data_set_id to the API returns an XML file which describes the dataset (or an error if the data_set_id is unknown). It also contains a url from which the data can be downloaded directly.

#### Service: [openml.data.description](http://expdb.cs.kuleuven.be/expdb/api/index.php#openml_data_description)
Returns a dataset description given a data_set_id. [Try it!](http://expdb.cs.kuleuven.be/expdb/api/?f=openml.data.description&data_id=1)

#### Returned file: [Data set](https://raw.github.com/joaquinvanschoren/OpenML/master/XML/Schemas/dataset_get.xsd) 
A dataset has a URL where it can be downloaded, as well as additional information on history, format and licence. See the [XSD Schema](https://raw.github.com/joaquinvanschoren/OpenML/master/XML/Schemas/dataset_get.xsd) for details.

Example:
```xml
<oml:data_set_description xmlns:oml="http://open-ml.org/openml">
    <oml:id>1</oml:id>
    <oml:name>Iris</oml:name>
    <oml:version>1.0</oml:version>
    <oml:creator>R.A. Fisher</oml:creator>
    <oml:contributor>Michael Marshall</oml:contributor>
    <oml:collection_date>July, 1988</oml:collection_date>
    <oml:upload_date>2013-01-25 12:10:04</oml:upload_date>
    <oml:description>Iris Plants Database</oml:description>
    <oml:language>English</oml:language>
    <oml:format>ARFF</oml:format>
    <oml:licence>UCI</oml:licence>
    <oml:url>http://expdb.cs.kuleuven.be/expdb/data/uci/nominal/iris.arff</oml:url>
    <oml:md5_checksum>b031a685d8d40136449d94365f9a8b81</oml:md5_checksum>
</oml:data_set_description>
```

***

## <a id="getimpl"></a> Getting an implementation
![implementation.get](https://raw.github.com/joaquinvanschoren/OpenML/master/Documentation/openml.implementation.get.png)
The user wants the workbench to import a previously shared implementation. Sending the implementation_id to the API returns an XML file which describes the implementation (or an error if the implementation_id is unknown). It also contains a url from which the implementation can be downloaded directly.

#### Service: [openml.implementation.get](http://expdb.cs.kuleuven.be/expdb/api/index.php#openml_implementation_get) 
Returns an implementation description given an implementation_id. [Try it!](http://expdb.cs.kuleuven.be/expdb/api/?f=openml.implementation.get&implementation_id=1)

#### Returned file: [Implementation](https://raw.github.com/joaquinvanschoren/OpenML/master/XML/Schemas/implementation.xsd) 
An implementation has a URL where it can be downloaded, either as source, binary or both, as well as additional information on history, dependencies and licence. See the [XSD Schema](https://raw.github.com/joaquinvanschoren/OpenML/master/XML/Schemas/implementation.xsd) for details.

Example:
```xml
<oml:implementation xmlns:oml="http://open-ml.org/openml">
	<oml:id>weka.J48_1.2</oml:id>
	<oml:name>weka.J48</oml:name>
	<oml:version>1.2</oml:version>
	<oml:creator>Eibe Frank</oml:creator>
	<oml:licence>public domain</oml:licence>
	<oml:language>English</oml:language>
	<oml:description>Class for generating an unpruned or a pruned C4.5 decision tree.</oml:description>  
	<oml:dependencies>WEKA 3.7</oml:dependencies>
	<oml:bibliographical_reference>
		<oml:citation>Ross Quinlan (1993). C4.5: Programs for Machine Learning, Morgan Kaufmann Publishers, San Mateo, CA.</oml:citation>
		<oml:url>http://...</oml:url>
	</oml:bibliographical_reference>
	<oml:source_format>zip</oml:source_format>
	<oml:binary_format>zip</oml:binary_format>
	<oml:source_md5>9e107d9d372bb6826bd81d3542a419d6</oml:source_md5>
	<oml:binary_md5>e4d909c290d0fb1ca068ffaddf22cbd0</oml:binary_md5>
</oml:implementation>
```

***

## <a id="auth"></a> Authenticate User
![authenticate](https://raw.github.com/joaquinvanschoren/OpenML/master/Documentation/openml.authenticate.png)
To upload data, the user first has to authenticate herself. The workbench sends the username and hashed password to the API, which will return an authentication session token, or an error if the login failed.

#### Service: [openml.authenticate](http://expdb.cs.kuleuven.be/expdb/api/index.php#openml_authenticate) 
Given a user name and hashed password, the service return an authentication session token, or an error if the login failed.

***

## <a id="sharedata"></a> Sharing a dataset
![data.upload](https://raw.github.com/joaquinvanschoren/OpenML/master/Documentation/openml.data.upload.png)
The user wants to share a new dataset. Given additional information about the dataset, the workbench creates an XML file to describe the dataset, which is uploaded together with the dataset file itself.

#### Service: [openml.data.upload](http://expdb.cs.kuleuven.be/expdb/api/index.php#openml_data_upload) 
Given an authentication token, a dataset description file and the file holding the dataset itself, the service will store the dataset and return a dataset_id. Returns an error if the authentication token is invalid (e.g., expired).

#### Required file: [Data set](https://raw.github.com/joaquinvanschoren/OpenML/master/XML/Schemas/dataset.xsd) 
The dataset description file should at least contain a name, description and creator. See the [XSD Schema](https://raw.github.com/joaquinvanschoren/OpenML/master/XML/Schemas/dataset.xsd) for details.

Minimal Example:
```xml
<oml:data_set_description xmlns:oml="http://open-ml.org/openml">
    <oml:name>Iris</oml:name>
    <oml:creator>R.A. Fisher</oml:creator>
    <oml:description>Iris Plants Database</oml:description>
</oml:data_set_description>
```

#### Required file: [ARFF Data set]
The file containing the dataset. This should be an ARFF file.

***

## <a id="shareimpl"></a> Sharing an implementation
![implementation.upload](https://raw.github.com/joaquinvanschoren/OpenML/master/Documentation/openml.implementation.upload.png)
The user wants to share a new implementation. Given additional information about the implementation, the workbench creates an XML file to describe the implementation, which is uploaded together with the implementation's source and/or binary files.

#### Service: [openml.implementation.upload](http://expdb.cs.kuleuven.be/expdb/api/index.php#openml_implementation_upload)
Given an authentication token, an implementation description file and the file holding the implementation source and/or binary, the service will store the implementation and return an implementation_id. This id will be generated based on the implementation name and version, if given. An error is returned if the authentication token is invalid (e.g., expired), or if a name/version combination has been given that is not unique.

#### Required file: [Implementation](https://raw.github.com/joaquinvanschoren/OpenML/master/XML/Schemas/implementation_upload.xsd) 
The implementation description file should at least contain a name and description. See the [XSD Schema](https://raw.github.com/joaquinvanschoren/OpenML/master/XML/Schemas/implementation_upload.xsd) for details.

Minimal Example:
```xml
<oml:data_set_description xmlns:oml="http://open-ml.org/openml">
    <oml:name>MyWorkflow</oml:name>
    <oml:description>Optimized Workflow for Task T</oml:description>
</oml:data_set_description>
```

#### Required file: [source code file]
A file containing the implementation source code. Will often be a ZIP file.

#### Required file: [binary file]
A file containing the implementation source code. Will often be a ZIP file.

***

## <a id="sharerun"></a> Sharing a run
![run.upload](https://raw.github.com/joaquinvanschoren/OpenML/master/Documentation/openml.run.upload.png)
The users wants to share a run of his implementation on a given machine learning task. The workbench creates an XML file to describe the run, which is uploaded together with any result files generated by the run. Optionally, parameter variations can be added to the run description.

#### Service: [openml.run.upload](http://expdb.cs.kuleuven.be/expdb/api/index.php#openml_run_upload) 
Given an authentication token, an run description file and any result files required for the task, the service will store the results and return a response depending on the task type. An error is returned if the authentication token is invalid, or if the given task_id or implementation_id are unknown.

#### Required file: [Run](https://raw.github.com/joaquinvanschoren/OpenML/master/XML/Schemas/run.xsd) 
The run description file should at least contain the task_id and implementation_id, and optionally any parameter setting that are specific for this run. See the [XSD Schema](https://raw.github.com/joaquinvanschoren/OpenML/master/XML/Schemas/run.xsd) for details.

Minimal Example:
```xml
<oml:run xmlns:oml="http://open-ml.org/openml">
	<oml:task_id>1</oml:task_id>
	<oml:implementation_id>knime.user.myworkflow_1.2</oml:implementation_id>
</oml:run>
```

#### Required file: [result files]
A file containing the output files required by the task type used. For supervised classification, this will be a table with predictions.

Example:
```xml
@relation openml_task_123_predictions

@ATTRIBUTE repeat INTEGER
@ATTRIBUTE fold INTEGER
@ATTRIBUTE rowid INTEGER
@ATTRIBUTE prediction {setosa,versicolor,virgina}
@ATTRIBUTE confidence.setosa NUMERIC
@ATTRIBUTE confidence.versicolor NUMERIC 
@ATTRIBUTE confidence.virgina NUMERIC

@data
1,1,1,"setosa",0.6,0.2,0.2
1,1,2,"versicolor",0.2,0.6,0.2
1,2,1,"setosa",0.6,0.2,0.2
1,2,2,"virgina",0.1,0.1,0.8
2,1,2,"setosa",0.6,0.2,0.2
2,1,2,"setosa",0.6,0.2,0.2
2,2,2,"setosa",0.6,0.2,0.2
...
```


#### Returned file: [Response](https://raw.github.com/joaquinvanschoren/OpenML/master/XML/Schemas/response.xsd) 
The response file returned implementation description file depending on the task type. For supervised classification, the API will also compute evaluation measures based on uploaded predictions, and return them as part of the response file. See the [XSD Schema](https://raw.github.com/joaquinvanschoren/OpenML/master/XML/Schemas/response.xsd) for details.

Example:
```xml
<oml:response xmlns:oml="http://open-ml.org/openml">
</oml:response>
```

***

## <a id="checkimpl"></a> Checking an implementation
![implementation.check](https://raw.github.com/joaquinvanschoren/OpenML/master/Documentation/openml.implementation.check.png)
The workbench wants to check whether an implementation with the given id is already registered.

#### Service: [openml.implementation.check](http://expdb.cs.kuleuven.be/expdb/api/index.php#openml_implementation_check) 
Returns 'registered' if an implementation with the given id exists in the database, 'unregistered' otherwise.
