DELETE FROM `evaluation`  WHERE `did` IN (SELECT `data` FROM `run`, `output_data` WHERE `run`.`task_id` IS NOT NULL AND `run`.`rid` = `output_data`.`run`);
DELETE FROM `dataset`     WHERE `did` IN (SELECT `data` FROM `run`, `output_data` WHERE `run`.`task_id` IS NOT NULL AND `run`.`rid` = `output_data`.`run`);
DELETE FROM `input_data`  WHERE `run` IN (SELECT `rid` FROM `run`, WHERE `run`.`task_id` IS NOT NULL);
DELETE FROM `output_data` WHERE `run` IN (SELECT `rid` FROM `run`, WHERE `run`.`task_id` IS NOT NULL);
DELETE FROM `run` WHERE `run`.`task_id` IS NOT NULL;

## faster:

DELETE FROM `evaluation` WHERE source > 718190;
DELETE FROM `dataset` WHERE did > 719187;
DELETE FROM input_data WHERE run > 718190;
DELETE FROM output_data WHERE run > 718190;

DELETE FROM run WHERE rid > 718190;
DELETE FROM cvrun WHERE rid > 718190;
