# 
# script that removes all test data from database
# asumes that testdata in implementation table does not prefix on "weka" or equals "weka.updated.J48"
# 

DELETE FROM `bibliographical_reference`;

DELETE FROM `input_setting` WHERE `input` IN (SELECT `fullName` FROM `input` WHERE implementation IN (SELECT `fullName` FROM `implementation` WHERE `name` NOT LIKE "weka%" OR `name` = "weka.updated.J48") );

DELETE FROM `input` WHERE `implementation` IN (SELECT `fullName` FROM `implementation` WHERE `name` NOT LIKE "weka%" OR `name` = "weka.updated.J48");



DELETE FROM evaluation WHERE source NOT IN (SELECT rid FROM run);

DELETE FROM workflow_setup WHERE implementation IN (SELECT `fullName` FROM `implementation` WHERE `name` NOT LIKE "weka%" OR `name` = "weka.updated.J48");

DELETE FROM algorithm_setup WHERE implementation IN (SELECT `fullName` FROM `implementation` WHERE `name` NOT LIKE "weka%" OR `name` = "weka.updated.J48");

DELETE FROM dataset WHERE `did` > 2237;

DELETE FROM `implementation` WHERE `name` NOT LIKE "weka%" OR `name` = "weka.updated.J48";



DELETE FROM cvrun WHERE rid > 716950;

DELETE FROM evaluation WHERE source > 716950;

DELETE FROM evaluation WHERE did IN (SELECT did FROM output_data WHERE run > 716950);

DELETE FROM run WHERE rid > 716950;

DELETE FROM input_data WHERE run > 716950;

DELETE FROM output_data WHERE run > 716950;