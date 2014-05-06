# query to check the calculated Meta Features. 
# Column `qualitiesPerInterval` should contain a constant. 

SELECT `i`.`data`, `q`.`value` AS `numInstances`, 
  `interval_end` - `interval_start` AS `interval_size`, 
  CEIL(`q`.`value` / 1000) AS `numIntervals`, 
  (COUNT(*) / CEIL(`q`.`value` / 1000)) AS `qualitiesPerInterval`, 
  COUNT(*) AS `qualities` 
FROM `data_quality_interval` `i`, `data_quality` `q` 
WHERE `i`.`data` = `q`.`data` AND `q`.`quality` = "NumberOfInstances" 
GROUP BY `data`, `interval_end` - `interval_start`;


# inspect maximum value of meta feature, grouped by dataset
SELECT `q`.`data`, `d`.`name`, `q`.`interval_start`, `q`.`interval_end`, CONVERT( SUBSTRING_INDEX( `value`, '-', -1 ), UNSIGNED INTEGER ) AS "MaxNominalAttDistinctValues"
FROM `data_quality_interval` `q`, `dataset` `d`
WHERE `q`.`data` = `d`.`did`
AND `quality` = "MaxNominalAttDistinctValues"
GROUP BY `q`.`data`, `q`.`interval_start`, `q`.`interval_end`
ORDER BY CONVERT( SUBSTRING_INDEX( `value`, '-', -1 ) , UNSIGNED INTEGER ) DESC;

SELECT `d`.`did`,`d`.`name`, `dq1`.`value` AS `value1` 
FROM `dataset` `d`, data_quality `dq1` 
WHERE `dq1`.`data` = `d`.`did` 
AND `dq1`.`quality` = "J48.00001.kappa" 
AND `name` LIKE "%anneal%"

# selecting results from various data qualities
SELECT quality, IF(data=1,"anneal",IF(data=70,"BNG(anneal, nominal)","BNG(anneal, numeric)")) AS name, value 
FROM data_quality 
WHERE (data = 1 OR data = 70 OR data = 244) 

# selecting results from various data qualities based on a range:
SELECT `q1`.`quality`, `q1`.`value` AS 'anneal', `q2`.`value` AS 'BNG(anneal,nominal,1000000)', `q3`.`value` AS 'BNG(anneal,numeric,1000000)' 
FROM `data_quality` `q1`, `data_quality` `q2`, `data_quality` `q3` 
WHERE `q1`.`data` = 1 AND `q2`.`data` = 70 AND `q3`.`data` = 244 
AND `q1`.`quality` = `q2`.`quality` AND `q2`.`quality` = `q3`.`quality` 
AND `q1`.`value` >= 0 AND `q2`.`value` >= 0 AND `q3`.`value` >= 0 
AND `q1`.`value` <= 1 AND `q2`.`value` <= 1 AND `q3`.`value` <= 1