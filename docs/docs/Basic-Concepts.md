# Basic Concepts

Researchers are encouraged to upload their experimental results on OpenML, so that these can be reused by anyone. Various high level papers  have been published that overview the design goals, benefits and opportunities (for example, at [ECML/PKDD 2013](http://link.springer.com/chapter/10.1007%2F978-3-642-40994-3_46), [SIGKDD Explorations](http://dl.acm.org/citation.cfm?id=2641198) and [JMLR](http://www.jmlr.org/proceedings/papers/v41/vanschoren15.html)). However, there is no clear overview of the basic concepts upon which the platform is build. In this blog post I will review these, and discuss some best practices. This page is a slightly updated version of [this blogpost](https://medium.com/open-machine-learning/basic-components-of-openml-a5745634c664)

## Data
One of the core components of OpenML are datasets. People can upload their datasets, and the system automatically organises these on line. An example of a dataset is the well-known [Iris dataset](http://www.openml.org/d/61). It shows all features, once of these is identified as the 'default target attribute', although this concept is flexible. It also shows some automatically computed data qualities (or, meta-features). Each dataset has its own unique ID.

Information about the dataset, the data features and the data qualities can be obtained automatically by means of the following API functions:

* [Get all available datasets](http://www.openml.org/api_docs/#!/data/get_data_list)
* [Get dataset](http://www.openml.org/api_docs/#!/data/get_data_id) (required the data id)
* [Get data features](http://www.openml.org/api_docs/#!/data/get_data_features_id) (requires the data id)
* [Get data qualities](http://www.openml.org/api_docs/#!/data/get_data_qualities_id) (requires the data id)

## Task types and tasks
A dataset alone does not constitute a scientific task. We must first agree on what types of results are expected to be shared. This is expressed in task types: they define what types of inputs are given, which types of output are expected to be returned, and what protocols should be used. For instance, classification tasks should include well-defined cross-validation procedures, labelled input data, and require predictions as outputs. The collection of all this information together is called a task. The Iris dataset has various tasks defined on it, [for example this one](http://www.openml.org/t/59). Although the web-interface does not show it, this task formally describes the target attribute that should be modelled (in this case the same as the default target attribute of the dataset, but this is flexible), the quality estimation procedure (10-fold cross-validation), the evaluation measure (predictive accuracy) and the cross-validation folds.

Useful API operations include:

* [Get all available tasks](http://www.openml.org/api_docs/#!/task/get_task_list)
* [Get all available tasks of a given type](http://www.openml.org/api_docs/#!/task/get_task_list_type_id) (e.g. get all Classification tasks, requires the id of the task type)
* [Get the details of a task](http://www.openml.org/api_docs/#!/task/get_task_id) (requires task id)

Currently, there are a wide range of task types defined on OpenML, including classification, regression, on line learning, clustering and subgroup discovery. Although this set can be extended, this is currently not a supported API operation (meaning that we will add them by hand). If you interested in task types that are currently not supported, please contact us. 

## Flows
Tasks can be 'solved' by classifiers (or algorithms, workflows, flows). OpenML stores references to these flows. It is important to stress that flows are actually ran on the computer of the user, only meta-information about the flow is stored on OpenML. This  information includes basic trivialities such as the creator, toolbox and compilation instructions, but also more formal description about hyper parameter. A flow can also contain subflows, for example, the flow Bagging can have a subflow 'Decision Tree' which would make the flow 'Bagging of Decision Trees'. A flow is distinguished by its name and 'external version', which are both provided by the uploader. When uploading a flow, it is important to think about a good naming convention for the both, for example, the git commit number could be used as external version, as this uniquely identifies a state of the code. Ideally, when two persons are using the same flow, they will use the same name and external version, so that results of the flows can be compared across tasks. (This is ensured when using the toolboxed in which OpenML is integrated, such as Weka, Scikit Learn and MLR).

Useful API functions are:

* [List all flows](http://www.openml.org/api_docs/#!/flow/get_flow_list) 
* [List all my flows](http://www.openml.org/api_docs/#!/flow/get_flow_owned)
* [Give details about a given flow](http://www.openml.org/api_docs/#!/flow/get_flow_id) (requires flow id)

## Runs
Whenever a flow executes a task, this is called a run. The existence of runs is actually the main contribution of OpenML. Some experiments take weeks to complete, and having the results stored on OpenML helps other researchers reuse the experiments. The task description specifies which information should be uploaded in order to have a valid run, in most cases, for each cross-validation fold the predictions on the test set. This allows OpenML to calculate basic evaluation measures, such as predictive accuracy, ROC curves and many more. Also information about the flow and hyper parameter settings should be provided.

Some useful API functions:

* [List all runs performed on a given task](http://www.openml.org/api_docs/#!/run/get_run_list_task_ids) (requires task id, e.g., the iris task is 59)
* [Compare two flows on all tasks](http://www.openml.org/api_docs/#!/run/get_run_list_filters) (requires a comma separated list of flow ids, e.g., 1720, 1721 for comparing k-nn with a decision tree)
* And many more ...

Usually, the result is in some XML or JSON format (depending on the preference of the user), linking together various task ids, flow ids, etc. In order for this to become meaningful, the user needs to perform other API tasks to get information about what flows were executed, what tasks and datasets were used, etc. Details about this will be provided in another post.

## Setups
Every run that is executed by a flow, contains information about the hyper parameter settings of the flow. A setup is the combination of all parameter settings of a given flow. OpenML internally links the result of a given run to a setup id. This way, experiments can be done across hyper parameter settings.

For example,  

* [Compare two setups on all tasks](http://www.openml.org/api_docs/#!/run/get_run_list_filters) (requires a comma separated list of setup ids, e.g., 8994, 8995, 8996 for comparing multiple MLP configurations)

As setups constitute a complex concept, most of the operations concerning setups are hidden from the user. Hence, not all setup functions are properly documented yet. For example, these do not contain a page on the webinterface.
