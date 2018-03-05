In many cases, a scientist could be interested in running a wide range of flows over a range of datasets (or tasks). For this purpose, a job scheduler has been implemented. OpenML maintains a list of (setup,task) tuples, that users requested to run. A setup is a flow with information about all parameter settings.

Flows can be linked to a specific workbench. For example, the flows weka.J48(1) (which has id 60) and weka.SMO_PolyKernel(1) (which has id 70) are linked to Weka_3.7.10. When the same algorithm is uploaded from out another Weka version, a new version of the implementation is registered at OpenML. For example, if we would upload weka.J48 from the old version weka_3.7.5, OpenML would register the implementation as weka.J48(2). (In fact, this is what actually happend. See implementation id 100.)

In order to schedule jobs, go to the job scheduler ([Alpha version](http://www.openml.org/backend/page/job_creation), no guarantees). Select a task type, and give the experiment a name. It is important to filter the tasks and setups using the menu on the left, since the overview can be huge. Note that there are a some tasks on "big datasets", which can slow down the experimentation proces. Be considerate including those. Also make sure to filter on setups with flows of your own workbench version. If these are not in the system yet, register these [on the share page](http://openml.org/new/flow). 

[OpenML Weka]
The OpenML weka package can be used to automatically execute the scheduled jobs. Run it with the following command: 

java -cp openmlweka.jar org.openml.weka.experiment.RunJob -T 3 -N 1000

With T being the task type to execute, and N the number of runs you want to perform. (You can set this number as high as you like, the experimenter will stop as soon as there are no jobs left.) Note that this experimenter will only execute jobs that are of this Weka version. 

The OpenML Weka package can automatically register implementations on OpenML. Using the GUI, execute an OpenML task with all the implementations that you want to be registered. Before running those, these will automatically be registered on OpenML. 