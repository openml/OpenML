<?php

$apiErrors[110][0] = 'Please provide data_id';
$apiErrors[110][1] = 'Please provide data_id';

$apiErrors[111][0] = 'Unknown dataset';
$apiErrors[111][1] = 'Data set description with data_id was not found in the database';

$apiErrors[130][0] = 'Problem with file uploading';
$apiErrors[130][1] = 'There was a problem with the file upload';

$apiErrors[131][0] = 'Problem validating uploaded description file';
$apiErrors[131][1] = 'The XML description format does not meet the standards';

$apiErrors[132][0] = 'Failed to move the files';
$apiErrors[132][1] = 'Internal server error, please contact api administrators';

$apiErrors[133][0] = 'Failed to make checksum of datafile';
$apiErrors[133][1] = 'Internal server error, please contact api administrators';

$apiErrors[134][0] = 'Failed to insert record in database';
$apiErrors[134][1] = 'Internal server error, please contact api administrators';

$apiErrors[135][0] = 'Please provide description xml';
$apiErrors[135][1] = 'Please provide description xml';

$apiErrors[136][0] = 'Please provide dataset file';
$apiErrors[136][1] = 'Please provide dataset file';

$apiErrors[137][0] = 'Please provide session_hash';
$apiErrors[137][1] = 'In order to share content, please authenticate (openml.authenticate) and provide session_hash';

$apiErrors[138][0] = 'Authentication failed';
$apiErrors[138][1] = 'The session_hash was not valid. Please try to login again, or contact api administrators';

$apiErrors[139][0] = 'Combination name / version already exists';
$apiErrors[139][1] = 'The combination of name and version of this dataset already exists. Leave version out for auto increment';

$apiErrors[140][0] = 'Please provide task_id';
$apiErrors[140][1] = 'Please provide task_id';

$apiErrors[141][0] = 'Unknown task';
$apiErrors[141][1] = 'The task with this id was not found in the database';

$apiErrors[160][0] = 'Error in file uploading';
$apiErrors[160][1] = 'There was a problem with the file upload';

$apiErrors[161][0] = 'Please provide description xml';
$apiErrors[161][1] = 'Please provide description xml';

$apiErrors[162][0] = 'Please provide source or binary file';
$apiErrors[162][1] = 'Please provide source or binary file. It is also allowed to upload both';

$apiErrors[163][0] = 'Problem validating uploaded description file';
$apiErrors[163][1] = 'The XML description format does not meet the standards';

$apiErrors[164][0] = 'Implementation already stored in database';
$apiErrors[164][1] = 'Please change name or version number';

$apiErrors[165][0] = 'Failed to move the files';
$apiErrors[165][1] = 'Internal server error, please contact api administrators';

$apiErrors[166][0] = 'Failed to add implementation to database';
$apiErrors[166][1] = 'Internal server error, please contact api administrators';

$apiErrors[167][0] = 'Illegal files uploaded';
$apiErrors[167][1] = 'An non required file was uploaded.';

$apiErrors[168][0] = 'The provided md5 hash equals not the server generated md5 hash of the file';
$apiErrors[168][1] = 'The provided md5 hash equals not the server generated md5 hash of the file';

$apiErrors[169][0] = 'Please provide session_hash';
$apiErrors[169][1] = 'In order to share content, please authenticate (openml.authenticate) and provide session_hash';

$apiErrors[170][0] = 'Authentication failed';
$apiErrors[170][1] = 'The session_hash was not valid. Please try to login again, or contact api administrators';

$apiErrors[180][0] = 'Please provide implementation_id';
$apiErrors[180][1] = 'Please provide implementation_id';

$apiErrors[181][0] = 'Unknown implementation';
$apiErrors[181][1] = 'The implementation with this ID was not found in the database';

$apiErrors[200][0] = 'Please provide session_hash';
$apiErrors[200][1] = 'In order to share content, please authenticate (openml.authenticate) and provide session_hash';

$apiErrors[201][0] = 'Authentication failed';
$apiErrors[201][1] = 'The session_hash was not valid. Please try to login again, or contact api administrators';

$apiErrors[202][0] = 'Please provide run xml';
$apiErrors[202][1] = 'Please provide run xml';

$apiErrors[203][0] = 'Could not validate run xml by xsd';
$apiErrors[203][1] = 'Please double check that the xml is valid. ';

$apiErrors[204][0] = 'Unknown task';
$apiErrors[204][1] = 'The task with this id was not found in the database';

$apiErrors[205][0] = 'Unknown implementation';
$apiErrors[205][1] = 'The implementation with this id was not found in the database';

$apiErrors[206][0] = 'Invalid number of files';
$apiErrors[206][1] = 'The number of uploaded files did not match the number of files expected for this task type';

$apiErrors[207][0] = 'File upload failed';
$apiErrors[207][1] = 'One of the files uploaded has a problem';

$apiErrors[208][0] = 'Error inserting setup record';
$apiErrors[208][1] = 'Internal server error, please contact api administrators';

$apiErrors[209][0] = 'Unable to store cvrun';
$apiErrors[209][1] = 'Internal server error, please contact api administrators';

$apiErrors[210][0] = 'Unable to store run';
$apiErrors[210][1] = 'Internal server error, please contact api administrators';

$apiErrors[211][0] = 'Dataset not in databse';
$apiErrors[211][1] = 'One of the datasets of this task was not included in database, please contact api administrators';

$apiErrors[212][0] = 'Unable to store file';
$apiErrors[212][1] = 'Internal server error, please contact api administrators';

$apiErrors[240][0] = 'Please provide task_type_id';
$apiErrors[240][1] = 'Please provide task_type_id';

$apiErrors[241][0] = 'Unknown task type';
$apiErrors[241][1] = 'The task type with this id was not found in the database';

$apiErrors[250][0] = 'Please provide username';
$apiErrors[250][1] = 'Please provide the username as a POST variable';

$apiErrors[251][0] = 'Please provide password';
$apiErrors[251][1] = 'Please provide the password (hashed as a MD5) as a POST variable';

$apiErrors[252][0] = 'Authentication failed';
$apiErrors[252][1] = 'The username and password did not match any record in the database. Please note that the password should be hashed using md5';

$apiErrors[270][0] = 'Please provide data_id';
$apiErrors[270][1] = 'Please provide data_id';

$apiErrors[271][0] = 'Unknown dataset';
$apiErrors[271][1] = 'Data set description with data_id was not found in the database';

$apiErrors[272][0] = 'No features found';
$apiErrors[272][1] = 'The registered dataset did not contain any features';



?>