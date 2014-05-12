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

#compare diff between knn and knn bag
SELECT LB.name, knn.score - LB.score AS "accuracy(kNN) - accuracy(LB kNN)" FROM
(SELECT DISTINCT d.name, e1.value AS 'score', t.task_id
FROM task t, task_values v, dataset d, run r1, algorithm_setup a1, implementation i1, evaluation e1 
WHERE t.task_id = v.task_id and t.ttid = 4  
AND v.input = 1 and v.value = d.did and t.task_id = r1.task_id 
and r1.setup = a1.sid and r1.rid = e1.source and e1.function = "predictive_accuracy" 
and a1.implementation_id = i1.id AND i1.fullName = "moa.LeveragingBag_kNN(1)" AND d.isOriginal = "true") AS `LB`, 
(SELECT DISTINCT d.name, e1.value AS 'score', t.task_id
FROM task t, task_values v, dataset d, run r1, algorithm_setup a1, implementation i1, evaluation e1 
WHERE t.task_id = v.task_id and t.ttid = 4
and v.input = 1 and v.value = d.did and t.task_id = r1.task_id 
and r1.setup = a1.sid and r1.rid = e1.source and e1.function = "predictive_accuracy" 
and a1.implementation_id = i1.id AND i1.fullName = "moa.kNN(1)" AND d.isOriginal = "true") as `knn`
WHERE LB.name = knn.name AND 
LB.task_id IN ( 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 158, 159, 160, 163, 164, 165, 166, 167, 169, 170, 171, 172, 173, 174, 175, 176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 188, 189, 190, 191, 192, 193, 194, 195, 196, 197, 198, 199, 200, 2056, 2126, 2127, 2128, 2129, 2130, 2131, 2132, 2133, 2134, 2150, 2151, 2154, 2155, 2156, 2157, 2159, 2160, 2161, 2162, 2163, 2164, 2165, 2166, 2167, 2268, 2269)
AND knn.task_id  IN ( 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 158, 159, 160, 163, 164, 165, 166, 167, 169, 170, 171, 172, 173, 174, 175, 176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 188, 189, 190, 191, 192, 193, 194, 195, 196, 197, 198, 199, 200, 2056, 2126, 2127, 2128, 2129, 2130, 2131, 2132, 2133, 2134, 2150, 2151, 2154, 2155, 2156, 2157, 2159, 2160, 2161, 2162, 2163, 2164, 2165, 2166, 2167, 2268, 2269 );

select d.name, e2.value as 'moa.LeveragingBag_kNN (meta.LeveragingBag -l lazy.kNN)', e1.value as 'moa.kNN (lazy.kNN )' 
FROM task t, task_values v, dataset d, data_quality dq, 
run r1, algorithm_setup a1, implementation i1, evaluation e1, 
run r2, algorithm_setup a2, implementation i2, evaluation e2 
WHERE t.task_id = v.task_id and t.ttid = 4 and d.did = dq.data and dq.quality = "NumberOfInstances" and dq.value >= 45000 and v.input = 1 and v.value = d.did 
and t.task_id = r1.task_id and r1.setup = a1.sid and r1.rid = e1.source and e1.function = "predictive_accuracy" and a1.implementation_id = i1.id AND i1.fullName = "moa.kNN(1)" 
and t.task_id = r2.task_id and r2.setup = a2.sid and r2.rid = e2.source and e2.function = "predictive_accuracy" and a2.implementation_id = i2.id AND i2.fullName = "moa.LeveragingBag_kNN(1)"