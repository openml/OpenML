INSERT INTO `task_type` (`ttid`, `name`, `description`, `creator`, `contributors`, `creationDate`) VALUES
(1, 'Supervised Classification', 'In supervised classification, you are given an input dataset in which instances are labeled with a certain class. The goal is to build a model that predicts the class for future unlabeled instances. The model is evaluated using a train-test procedure, e.g. cross-validation.<br><br>\r\n\r\nTo make results by different users comparable, you are given the exact train-test folds to be used, and you need to return at least the predictions generated by your model for each of the test instances. OpenML will use these predictions to calculate a range of evaluation measures on the server.<br><br>\r\n\r\nYou can also upload your own evaluation measures, provided that the code for doing so is available from the implementation used. For extremely large datasets, it may be infeasible to upload all predictions. In those cases, you need to compute and provide the evaluations yourself.<br><br>\r\n\r\nOptionally, you can upload the model trained on all the input data. There is no restriction on the file format, but please use a well-known format or PMML.', 'Joaquin Vanschoren, Jan van Rijn, Luis Torgo, Bernd Bischl', 'Bo Gao, Simon Fischer, Venkatesh Umaashankar, Michael Berthold, Bernd Wiswedel ,Patrick Winter', '2013-01-24 00:00:00'),
(2, 'Supervised Regression', 'Given a dataset with a numeric target and a set of train/test splits, e.g. generated by a cross-validation procedure, train a model and return the predictions of that model.', 'Joaquin Vanschoren, Jan van Rijn, Luis Torgo, Bernd Bischl', 'Bo Gao, Simon Fischer, Venkatesh Umaashankar, Michael Berthold, Bernd Wiswedel ,Patrick Winter', '2013-02-13 00:00:00'),
(3, 'Learning Curve', 'Given a dataset with a nominal target, various data samples of increasing size are defined. A model is build for each individual data sample; from this a learning curve can be drawn. ', 'Pavel Brazdil, Jan van Rijn, Joaquin Vanschoren', NULL, '2014-01-21 00:00:00'),
(4, 'Supervised Data Stream Classification', 'Given a dataset with a nominal target, various data samples of increasing size are defined. A model is build for each individual data sample; from this a learning curve can be drawn.', 'Geoffrey Holmes, Bernhard Pfahringer, Jan van Rijn, Joaquin Vanschoren', NULL, '2014-03-01 00:00:00'),
(5, 'Clustering', 'Given an input dataset, the task is to partition it into various clusters.', '\"Mehdi Jamali\", \"Jan van Rijn\", \"Nenad Tomasev\", \"Joaquin Vanschoren\"', NULL, '2014-10-24 00:00:00'),
(6, 'Machine Learning Challenge', 'This is a standard machine learning challenge with a hidden private dataset.\r\nIt offers a labeled training set and an unlabeled test set. \r\n\r\nThe task is to label the unlabeled instances. Only the OpenML server knows the correct labels, and will evaluate the submitted predictions using these hidden labels. The evaluation procedure, measure, and cost function (if any) are provided.', '\"Jan van Rijn\",\"Joaquin Vanschoren\"', NULL, '2014-11-28 00:00:00'),
(7, 'Survival Analysis', 'Related to Regression. Given a dataset (typically consisting of patient data) predict a left timestamp (date entering the study), right timestamp (date of leaving the study), or both. ', '\"Benrd Bischl\",\"Dominik Kirchhoff\",\"Michel Lang\",\"Jan van Rijn\",\"Joaquin Vanschoren\"', NULL, '2014-12-03 00:00:00'),
(8, 'Subgroup Discovery', 'Subgroup discovery is a data mining technique which extracts interesting rules with respect to a target variable. An important characteristic of this task is the combination of predictive and descriptive induction. An overview related to the task of subgroup discovery is presented. (description by: Herrera et. al., An overview on subgroup discovery: foundations and applications)', '\"Jan N. van Rijn\", \"Arno Knobbe\", \"Joaquin Vanschoren\"', NULL, '2016-06-17 10:59:20'),
(9, 'Multitask Regression', '', 'Jan N. van Rijn', NULL, '2019-10-24 23:46:54'),
(10, 'Active Classification', '', 'various contributors', NULL, '2023-02-22 11:46:54');
