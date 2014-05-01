# query to check the calculated Meta Features. 
# Column `qualitiesPerInterval` should contain a constant. 

SELECT `i`.`data`, `q`.`value` AS `numInstances`, 
  `interval_end` - `interval_start` AS `interval_size`, 
  CEIL(`q`.`value` / 1000) AS `numIntervals`, 
  (COUNT(*) / CEIL(`q`.`value` / 1000)) AS `qualitiesPerInterval`, 
  COUNT(*) AS `qualities` 
FROM `data_quality_interval` `i`, `data_quality` `q` 
WHERE `i`.`data` = `q`.`data` AND `q`.`quality` = "NumberOfInstances" 
GROUP BY `data`, `interval_end` - `interval_start`