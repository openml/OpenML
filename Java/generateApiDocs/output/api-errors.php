<?php

// for function: openml.global 
$this->apiErrors[100][0] = 'Function not valid';
$this->apiErrors[100][1] = 'Function not valid';

// for function: openml.data.description 
$this->apiErrors[110][0] = 'Please provide data_id';
$this->apiErrors[110][1] = 'Please provide data_id';

// for function: openml.data.description 
$this->apiErrors[111][0] = 'Unknown dataset';
$this->apiErrors[111][1] = 'Data set description with data_id was not found in the database';

// for function: openml.data.upload 
$this->apiErrors[130][0] = 'Problem with file uploading';
$this->apiErrors[130][1] = 'There was a problem with the file upload';

// for function: openml.data.upload 
$this->apiErrors[131][0] = 'Problem validating uploaded description file';
$this->apiErrors[131][1] = 'The XML description format does not meet the standards';

// for function: openml.data.upload 
$this->apiErrors[132][0] = 'Failed to move the files';
$this->apiErrors[132][1] = 'Internal server error, please contact api administrators';

// for function: openml.data.upload 
$this->apiErrors[133][0] = 'Failed to make checksum of datafile';
$this->apiErrors[133][1] = 'Internal server error, please contact api administrators';

// for function: openml.data.upload 
$this->apiErrors[134][0] = 'Failed to insert record in database';
$this->apiErrors[134][1] = 'Internal server error, please contact api administrators';

// for function: openml.data.upload 
$this->apiErrors[135][0] = 'Please provide description xml';
$this->apiErrors[135][1] = 'Please provide description xml';

// for function: openml.data.upload 
$this->apiErrors[136][0] = 'Error slot open';
$this->apiErrors[136][1] = 'Error slot open, will be filled by not yet defined error';

// for function: openml.data.upload 
$this->apiErrors[137][0] = 'Please provide session_hash';
$this->apiErrors[137][1] = 'In order to share content, please authenticate (openml.authenticate) and provide session_hash';

// for function: openml.data.upload 
$this->apiErrors[138][0] = 'Authentication failed';
$this->apiErrors[138][1] = 'The session_hash was not valid. Please try to login again, or contact api administrators';

// for function: openml.data.upload 
$this->apiErrors[139][0] = 'Combination name / version already exists';
$this->apiErrors[139][1] = 'The combination of name and version of this dataset already exists. Leave version out for auto increment';

// for function: openml.data.upload 
$this->apiErrors[140][0] = 'Both dataset file and dataset url provided. Please provide only one';
$this->apiErrors[140][1] = 'The system is confused since both a dataset file (post) and a dataset url (xml) are provided. Please remove one';

// for function: openml.data.upload 
$this->apiErrors[141][0] = 'Neither dataset file or dataset url are provided';
$this->apiErrors[141][1] = 'Please provide either a dataset file as POST variable, xor a dataset url in the description XML';

// for function: openml.data.upload 
$this->apiErrors[142][0] = 'Error in processing arff file. Can be a syntax error, or the specified target feature does not exists';
$this->apiErrors[142][1] = 'For now, we only check on arff files. If a dataset is claimed to be in such a format, and it can not be parsed, this error is returned.';

// for function: openml.data.upload 
$this->apiErrors[143][0] = 'Suggested target feature not legal';
$this->apiErrors[143][1] = 'It is possible to suggest a default target feature (for predictive tasks). However, it should be provided in the data. ';

// for function: openml.task.search 
$this->apiErrors[150][0] = 'Please provide task_id';
$this->apiErrors[150][1] = 'Please provide task_id';

// for function: openml.task.search 
$this->apiErrors[151][0] = 'Unknown task';
$this->apiErrors[151][1] = 'The task with this id was not found in the database';

// for function: openml.task.types.search 
$this->apiErrors[240][0] = 'Please provide task_type_id';
$this->apiErrors[240][1] = 'Please provide task_type_id';

// for function: openml.task.types.search 
$this->apiErrors[241][0] = 'Unknown task type';
$this->apiErrors[241][1] = 'The task type with this id was not found in the database';

// for function: openml.authenticate 
$this->apiErrors[250][0] = 'Please provide username';
$this->apiErrors[250][1] = 'Please provide the username as a POST variable';

// for function: openml.authenticate 
$this->apiErrors[251][0] = 'Please provide password';
$this->apiErrors[251][1] = 'Please provide the password (hashed as a MD5) as a POST variable';

// for function: openml.authenticate 
$this->apiErrors[252][0] = 'Authentication failed';
$this->apiErrors[252][1] = 'The username and password did not match any record in the database. Please note that the password should be hashed using md5';

// for function: openml.data.features 
$this->apiErrors[270][0] = 'Please provide data_id';
$this->apiErrors[270][1] = 'Please provide data_id';

// for function: openml.data.features 
$this->apiErrors[271][0] = 'Unknown dataset';
$this->apiErrors[271][1] = 'Data set description with data_id was not found in the database';

// for function: openml.data.features 
$this->apiErrors[272][0] = 'No features found';
$this->apiErrors[272][1] = 'The registered dataset did not contain any features';

// for function: openml.data.features 
$this->apiErrors[273][0] = 'Dataset not processed yet';
$this->apiErrors[273][1] = 'The dataset was not processed yet, no features are available. Please wait for a few minutes. ';

// for function: openml.data.features 
$this->apiErrors[274][0] = 'Dataset processed with error';
$this->apiErrors[274][1] = 'The feature extractor has run into an error while processing the dataset. Please check whether it is a valid supported file. ';

// for function: openml.authenticate.check 
$this->apiErrors[290][0] = 'Username not provided';
$this->apiErrors[290][1] = 'Please provide username';

// for function: openml.authenticate.check 
$this->apiErrors[291][0] = 'Hash not provided';
$this->apiErrors[291][1] = 'Please provide hash to be checked';

// for function: openml.authenticate.check 
$this->apiErrors[292][0] = 'Hash does not exist';
$this->apiErrors[292][1] = 'Hash does not exist, or is not owned by this user';

// for function: openml.task.evaluations 
$this->apiErrors[300][0] = 'Please provide task_id';
$this->apiErrors[300][1] = 'Please provide task_id';

// for function: openml.task.evaluations 
$this->apiErrors[301][0] = 'Unknown task';
$this->apiErrors[301][1] = 'The task with this id was not found in the database';

// for function: openml.data.delete 
$this->apiErrors[350][0] = 'Please provide session_hash';
$this->apiErrors[350][1] = 'In order to remove your content, please authenticate (openml.authenticate) and provide session_hash';

// for function: openml.data.delete 
$this->apiErrors[351][0] = 'Authentication failed';
$this->apiErrors[351][1] = 'The session_hash was not valid. Please try to login again, or contact api administrators';

// for function: openml.data.delete 
$this->apiErrors[352][0] = 'Dataset does not exists';
$this->apiErrors[352][1] = 'The data id could not be linked to an existing dataset.';

// for function: openml.data.delete 
$this->apiErrors[353][0] = 'Dataset is not owned by you';
$this->apiErrors[353][1] = 'The dataset was owned by another user. Hence you cannot delete it.';

// for function: openml.data.delete 
$this->apiErrors[354][0] = 'Dataset is in use by other content. Can not be deleted';
$this->apiErrors[354][1] = 'The data is used in runs. Delete this other content before deleting this dataset. ';

// for function: openml.data.delete 
$this->apiErrors[355][0] = 'Deleting dataset failed.';
$this->apiErrors[355][1] = 'Deleting the dataset failed. Please contact support team.';

// for function: openml.data.qualities 
$this->apiErrors[360][0] = 'Please provide data_id';
$this->apiErrors[360][1] = 'Please provide data_id';

// for function: openml.data.qualities 
$this->apiErrors[361][0] = 'Unknown dataset';
$this->apiErrors[361][1] = 'Data set description with data_id was not found in the database';

// for function: openml.data.qualities 
$this->apiErrors[362][0] = 'No qualities found';
$this->apiErrors[362][1] = 'The registered dataset did not contain any calculated qualities';

// for function: openml.data.qualities 
$this->apiErrors[363][0] = 'Dataset not processed yet';
$this->apiErrors[363][1] = 'The dataset was not processed yet, no qualities are available. Please wait for a few minutes.';

// for function: openml.data.qualities 
$this->apiErrors[364][0] = 'Dataset processed with error';
$this->apiErrors[364][1] = 'The quality calculator has run into an error while processing the dataset. Please check whether it is a valid supported file. ';

// for function: openml.data.qualities 
$this->apiErrors[365][0] = 'Interval start or end illegal';
$this->apiErrors[365][1] = 'There was a problem with the interval start or end.';

// for function: openml.data 
$this->apiErrors[370][0] = 'No datasets available';
$this->apiErrors[370][1] = 'There are no valid datasets in the system. Please upload!';

// for function: openml.estimationprocedure.get 
$this->apiErrors[440][0] = 'Please provide estimationprocedure_id';
$this->apiErrors[440][1] = 'Please provide estimationprocedure_id';

// for function: openml.estimationprocedure.get 
$this->apiErrors[441][0] = 'estimationprocedure_id not valid';
$this->apiErrors[441][1] = 'Please provide a valid estimationprocedure_id';



?>