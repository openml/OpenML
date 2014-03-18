ALTER TABLE `dataset` CHANGE `processed` `processed` DATETIME NULL DEFAULT NULL ;
ALTER TABLE `output_data` ADD `field` VARCHAR( 128 ) NULL DEFAULT NULL ;
ALTER TABLE `run` ADD `processed` DATETIME NULL DEFAULT NULL AFTER `error` ;

-- CAREFULL!!! SETUP ID SHOULD BE KNOWN BEFORE INSERTING THESE VALUES
CREATE TABLE `schedule` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `learner` varchar(255) NOT NULL,
  `setup_id` int(8) NOT NULL,
  `workbench` varchar(255) NOT NULL,
  `ttid` int(10) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=8 ;


INSERT INTO `schedule` (`id`, `learner`, `setup_id`, `workbench`, `ttid`) VALUES
(1, 'meta.AccuracyWeightedEnsemble -l (meta.WEKAClassifier -l (weka.classifiers.functions.SMO -C 1.0 -L 0.001 -P 1.0E-12 -N 0 -V -1 -W 1 -K "weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0") -w 500) -n 10.0', 12, 'moa', 4),
(2, 'meta.AccuracyWeightedEnsemble -l (meta.WEKAClassifier -l (weka.classifiers.trees.J48 -C 0.25 -M 2) -w 500) -n 10.0', 11, 'moa', 4),
(3, 'meta.AccuracyWeightedEnsemble -l (meta.WEKAClassifier -l (weka.classifiers.functions.Logistic -R 1.0E-8 -M -1) -w 500) -n 10.0', 13, 'moa', 4),
(4, 'bayes.naiveBayes', 6, 'moa', 4),
(5, 'functions.SGD', 9, 'moa', 4),
(6, 'trees.HoeffdingTree', 7, 'moa', 4),
(7, 'meta.LeveragingBag -l (trees.HoeffdingTree)', 10, 'moa', 4);