TRUNCATE task;
TRUNCATE task_values;

INSERT INTO `task` (`ttid`) VALUES (1),(1),(1),(1),(1);

INSERT INTO `task_values` (`task_id`, `input`, `value`) VALUES
(1, 1, '61'),
(1, 2, 'class'),
(1, 3, 'cross_validation'),
(1, 4, 'http://expdb.cs.kuleuven.be/expdb/data/splits/iris_splits_CV_10_2.arff'),
(1, 5, '2'),
(1, 6, '10'),
(1, 8, 'predictive_accuracy'),

(2, 1, '61'),
(2, 2, 'class'),
(2, 3, 'cross_validation'),
(2, 4, 'http://expdb.cs.kuleuven.be/expdb/data/splits/iris_splits_CV_10_10.arff'),
(2, 5, '2'),
(2, 6, '10'),
(2, 8, 'predictive_accuracy'),

(3, 1, '40'),
(3, 2, 'Class'),
(3, 3, 'cross_validation'),
(3, 4, 'http://expdb.cs.kuleuven.be/expdb/data/splits/sonar_splits_CV_10_2.arff'),
(3, 5, '2'),
(3, 6, '10'),
(3, 8, 'predictive_accuracy'),

(4, 1, '40'),
(4, 2, 'Class'),
(4, 3, 'cross_validation'),
(4, 4, 'http://expdb.cs.kuleuven.be/expdb/data/splits/sonar_splits_CV_10_10.arff'),
(4, 5, '2'),
(4, 6, '10'),
(4, 8, 'precission'),

(5, 1, '1'),
(5, 2, 'class'),
(5, 3, 'cross_validation'),
(5, 4, 'http://expdb.cs.kuleuven.be/expdb/data/splits/anneal_splits_CV_10_2.arff'),
(5, 5, '2'),
(5, 6, '10'),
(5, 8, 'predictive_accuracy');
