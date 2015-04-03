# query to check the calculated Meta Features. 
# Column `qualitiesPerInterval` should contain a constant. 

SELECT `d`.`did`, `q`.`value` AS `numInstances`, `interval_end` - `interval_start` AS `interval_size`, 
CEIL(`q`.`value` / 1000) AS `numIntervals`, 
(COUNT(*) / CEIL(`q`.`value` / 1000)) AS `qualitiesPerInterval`, 
COUNT(*) AS `qualities` 
FROM `data_quality` `q`, `dataset` `d`
LEFT JOIN `data_quality_interval` `i` ON `d`.`did` = `i`.`data` 
WHERE `q`.`quality` IS NOT NULL 
AND `d`.`did` = `q`.`data` 
AND `q`.`quality` = 'NumberOfInstances'  
GROUP BY `d`.`did`, `interval_end` - `interval_start` 
ORDER BY `qualitiesPerInterval` ASC

# best classifier out of set
SELECT res.setup, i.fullName, AVG(value) AS avg FROM (SELECT r.task_id,r.setup,e.value FROM run r, evaluation e WHERE r.rid = e.source AND e.function = "predictive_accuracy" AND r.task_id IN (122, 160, 163, 170, 174, 175, 178, 182, 185,188, 189, 190, 191, 192, 193, 194, 195, 196, 197,198, 199, 200,2056,2127,2129,2130,2132,2133,2149,2151,2152,2154,2155,2156,2157,2159,2160,2162,2163,2164,2165,2166,2167,2168,2229,2244,2268,2269,6704, 6706,6707,7275,7280,7283,7285,7309,7310,7311,7312,7315,7316,7317) AND r.setup IN (
35, 21, 1471, 19, 22, 1893, 39, 20, 32, 36, 2071
) GROUP BY r.task_id, r.setup) res, algorithm_setup ss, implementation i WHERE res.setup = ss.sid AND ss.implementation_id = i.id  GROUP BY setup ORDER BY avg DESC

SELECT t.task_id, d.name, e_orig.value AS `orig` , e_bag.value AS `bagging` , e_bag.value - e_orig.value AS diff
FROM task_inputs t, task_tag tag, dataset d, run r_orig, evaluation e_orig, run r_bag, evaluation e_bag
WHERE t.input = "source_data"
AND e_orig.function = "predictive_accuracy"
AND e_bag.function = "predictive_accuracy"
AND tag.tag = "StreamEnsembles"
AND t.task_id = r_orig.task_id
AND t.task_id = r_bag.task_id
AND t.value = d.did
AND r_orig.rid = e_orig.source
AND r_bag.rid = e_bag.source
AND r_orig.setup =22
AND r_bag.setup =23
AND t.task_id = tag.id
GROUP BY t.task_id
ORDER BY e_bag.value - e_orig.value ASC

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


SELECT `q1`.`quality`, (`q2`.`value` / `q1`.`value`) AS 'BNG(anneal,nominal,1000000)' , (`q3`.`value` / `q1`.`value`) AS 'BNG(anneal,numeric,1000000)' 
FROM `data_quality` `q1`, `data_quality` `q2`, `data_quality` `q3` 
WHERE `q1`.`data` = 1 AND `q2`.`data` = 70 AND `q3`.`data` = 244 
AND `q1`.`quality` = `q2`.`quality` AND `q2`.`quality` = `q3`.`quality` 
AND `q1`.`value` > 0 AND `q2`.`value` > 0 AND `q3`.`value` > 0 
AND `q1`.`value` <= 1000 AND `q2`.`value` <= 1000 AND `q3`.`value` <= 1000

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

# compare diff between stream task on original data and on BNG data
SELECT `dataset_original`.`name` AS `original_dataset`, accuracy_original.value AS `accuracy_original_dataset`, accuracy_bng.value AS `accuracy_bng_dataset`
FROM task task_original, task task_bng, task_inputs `input_orig_data`, task_inputs `input_bng_data`, dataset `dataset_original`, dataset `dataset_bng`, run run_original, run run_bng, evaluation accuracy_original, evaluation accuracy_bng
WHERE task_original.task_id = input_orig_data.task_id 
AND task_bng.task_id = input_bng_data.task_id 
AND input_orig_data.input = "source_data" 
AND input_bng_data.input = "source_data" 
AND input_orig_data.value = dataset_original.did 
AND input_bng_data.value = dataset_bng.did 
AND dataset_bng.original_data_id = dataset_original.did 
AND run_original.task_id = task_original.task_id
AND run_bng.task_id = task_bng.task_id
AND accuracy_original.source = run_original.rid
AND accuracy_bng.source = run_bng.rid
AND accuracy_original.function = "predictive_accuracy"
AND accuracy_bng.function = "predictive_accuracy"
AND task_original.ttid = 4 
AND task_bng.ttid = 4
AND run_original.setup = 22
AND run_bng.setup = 22

# grap all relevant runs
SELECT r.run_id FROM `run` `r`,`algorithm_setup` `s`,`implementation` `i` WHERE r.setup = s.sid and s.implementation_id = i.id and r.task_id IN (2056, 171, 170, 175, 174, 163, 160, 185, 190,
191, 188, 189, 178, 177, 182, 183, 2133, 2132, 2134, 2129, 200,
2128, 2131, 2130, 197, 196, 199, 198, 193, 192, 195, 194, 2126,
2127, 2167, 2166, 2165, 2164, 2163, 2162, 2160, 2150, 2151,
127, 2159, 2156, 2157, 2154, 122) AND i.fullName IN ("moa.LeveragingBag_HoeffdingTree(1)", "moa.LeveragingBag_kNN(1)",
		"moa.OzaBoost_HoeffdingTree(1)", "moa.OzaBag_HoeffdingTree(1)", 
		"moa.kNN(1)",
		"moa.HoeffdingTree(1)", "moa.SGD(1)", "moa.NaiveBayes(1)",
		"moa.SPegasos(1)",
		"moa.WEKAClassifier_J48(1)", "moa.WEKAClassifier_OneR(1)",
		"moa.WEKAClassifier_REPTree(1)", "moa.WEKAClassifier_SMO_PolyKernel(1)") 

# evaluations / relevant task
SELECT r.rid, r.task_id, i.fullName, count(*) as evaluations FROM `run` `r`,`algorithm_setup` `s`,`implementation` `i`, `evaluation_interval` `e` WHERE r.setup = s.sid and s.implementation_id = i.id AND e.function = "predictive_accuracy" AND e.source = r.rid and r.task_id IN (2056, 171, 170, 175, 174, 163, 160, 185, 190,
191, 188, 189, 178, 177, 182, 183, 2133, 2132, 2134, 2129, 200,
2128, 2131, 2130, 197, 196, 199, 198, 193, 192, 195, 194, 2126,
2127, 2167, 2166, 2165, 2164, 2163, 2162, 2160, 2150, 2151,
127, 2159, 2156, 2157, 2154, 122) AND i.fullName IN ("moa.LeveragingBag_HoeffdingTree(1)", "moa.LeveragingBag_kNN(1)",
		"moa.OzaBoost_HoeffdingTree(1)", "moa.OzaBag_HoeffdingTree(1)", 
		"moa.kNN(1)",
		"moa.HoeffdingTree(1)", "moa.SGD(1)", "moa.NaiveBayes(1)",
		"moa.SPegasos(1)",
		"moa.WEKAClassifier_J48(1)", "moa.WEKAClassifier_OneR(1)",
		"moa.WEKAClassifier_REPTree(1)", "moa.WEKAClassifier_SMO_PolyKernel(1)") 
GROUP BY `r`.`rid`
ORDER BY r.task_id, i.fullName;

#evaluations check
SELECT r.rid, r.task_id, v.value AS did, q.value AS numInstances, i.fullName, count(*) as evaluations, (CEIL(q.value / 1000) / count(*)) as check_should_be1 
FROM `run` `r`,`algorithm_setup` `s`,`implementation` `i`, `evaluation_interval` `e`, `task_values` `v`, data_quality q 
WHERE v.value = q.data and q.quality = "NumberOfInstances" AND v.task_id = r.task_id AND v.input = 1 
AND r.setup = s.sid and s.implementation_id = i.id AND e.function = "predictive_accuracy" 
AND e.source = r.rid and r.task_id IN (2056, 171, 170, 175, 174, 163, 160, 185, 190,
191, 188, 189, 178, 177, 182, 183, 2133, 2132, 2134, 2129, 200,
2128, 2131, 2130, 197, 196, 199, 198, 193, 192, 195, 194, 2126,
2127, 2167, 2166, 2165, 2164, 2163, 2162, 2160, 2150, 2151,
127, 2159, 2156, 2157, 2154, 122) AND i.fullName IN ("moa.LeveragingBag_HoeffdingTree(1)", "moa.LeveragingBag_kNN(1)",
		"moa.OzaBoost_HoeffdingTree(1)", "moa.OzaBag_HoeffdingTree(1)", 
		"moa.kNN(1)",
		"moa.HoeffdingTree(1)", "moa.SGD(1)", "moa.NaiveBayes(1)",
		"moa.SPegasos(1)",
		"moa.WEKAClassifier_J48(1)", "moa.WEKAClassifier_OneR(1)",
		"moa.WEKAClassifier_REPTree(1)", "moa.WEKAClassifier_SMO_PolyKernel(1)") 
GROUP BY `r`.`rid` 
HAVING (CEIL(q.value / 1000) / count(*)) <> 1
ORDER BY r.task_id, i.fullName;

#evaluations check extensive
#SELECT r.rid, r.task_id, v.value AS did, q.value AS numInstances, i.fullName, count(*) as evaluations, (CEIL(q.value / 1000) / count(*)) as check_should_be1 
FROM `run` `r` LEFT JOIN (select * from evaluation_interval WHERE e.function = "predictive_accuracy") `e` ON r.rid = e.source,`algorithm_setup` `s`,`implementation` `i`, `task_values` `v`, data_quality q WHERE v.value = q.data and q.quality = "NumberOfInstances" AND v.task_id = r.task_id AND v.input = 1 AND r.setup = s.sid and s.implementation_id = i.id and r.task_id IN (2056, 171, 170, 175, 174, 163, 160, 185, 190,
#191, 188, 189, 178, 177, 182, 183, 2133, 2132, 2134, 2129, 200,
#2128, 2131, 2130, 197, 196, 199, 198, 193, 192, 195, 194, 2126,
#2127, 2167, 2166, 2165, 2164, 2163, 2162, 2160, 2150, 2151,
#127, 2159, 2156, 2157, 2154, 122) AND i.fullName IN ("moa.LeveragingBag_HoeffdingTree(1)", "moa.LeveragingBag_kNN(1)",
#		"moa.OzaBoost_HoeffdingTree(1)", "moa.OzaBag_HoeffdingTree(1)", 
#		"moa.kNN(1)",
#		"moa.HoeffdingTree(1)", "moa.SGD(1)", "moa.NaiveBayes(1)",
#		"moa.SPegasos(1)",
#		"moa.WEKAClassifier_J48(1)", "moa.WEKAClassifier_OneR(1)",
#		"moa.WEKAClassifier_REPTree(1)", "moa.WEKAClassifier_SMO_PolyKernel(1)") 
#GROUP BY `r`.`rid` 
#HAVING (CEIL(q.value / 1000) / count(*)) <> 1
#ORDER BY r.task_id, i.fullName;

SELECT r.rid, r.task_id, v.value AS did, i.fullName
FROM `run` `r`,`algorithm_setup` `s`,`implementation` `i`, `task_values` `v`
WHERE v.task_id = r.task_id AND v.input = 1 
AND r.setup = s.sid and s.implementation_id = i.id and r.task_id IN (2056, 171, 170, 175, 174, 163, 160, 185, 190,
191, 188, 189, 178, 177, 182, 183, 2133, 2132, 2134, 2129, 200,
2128, 2131, 2130, 197, 196, 199, 198, 193, 192, 195, 194, 2126,
2127, 2167, 2166, 2165, 2164, 2163, 2162, 2160, 2150, 2151,
127, 2159, 2156, 2157, 2154, 122) AND i.fullName IN ("moa.LeveragingBag_HoeffdingTree(1)", "moa.LeveragingBag_kNN(1)",
		"moa.OzaBoost_HoeffdingTree(1)", "moa.OzaBag_HoeffdingTree(1)", 
		"moa.kNN(1)",
		"moa.HoeffdingTree(1)", "moa.SGD(1)", "moa.NaiveBayes(1)",
		"moa.SPegasos(1)",
		"moa.WEKAClassifier_J48(1)", "moa.WEKAClassifier_OneR(1)",
		"moa.WEKAClassifier_REPTree(1)", "moa.WEKAClassifier_SMO_PolyKernel(1)") 
		AND  r.rid NOT IN (select source from evaluation_interval WHERE function = "predictive_accuracy")
ORDER BY r.task_id, i.fullName



select d.name, e2.value as 'moa.LeveragingBag_kNN (meta.LeveragingBag -l lazy.kNN)', e1.value as 'moa.kNN (lazy.kNN )' 
FROM task t, task_values v, dataset d, data_quality dq, 
run r1, algorithm_setup a1, implementation i1, evaluation e1, 
run r2, algorithm_setup a2, implementation i2, evaluation e2 
WHERE t.task_id = v.task_id and t.ttid = 4 and d.did = dq.data and dq.quality = "NumberOfInstances" and dq.value >= 45000 and v.input = 1 and v.value = d.did 
and t.task_id = r1.task_id and r1.setup = a1.sid and r1.rid = e1.source and e1.function = "predictive_accuracy" and a1.implementation_id = i1.id AND i1.fullName = "moa.kNN(1)" 
and t.task_id = r2.task_id and r2.setup = a2.sid and r2.rid = e2.source and e2.function = "predictive_accuracy" and a2.implementation_id = i2.id AND i2.fullName = "moa.LeveragingBag_kNN(1)"
