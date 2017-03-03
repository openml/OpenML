[![Stories in Ready](https://badge.waffle.io/openml/OpenML.png?label=ready&title=Ready)](https://waffle.io/openml/OpenML)
[![Stories in In Progress](https://badge.waffle.io/openml/OpenML.png?label=in%20progress&title=In%20Progress)](https://waffle.io/openml/OpenML)

OpenML: Open Machine Learning
=============================
How to contribute: https://github.com/openml/OpenML/wiki/How-to-contribute  
Developers: to keep track of issues, we use Waffle: https://waffle.io/openml/OpenML  
Citation and Honor Code: http://openml.org/guide/#!terms  
Google Group / Email List: https://groups.google.com/forum/?hl=de#!forum/openml

## Aim
OpenML aims to create a novel ecosystem for machine learning experimentation. The current generation of machine learning 
and data mining platforms offers a wide variety of algorithms to process and model all kinds of data. They also offer 
convenient ways for running many experiments to assess, select and fine-tune algorithm performance and optimize workflows. 
On the whole, however, users only have access to their own experiments: there is no global repository for machine learning 
experiments, nor a standardized way to share experiments with other users. This means that a lot of valuable knowledge 
about machine learning techniques is lost. OpenML is a platform to share detailed experimental 
results with the community at large and organize them for future reuse. Moreover, it will be directly integrated in 
today’s most popular data mining tools (for now: R, KNIME, RapidMiner and WEKA). Such an easy and free exchange of 
experiments has tremendous potential to speed up machine learning research, to engender larger, more detailed studies 
and to offer accurate advice to practitioners. Finally, it will also be a valuable resource for education in machine 
learning and data mining.

## How does it work?
OpenML works much like a data mining challenge platform, such as Kaggle, except that solutions are constructed collaboratively, with anybody free to build on other people's work, instead of a competition setting, where all progress is kept secret.

Researchers can define *TASKS* (or challenges of you will):
well-described problems to be solved by a machine learning workflow. A task contains a set of input data, some parameters,
and a set of expected results. 

For instance, a "Supervised Classification" task will contain a dataset, an estimation procedure (e.g. 10-fold cross-validation),
an evaluation measure to optimize for, and will require you to upload a workflow together with predictions for all the
input instances. It will also provide train-test splits so that your results can be compared with those of other users.

Users can also define new *TASK TYPES* for any other machine learning task, by defining what the inputs, parameters and outputs are.
However, some task types, like supervised classification, receive additional support. For instance, when uploading predictions,
OpenML will calculate a large range of different performance metrics and store those in a database.

The OpenML database stores and links all information regarding the experiments: the datasets, algorithms/workflows (uploaded by the user), results (predictions and computed measures), and authors.
The [OpenML](http://www.openml.org) website allows easy querying for all this information.
Based on simple keyword queries you can look for authors, datasets, algorithms. From a dataset, you can look at all results obtained
on that dataset, for an algorithm or workflow, you can look at all results of that algorithm on different algorithms.

The key point of OpenML is that all this should be made possible with minimal effort by the user. OpenML offers a 
RESTful API for finding and downloading tasks and datasets and uploading implementations and results. This will be integrated
in key machine learning toolboxes (currently MOA, Python, R, RapidMiner and WEKA), so that experiments can be run and shared at 
the click of a button or a few key strokes. Custom libraries can also link to OpenML by using the API services they need. 

Downloading of tasks and datasets is completely open, but users will need to register on the OpenML website to upload
results.

# API Overview
This has been removed from the readme. Please consult the documentation on [http://www.openml.org/guide#!devels](http://www.openml.org/guide#!devels).
