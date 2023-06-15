INSERT INTO `estimation_procedure` (`id`, `ttid`, `name`, `type`, `repeats`, `folds`, `samples`, `percentage`, `stratified_sampling`, `custom_testset`, `date`) VALUES
(1, 1, '10-fold Crossvalidation', 'crossvalidation', 1, 10, 'false', NULL, 'true', 'false', '2014-12-31 21:00:00'),
(2, 1, '5 times 2-fold Crossvalidation', 'crossvalidation', 5, 2, 'false', NULL, 'true', 'false', '2014-12-31 21:00:00'),
(3, 1, '10 times 10-fold Crossvalidation', 'crossvalidation', 10, 10, 'false', NULL, 'true', 'false', '2014-12-31 21:00:00'),
(4, 1, 'Leave one out', 'leaveoneout', 1, NULL, 'false', NULL, 'false', 'false', '2014-12-31 21:00:00'),
(5, 1, '10% Holdout set', 'holdout', 1, NULL, 'false', 33, 'true', 'false', '2014-12-31 21:00:00'),
(6, 1, '33% Holdout set', 'holdout', 1, NULL, 'false', 33, 'true', 'false', '2014-12-31 21:00:00'),
(7, 2, '10-fold Crossvalidation', 'crossvalidation', 1, 10, 'false', NULL, 'false', 'false', '2014-12-31 21:00:00'),
(8, 2, '5 times 2-fold Crossvalidation', 'crossvalidation', 5, 2, 'false', NULL, 'false', 'false', '2014-12-31 21:00:00'),
(9, 2, '10 times 10-fold Crossvalidation', 'crossvalidation', 10, 10, 'false', NULL, 'false', 'false', '2014-12-31 21:00:00'),
(10, 2, 'Leave one out', 'leaveoneout', 1, NULL, 'false', NULL, 'false', 'false', '2014-12-31 21:00:00'),
(11, 2, '10% Holdout set', 'holdout', 1, NULL, 'false', 33, 'false', 'false', '2014-12-31 21:00:00'),
(12, 2, '33% Holdout set', 'holdout', 1, NULL, 'false', 33, 'false', 'false', '2014-12-31 21:00:00'),
(13, 3, '10-fold Learning Curve', 'crossvalidation', 1, 10, 'true', NULL, 'true', 'false', '2014-12-31 21:00:00'),
(14, 3, '10 times 10-fold Learning Curve', 'crossvalidation', 10, 10, 'true', NULL, 'true', 'false', '2014-12-31 21:00:00'),
(15, 4, 'Interleaved Test then Train', 'testthentrain', NULL, NULL, 'false', NULL, NULL, 'false', '2014-12-31 21:00:00'),
(16, 1, 'Custom Holdout', 'customholdout', 1, 1, 'false', NULL, 'false', 'true', '2014-12-31 21:00:00'),
(17, 5, '50 times Clustering', 'testontrainingdata', 50, NULL, 'false', NULL, NULL, 'false', '2014-12-31 21:00:00'),
(18, 6, 'Holdout unlabeled', 'holdoutunlabeled', 1, 1, 'false', NULL, 'false', 'false', '2014-12-31 21:00:00'),
(19, 7, '10-fold Crossvalidation', 'crossvalidation', 1, 10, 'false', NULL, 'true', 'false', '2014-12-31 21:00:00'),
(20, 7, '5 times 2-fold Crossvalidation', 'crossvalidation', 5, 2, 'false', NULL, 'true', 'false', '2014-12-31 21:00:00'),
(21, 7, '10 times 10-fold Crossvalidation', 'crossvalidation', 10, 10, 'false', NULL, 'true', 'false', '2014-12-31 21:00:00'),
(22, 7, 'Leave one out', 'leaveoneout', 1, NULL, 'false', NULL, 'false', 'false', '2014-12-31 21:00:00'),
(23, 1, '100 times 10-fold Crossvalidation', 'crossvalidation', 100, 10, 'false', NULL, 'true', 'false', '2015-09-02 10:18:37'),
(24, 2, 'Custom 10-fold Crossvalidation', 'customholdout', 1, 10, 'false', NULL, 'false', 'true', '2015-09-20 16:44:44'),
(25, 1, '4-fold Crossvalidation', 'crossvalidation', 1, 4, 'false', NULL, 'true', 'false', '2016-03-15 13:32:10'),
(26, 1, 'Test on Training Data', 'testontrainingdata', NULL, NULL, 'false', NULL, NULL, 'false', '2019-03-16 11:30:14'),
(27, 2, 'Test on Training Data', 'testontrainingdata', NULL, NULL, 'false', NULL, NULL, 'false', '2019-03-16 11:30:14'),
(28, 1, '20% Holdout (Ordered)', 'holdout_ordered', 1, 1, 'false', 20, NULL, 'false', '2019-05-23 12:40:53'),
(29, 9, '10-fold Crossvalidation', 'crossvalidation', 1, 10, 'false', NULL, 'true', 'false', '2014-12-31 20:00:00'),
(30, 10, '10-fold Crossvalidation', 'crossvalidation', 1, 10, 'false', NULL, 'true', 'false', '2023-02-22 11:46:54'),
(31, 10, '5 times 2-fold Crossvalidation', 'crossvalidation', 5, 2, 'false', NULL, 'true', 'false', '2023-02-22 11:46:54'),
(32, 10, '10 times 10-fold Crossvalidation', 'crossvalidation', 10, 10, 'false', NULL, 'true', 'false', '2023-02-22 11:46:54'),
(33, 10, '10% Holdout set', 'holdout', 1, NULL, 'false', 33, 'true', 'false', '2023-02-22 11:46:54'),
(34, 10, '33% Holdout set', 'holdout', 1, NULL, 'false', 33, 'true', 'false', '2023-02-22 11:46:54'),
(35, 11, '33% Holdout set', 'holdout', 1, NULL, 'false', 33, 'true', 'false', '2023-06-15 16:34:54');