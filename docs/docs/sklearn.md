# scikit-learn

OpenML is readily integrated with scikit-learn through the [Python API](Python-guide).

!!! example
    ```python
    from sklearn import ensemble
    from openml import tasks, flows, Runs

    task = tasks.get_task(3954)
    clf = ensemble.RandomForestClassifier()
    flow = flows.sklearn_to_flow(clf)
    run = runs.run_flow_on_task(task, flow)
    result = run.publish()
    ```
Key features:  

* Query and download OpenML datasets and use them however you like  
* Build any sklearn estimator or pipeline and convert to OpenML flows  
* Run any flow on any task and save the experiment as run objects  
* Upload your runs for collaboration or publishing  
* Query, download and reuse all shared runs  

For many more details and examples, see the [Python tutorial](Python-guide).
