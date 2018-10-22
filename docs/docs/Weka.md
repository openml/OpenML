OpenML is integrated in the Weka (Waikato Environment for Knowledge Analysis) Experimenter and the Command Line Interface.

## Installation
OpenML is available as a weka extension in the package manager:

* [Download the latest version](http://www.cs.waikato.ac.nz/ml/weka/downloading.html) (3.7.13 or higher).
* Launch Weka, or start from commandline:
> java -jar weka.jar
* If you need more memory (e.g. 1GB), start as follows:
> java -Xmx1G -jar weka.jar
* Open the package manager (Under 'Tools')
* Select package **OpenmlWeka** and click install. Afterwards, restart WEKA.
* From the Tools menu, open the 'OpenML Experimenter'.

## Graphical Interface
![OpenML Experimenter](https://github.com/openml/OpenML/raw/master/img/openmlweka.png)

You can solve OpenML Tasks in the Weka Experimenter, and automatically upload your experiments to OpenML (or store them locally).  

* From the Tools menu, open the 'OpenML Experimenter'.
* Enter your [API key](https://www.openml.org/u#!api) in the top field (log in first). You can also store this in a config file (see below).
* In the 'Tasks' panel, click the 'Add New' button to add new tasks. Insert the task id's as comma-separated values (e.g., '1,2,3,4,5'). Use the search function on OpenML to find interesting tasks and click the ID icon to list the ID's. In the future this search will also be integrated in WEKA.
* Add algorithms in the "Algorithm" panel.
* Go to the "Run" tab, and click on the "Start" button.
* The experiment will be executed and sent to OpenML.org.
* The runs will now appear on OpenML.org. You can follow their progress and check for errors on your profile page under 'Runs'.

## CommandLine Interface
The Command Line interface is useful for running experiments automatically on a server, without using a GUI.

* Create a config file called <code>openml.conf</code> in a new directory called <code>.openml</code> in your home dir. It should contain the following line:
> api_key = YOUR_KEY
* Execute the following command:
> java -cp weka.jar openml.experiment.TaskBasedExperiment -T <task_id> -C <classifier_classpath> -- <parameter_settings>
* For example, the following command will run Weka's J48 algorithm on Task 1:
> java -cp OpenWeka.beta.jar openml.experiment.TaskBasedExperiment -T 1 -C weka.classifiers.trees.J48
* The following suffix will set some parameters of this classifier:
> -- -C 0.25 -M 2

## Issues
Please report any bugs that you may encounter in the issue tracker: https://github.com/openml/openml-weka
Or email to j.n.van.rijn@liacs.leidenuniv.nl
