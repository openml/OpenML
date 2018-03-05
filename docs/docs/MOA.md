OpenML features extensive support for MOA. However currently this is implemented as a stand alone MOA compilation, using the latest version (as of May, 2014).

[Download MOA for OpenML](https://www.openml.org/downloads/openmlmoa.beta.jar)

## Quick Start
![OpenML MOA Interface](https://github.com/openml/OpenML/raw/master/img/openmlmoa.png)

* Download the standalone MOA environment above.</li>
* Find your [API key](https://www.openml.org/u#!api) in your profile (log in first). Create a config file called <code>openml.conf</code> in a <code>.openml</code> directory in your home dir. It should contain the following lines:
>api_key = YOUR_KEY
* Launch the JAR file by double clicking on it, or launch from command-line using the following command:
> java -cp openmlmoa.beta.jar moa.gui.GUI
* Select the task <code>moa.tasks.openml.OpenmlDataStreamClassification</code> to evaluate a classifier on an OpenML task, and send the results to OpenML.
* Optionally, you can generate new streams using the Bayesian Network Generator: select the <code>moa.tasks.WriteStreamToArff</code> task, with <code>moa.streams.generators.BayesianNetworkGenerator</code>.
