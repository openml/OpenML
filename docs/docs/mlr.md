# Machine Learning in R (mlr)

OpenML is readily integrated with mlr through the [R API](R-guide).

!!! example
    ```r
    library(OpenML)
    library(mlr)

    task = getOMLTask(10)
    lrn = makeLearner("classif.rpart")
    run = runTaskMlr(task, lrn)
    run.id = uploadOMLRun(run)
    ```

Key features:  

* Query and download OpenML datasets and use them however you like  
* Build any mlr learner, run it on any task and save the experiment as run objects  
* Upload your runs for collaboration or publishing  
* Query, download and reuse all shared runs  

For many more details and examples, see the [R tutorial](R-guide).
