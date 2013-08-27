# This is an example on how a run can be generated and uploaded. 
# Doesn't work yet.

# Which task should be solved?
task.id <- 1
task <- downloadOpenMLTask(id = task.id)

# Which Implementation/Learner should be used?
lrn <- makeLearner("classif.rpart")
session.hash <- authenticateUser("dominikkirchhoff", "testpasswort")
# Check if implementation lrn is already registered. If not, upload it automatically.
# This is not yet done. The API call is missing.
implementation.id <- getMLRLearnerImplementationId(
  learner = lrn, 
  register = TRUE, 
  session.hash = session.hash)

# Make a run.
results <- runTask(task = task, learner = lrn, return.mlr.results = TRUE)
# Save the predictions.
output.file <- tempfile()
save(results$run.pred, file = output.file)

# Generate a description object of the run. 
# Maybe we should do this in uploadOpenMLRun and save a line of code?
description <- OpenMLRun(task.id = task.id, implementation.id = implementation.id)

# Upload the run.
uploadOpenMLRun(description = description, output.files = output.file, session.hash = session.hash)