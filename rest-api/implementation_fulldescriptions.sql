UPDATE implementation set fulldescription='Implements a least median sqaured linear regression utilising the existing weka LinearRegression class to form predictions. The basis of the algorithm is Robust regression and outlier detection Peter J. Rousseeuw, Annick M. Leroy. c1987
Version:
  
$Revision: 1.9.2.1 $

Author:
  
Tony Voyle (tv6@waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.LeastMedSq';
UPDATE implementation set fulldescription='Interface for classifiers that can be converted to Java source.
Version:
  
$Revision: 1.4 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)
' where name='weka.Sourcable';
UPDATE implementation set fulldescription='Interface for classes that want to listen for Attribute selection changes in the attribute panel
Version:
  
$Revision: 1.4 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)
' where name='weka.AttributePanelListener';
UPDATE implementation set fulldescription='DatabaseUtils provides utility functions for accessing the experiment database. The jdbc driver and database to be used default to "jdbc.idbDriver" and "jdbc:idb=experiments.prp". These may be changed by creating a java properties file called DatabaseUtils.props in user.home or the current directory. eg:
 jdbcDriver=jdbc.idbDriver jdbcURL=jdbc:idb=experiments.prp
Version:
  
$Revision: 5159 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.DatabaseUtils';
UPDATE implementation set fulldescription='This panel displays one dimensional views of the attributes in a dataset. Colouring is done on the basis of a column in the dataset or an auxiliary array (useful for colouring cluster predictions).
Version:
  
$Revision: 1.9 $

Author:
  
Malcolm Ware (mfw4@cs.waikato.ac.nz), Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.AttributePanel';
UPDATE implementation set fulldescription='Base class for RBFKernel and PolyKernel that implements a simple LRU. (least-recently-used) cache if the cache size is set to a value > 0. Otherwise it uses a full cache.
Version:
  
$Revision: 1.1.2.4 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz), Shane Legg (shane@intelligenesis.net) (sparse vector code), Stuart Inglis (stuart@reeltwo.com) (sparse vector code), J. Lindgren (jtlindgr{at}cs.helsinki.fi) (RBF kernel), Steven Hugg (hugg@fasterlight.com) (refactored, LRU cache), Bernhard Pfahringer (bernhard@cs.waikato.ac.nz) (full cache)

See Also:

Serialized Form
' where name='weka.CachedKernel';
UPDATE implementation set fulldescription='A component that accepts named stringbuffers and displays the name in a list box. When a name is right-clicked, a frame is popped up that contains the string held by the stringbuffer. Optionally a text component may be provided that will have it\'s text set to the named result text on a left-click.
Version:
  
$Revision: 4747 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.ResultHistoryPanel';
UPDATE implementation set fulldescription='
public class 
Matrix
extends java.lang.Object
implements java.lang.Cloneable, java.io.Serializable
Implementation for performing operations on a matrix of floating-point values. Deprecated: Uses internally the code of the sub-package  
weka.core.matrix
 - only for backwards compatibility.
Version:
  
$Revision: 1.18.2.2 $

Author:
  
Gabi Schmidberger (gabi@cs.waikato.ac.nz), Yong Wang (yongwang@cs.waikato.ac.nz), Eibe Frank (eibe@cs.waikato.ac.nz), Len Trigg (eibe@cs.waikato.ac.nz), Fracpete (fracpete at waikato dot ac dot nz)

See Also:

Serialized Form
' where name='weka.Matrix';
UPDATE implementation set fulldescription='Symbolic probability estimator based on symbol counts and a prior.
Version:
  
$Revision: 1.3.2.1 $

Author:
  
Remco Bouckaert (rrb@xm.co.nz)

See Also:

Serialized Form
' where name='weka.DiscreteEstimatorBayes';
UPDATE implementation set fulldescription='Abstract class for cluster data generators. ------------------------------------------------------------------- 
 General options are: 
 -r string 
 Name of the relation of the generated dataset. 
 (default = name built using name of used generator and options) 
 -a num 
 Number of attributes. (default = 2) 
 -k num 
 Number of clusters. (default = 4) 
 -c 
 Class Flag. If set, cluster is listed in extra class attribute.
 -o filename
 writes the generated dataset to the given file using ARFF-Format. (default = stdout). ------------------------------------------------------------------- 
 Example usage as the main of a datagenerator called RandomGenerator: public static void main(String [] args) {   try {     DataGenerator.makeData(new RandomGenerator(), argv);   } catch (Exception e) {     System.err.println(e.getMessage());   } } ------------------------------------------------------------------ 

Version:
  
$Revision: 1.2 $

Author:
  
Gabi Schmidberger (gabi@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.ClusterGenerator';
UPDATE implementation set fulldescription='Implementation for constructing a tree that considers K random features at each node. Performs no pruning.
Version:
  
$Revision: 5291 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz), Richard Kirkby (rkirkby@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.RandomTree';
UPDATE implementation set fulldescription='Abtract class for manipulating mixture distributions. 
 REFERENCES 
 Wang, Y. (2000). "A new approach to fitting linear models in high dimensional spaces." PhD Thesis. Department of Computer Science, University of Waikato, New Zealand. 
 Wang, Y. and Witten, I. H. (2002). "Modeling for optimal probability prediction." Proceedings of ICML\'2002. Sydney. 

Version:
  
$Revision: 1.1 $

Author:
  
Yong Wang (yongwang@cs.waikato.ac.nz)
' where name='weka.MixtureDistribution';
UPDATE implementation set fulldescription='Implementation for boosting a classifier using Freund &amp; Schapire\'s Adaboost  M1 method. For more information, see
 Yoav Freund and Robert E. Schapire (1996). 
Experiments with a new boosting algorithm
.  Proc International Conference on Machine Learning, pages 148-156, Morgan Kaufmann, San Francisco.
 Valid options are:
 -D 
 Turn on debugging output.
 -W classname 
 Specify the full class name of a classifier as the basis for  boosting (required).
 -I num 
 Set the number of boost iterations (default 10). 
 -P num 
 Set the percentage of weight mass used to build classifiers (default 100). 
 -Q 
 Use resampling instead of reweighting.
 -S seed 
 Random number seed for resampling (default 1). 
 Options after -- are passed to the designated classifier.

Version:
  
$Revision: 1.24.2.6 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz), Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.AdaBoostM1';
UPDATE implementation set fulldescription='Implementation for running an arbitrary classifier on data that has been passed through an arbitrary filter.
 Valid options from the command line are:
 -W classifierstring 
 Classifierstring should contain the full class name of a classifier (options are specified after a --). 
 -F filterstring 
 Filterstring should contain the full class name of a filter followed by options to the filter. 

Version:
  
$Revision: 1.20.2.3 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.FilteredClassifier';
UPDATE implementation set fulldescription='This class represents a node in the Graph.
Version:
  
$Revision: 1.2.2.1 $ - 23 Apr 2003 - Initial version (Ashraf M. Kibriya)

Author:
  
Ashraf M. Kibriya (amk14@cs.waikato.ac.nz)
' where name='weka.GraphNode';
UPDATE implementation set fulldescription='GeneticSearch is a crude implementation of genetic search for learning  Bayesian network structures.
Author:
  
Remco Bouckaert (rrb@xm.co.nz) Version: $Revision: 1.2 $

See Also:

Serialized Form
' where name='weka.GeneticSearch';
UPDATE implementation set fulldescription='Marker interface for a loader/saver that can retrieve instances incrementally
Version:
  
$Revision 1.0 $

Author:
  
Mark Hall
' where name='weka.IncrementalConverter';
UPDATE implementation set fulldescription='Abstract attribute selection evaluation class
Version:
  
$Revision: 1.9 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.ASEvaluation';
UPDATE implementation set fulldescription='The RBF kernel. K(x, y) = e^-(gamma * 
^2)
Version:
  
$Revision: 1.1.2.2 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz), Shane Legg (shane@intelligenesis.net) (sparse vector code), Stuart Inglis (stuart@reeltwo.com) (sparse vector code), J. Lindgren (jtlindgr{at}cs.helsinki.fi) (RBF kernel)

See Also:

Serialized Form
' where name='weka.RBFKernel';
UPDATE implementation set fulldescription='This class takes any Component and outputs it to a Postscript file.
 
Note:
 This writer does not work with Components that rely on clipping, like e.g. scroll lists. Here the complete list is printed, instead of only in the borders of the scroll list (may overlap other components!). This is due to the way, clipping is handled in Postscript. There was no easy way around  this issue. :-(
Version:
  
$Revision: 1.1.2.1 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)

See Also:

PostscriptGraphics

' where name='weka.PostscriptWriter';
UPDATE implementation set fulldescription='Interface to something that can process a IncrementalClassifierEvent
Since:
  
1.0

Version:
  
$Revision: 1.1.2.1 $

Author:
  
Mark Hall

See Also:

EventListener
' where name='weka.IncrementalClassifierListener';
UPDATE implementation set fulldescription='Abstract utility class for handling settings common to meta classifiers that build an ensemble from a single base learner.
Version:
  
$Revision: 1.2 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.IteratedSingleClassifierEnhancer';
UPDATE implementation set fulldescription='Cells of this matrix correspond to counts of the number (or weight) of predictions for each actual value / predicted value combination.
Version:
  
$Revision: 1.5.2.1 $

Author:
  
Len Trigg (len@reeltwo.com)

See Also:

Serialized Form
' where name='weka.ConfusionMatrix';
UPDATE implementation set fulldescription='This class lays out the vertices of a graph in a hierarchy of vertical levels, with a number of nodes in each level. The number of levels is the depth of the deepest child reachable from some parent at level 0. It implements a layout technique as described by K. Sugiyama, S. Tagawa, and M. Toda. in "Methods for visual understanding of hierarchical systems", IEEE Transactions on Systems, Man and Cybernetics, SMC-11(2):109-125, Feb. 1981. 
There have been a few modifications made, however. The crossings function is changed as it was non-linear in time complexity. Furthermore, we don\'t have any interconnection matrices for each level, instead we just have one big interconnection matrix for the whole graph and a int[][] array which stores the vertices present in each level.
Version:
  
$Revision: 1.3.2.1 $ - 24 Apr 2003 - Initial version (Ashraf M. Kibriya)

Author:
  
Ashraf M. Kibriya (amk14@cs.waikato.ac.nz)
' where name='weka.HierarchicalBCEngine';
UPDATE implementation set fulldescription='Classifier for incremental learning of large datasets by way of racing logit-boosted committees.  Valid options are:
 -C num 
 Set the minimum chunk size (default 500). 
 -M num 
 Set the maximum chunk size (default 2000). 
 -V num 
 Set the validation set size (default 1000). 
 -D 
 Turn on debugging output.
 -W classname 
 Specify the full class name of a weak learner as the basis for  boosting (required).
 -Q 
 Use resampling instead of reweighting.
 -S seed 
 Random number seed for resampling (default 1).
 -P type 
 The type of pruning to use. 
 Options after -- are passed to the designated learner.

Version:
  
$Revision: 1.4.2.2 $

Author:
  
Richard Kirkby (rkirkby@cs.waikato.ac.nz), Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.RacedIncrementalLogitBoost';
UPDATE implementation set fulldescription='Implementation for storing an object in serialized form in memory. It can be used  to make deep copies of objects, and also allows compression to conserve memory. 

Version:
  
$Revision: 1.7 $

Author:
  
Richard Kirkby (rbk1@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.SerializedObject';
UPDATE implementation set fulldescription='
Version:
  
$Revision: 1.3.2.2 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.ClassValuePicker';
UPDATE implementation set fulldescription='A little helper class for setting the Look and Feel of the user interface. Was necessary, since Java 1.5 sometimes crashed the WEKA GUI (e.g. under  Linux/Gnome).
Version:
  
$Revision: 1.1.2.3 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)
' where name='weka.LookAndFeel';
UPDATE implementation set fulldescription='This class enables one to change the UID of a serialized object and therefore not losing the data stored in the binary format.
Version:
  
$Revision: 1.1.2.2 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)
' where name='weka.SerialUIDChanger';
UPDATE implementation set fulldescription='This class is used in conjunction with the Node class to form a tree  structure. This in particular contains information about an edges in the tree.
Version:
  
$Revision: 1.3 $

Author:
  
Malcolm Ware (mfw4@cs.waikato.ac.nz)
' where name='weka.Edge';
UPDATE implementation set fulldescription='A wrapper bean for Weka filters
Version:
  
$Revision: 1.11.2.2 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.Filter';
UPDATE implementation set fulldescription='Interface for search methods capable of doing something sensible given a starting set of attributes.
Version:
  
$Revision: 1.6 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)
' where name='weka.StartSetHandler';
UPDATE implementation set fulldescription='This panel allows the user to select and configure a classifier, set the attribute of the current dataset to be used as the class, and evaluate the classifier using a number of testing modes (test on the training data, train/test on a percentage split, n-fold cross-validation, test on a separate split). The results of classification runs are stored in a result history so that previous results are accessible.
Version:
  
$Revision: 1.79.2.6 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz), Mark Hall (mhall@cs.waikato.ac.nz), Richard Kirkby (rkirkby@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.ClassifierPanel';
UPDATE implementation set fulldescription='Simple EM (expectation maximisation) class. 
 EM assigns a probability distribution to each instance which indicates the probability of it belonging to each of the clusters. EM can decide how many clusters to create by cross validation, or you may specify apriori how many clusters to generate. 
 The cross validation performed to determine the number of clusters is done in the following steps:
 1. the number of clusters is set to 1
 2. the training set is split randomly into 10 folds.
 3. EM is performed 10 times using the 10 folds the usual CV way.
 4. the loglikelihood is averaged over all 10 results.
 5. if loglikelihood has increased the number of clusters is increased by 1 and the program continues at step 2. 
 The number of folds is fixed to 10, as long as the number of instances in the training set is not smaller 10. If this is the case the number of folds is set equal to the number of instances.
 Valid options are:
 -V 
 Verbose. 
 -N 
 
 Specify the number of clusters to generate. If omitted, EM will use cross validation to select the number of clusters automatically. 
 -I 
 
 Terminate after this many iterations if EM has not converged. 
 -S 
 
 Specify random number seed. 
 -M 
 
 Set the minimum allowable standard deviation for normal density calculation.
Version:
  
$Revision: 6301 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz), Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.EM';
UPDATE implementation set fulldescription='The main class for the Weka GUIChooser. Lets the user choose which GUI they want to run.
Version:
  
$Revision: 7342 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz), Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.GUIChooser';
UPDATE implementation set fulldescription='GUI Customizer for the prediction appender bean
Version:
  
$Revision: 1.1 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.PredictionAppenderCustomizer';
UPDATE implementation set fulldescription='This class records all the data about a particular node for displaying.
Version:
  
$Revision: 1.3 $

Author:
  
Malcolm Ware (mfw4@cs.waikato.ac.nz)
' where name='weka.Node';
UPDATE implementation set fulldescription='GUI customizer for the train test split maker bean
Version:
  
$Revision: 1.1.2.1 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.TrainTestSplitMakerCustomizer';
UPDATE implementation set fulldescription='This panel controls setting a list of algorithms for an experiment to iterate over.
Version:
  
$Revision: 1.7.2.7 $

Author:
  
Richard Kirkby (rkirkby@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.AlgorithmListPanel';
UPDATE implementation set fulldescription='Implementation for generating a Naive Bayes tree (decision tree with Naive Bayes classifiers at the leaves). 
 For more information, see
 Ron Kohavi (1996). Scaling up the accuracy of naive-Bayes classifiers: a decision tree hybrid. 
Proceedings of the Second International Conference on Knowledge Discovery and Data Mining
.

Version:
  
$Revision: 1.3.2.1 $

See Also:

Serialized Form
' where name='weka.NBTree';
UPDATE implementation set fulldescription='GUI customizer for the classifier wrapper bean
Version:
  
$Revision: 1.6.2.1 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.ClassifierCustomizer';
UPDATE implementation set fulldescription='A dialog to present the user with a list of items, that the user can make a selection from, or cancel the selection.
Version:
  
$Revision: 1.4.4.1 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.ListSelectorDialog';
UPDATE implementation set fulldescription='This panel controls simple preprocessing of instances. Summary information on instances and attributes is shown. Filters may be configured to alter the set of instances. Altered instances may also be saved.
Version:
  
$Revision: 1.50.2.6 $

Author:
  
Richard Kirkby (rkirkby@cs.waikato.ac.nz), Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.PreprocessPanel';
UPDATE implementation set fulldescription='Helper class for logistic model trees (weka.classifiers.trees.lmt.LMT) to implement the  splitting criterion based on residuals.
Version:
  
$Revision: 1.1 $

Author:
  
Niels Landwehr

See Also:

Serialized Form
' where name='weka.ResidualModelSelection';
UPDATE implementation set fulldescription='Implementation to generate data randomly by producing a decision list. The decision list consists of rules. Instances are generated randomly one by one. If decision list fails to classify the current instance, a new rule according to this current instance is generated and added to the decision list.
 The option -V switches on voting, which means that at the end of the generation all instances are reclassified to the class value that is supported by the most rules.
 This data generator can generate \'boolean\' attributes (= nominal with the values {true, false}) and numeric attributes. The rules can be \'A\' or \'NOT A\' for boolean values and \'B < random_value\' or \'B >= random_value\' for numeric values.
  Valid options are:
 -R num 
 The maximum number of attributes chosen to form a rule (default 10).
 -M num 
 The minimum number of attributes chosen to form a rule (default 1).
 -I num 
 The number of irrelevant attributes (default 0).
 -N num 
 The number of numeric attributes (default 0).
 -S seed 
 Random number seed for random function used (default 1). 
 -V 
 Flag to use voting. 
 Following an example of a generated dataset: 
 %
 % weka.datagenerators.RDG1 -r expl -a 2 -c 3 -n 4 -N 1 -I 0 -M 2 -R 10 -S 2
 %
 relation expl
 attribute a0 {false,true}
 attribute a1 numeric
 attribute class {c0,c1,c2}
 data
 true,0.496823,c0
 false,0.743158,c1
 false,0.408285,c1
 false,0.993687,c2
 %
 % Number of attributes chosen as irrelevant = 0
 %
 % DECISIONLIST (number of rules = 3):
 % RULE 0:   c0 := a1 
 % RULE 1:   c1 := a1 
 % RULE 2:   c2 := not(a0), a1 >= 0.562

Version:
  
$Revision: 5676 $

Author:
  
Gabi Schmidberger (gabi@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.RDG1';
UPDATE implementation set fulldescription='Implementation for performing (ridged) linear regression.
Version:
  
$Revision: 1.2.2.2 $

Author:
  
Fracpete (fracpete at waikato dot ac dot nz)
' where name='weka.LinearRegression';
UPDATE implementation set fulldescription='Finds split points using correlation.
Version:
  
$Revision: 1.1 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.CorrelationSplitInfo';
UPDATE implementation set fulldescription='Simple probability estimator that places a single Poisson distribution over the observed values.
Version:
  
$Revision: 1.4 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.PoissonEstimator';
UPDATE implementation set fulldescription='Implementation for editing SimpleDateFormat strings.
Version:
  
$Revision: 1.1.2.2 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)

See Also:

SimpleDateFormat
' where name='weka.SimpleDateFormatEditor';
UPDATE implementation set fulldescription='A simple instance filter that renames the relation, all attribute names and all nominal (and string) attribute values. For exchanging sensitive datasets. Currently doesn\'t like string attributes.
Version:
  
$Revision: 1.2.2.1 $

Author:
  
Len Trigg (len@reeltwo.com)

See Also:

Serialized Form
' where name='weka.Obfuscate';
UPDATE implementation set fulldescription='Bean info class for the model performance chart
Version:
  
$Revision: 1.1 $

Author:
  
Mark Hall
' where name='weka.ModelPerformanceChartBeanInfo';
UPDATE implementation set fulldescription='DatabaseResultListener takes the results from a ResultProducer and submits them to a central database.
Version:
  
$Revision: 5124 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.DatabaseResultListener';
UPDATE implementation set fulldescription='Startup class for the KnowledgeFlow. Displays a splash screen.
Version:
  
$Revision: 1.23.2.4 $

Author:
  
Mark Hall
' where name='weka.KnowledgeFlow';
UPDATE implementation set fulldescription='Bean info class for the attribute summarizer bean
Version:
  
$Revision: 1.1 $

Author:
  
Mark Hall
' where name='weka.AttributeSummarizerBeanInfo';
UPDATE implementation set fulldescription='K* is an instance-based classifier, that is the class of a test instance is based upon the class of those training instances similar to it, as determined by some similarity function.  The underlying assumption of instance-based classifiers such as K*, IB1, PEBLS, etc, is that similar instances will have similar classes. For more information on K*, see 
 John, G. Cleary and Leonard, E. Trigg (1995) "K*: An Instance- based Learner Using an Entropic Distance Measure", 
Proceedings of the 12th International Conference on Machine learning
, pp. 108-114.

Version:
  
$Revision: 1.2.2.1 $

Author:
  
Len Trigg (len@reeltwo.com), Abdelaziz Mahoui (am14@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.KStar';
UPDATE implementation set fulldescription='Interface to objects able to generate a fixed set of results for a particular split of a dataset. The set of results should contain fields related to any settings of the SplitEvaluator (not including the dataset name. For example, one field for the classifier used to get the results, another for the classifier options, etc). 
 Possible implementations of SplitEvaluator: 
   
StdClassification results   
StdRegression results
Version:
  
$Revision: 1.6 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)
' where name='weka.SplitEvaluator';
UPDATE implementation set fulldescription='Implementation for combining classifiers using unweighted average of probability estimates (classification) or numeric predictions  (regression). Valid options from the command line are:
 -B classifierstring 
 Classifierstring should contain the full class name of a scheme included for selection followed by options to the classifier (required, option should be used once for each classifier).

Version:
  
$Revision: 1.7 $

Author:
  
Alexander K. Seewald (alex@seewald.at), Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.Vote';
UPDATE implementation set fulldescription='Implementation for handling the impurity values when spliting the instances
Version:
  
$Revision: 1.6 $

Author:
  
Yong Wang (yongwang@cs.waikato.ac.nz)
' where name='weka.Impurity';
UPDATE implementation set fulldescription='Abstract clustering model that produces (for each test instance) an estimate of the membership in each cluster  (ie. a probability distribution).
Version:
  
$Revision: 1.4 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz), Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.DensityBasedClusterer';
UPDATE implementation set fulldescription='This panel controls setting a list of datasets for an experiment to iterate over.
Version:
  
$Revision: 1.13.4.3 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.DatasetListPanel';
UPDATE implementation set fulldescription='
Version:
  
$Revision: 1.3.2.1 $

Author:
  
Peter A. Flach, Nicolas Lachiche

See Also:

Serialized Form
' where name='weka.IndividualInstances';
UPDATE implementation set fulldescription='Implementation for creating a committee of random classifiers. The base classifier (that forms the committee members) needs to implement the Randomizable interface. Valid options are:
 -W classname 
 Specify the full class name of a base classifier as the basis for  the random committee (required).
 -I num 
 Set the number of committee members (default 10). 
 -S seed 
 Random number seed for the randomization process (default 1). 
 Options after -- are passed to the designated classifier.

Version:
  
$Revision: 1.5.2.2 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.RandomCommittee';
UPDATE implementation set fulldescription='Contains utility functions for generating lists of predictions in  various manners.
Version:
  
$Revision: 1.9 $

Author:
  
Len Trigg (len@reeltwo.com)
' where name='weka.EvaluationUtils';
UPDATE implementation set fulldescription='This filter takes a dataset and outputs a specified fold for cross validation. If you want the folds to be stratified use the supervised version. Valid options are: 
 -V 
 Specifies if inverse of selection is to be output.
 -N number of folds 
 Specifies number of folds dataset is split into (default 10). 
 -F fold 
 Specifies which fold is selected. (default 1)
 -S seed 
 Specifies a random number seed for shuffling the dataset. (default 0, don\'t randomize)

Version:
  
$Revision: 1.1.2.1 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.RemoveFolds';
UPDATE implementation set fulldescription='This panel switches between simple and advanced experiment setup panels.
Version:
  
$Revision: 1.1.2.1 $

Author:
  
Richard Kirkby (rkirkby@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.SetupModePanel';
UPDATE implementation set fulldescription='Implementation representing a range of cardinal numbers. The range is set by a  string representation such as: 
   all   first-last   1,2,3,4 or combinations thereof. The range is internally converted from 1-based to 0-based (so methods that set or get numbers not in string format should use 0-based numbers).
Version:
  
$Revision: 1.14 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.Range';
UPDATE implementation set fulldescription='Abstract unsupervised attribute subset evaluator.
Version:
  
$Revision: 1.6 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.UnsupervisedSubsetEvaluator';
UPDATE implementation set fulldescription='The CVSearchAlgorithm class supports Bayes net structure search algorithms that are based on cross validation (as opposed to for example score based of conditional independence based search algorithms).
Version:
  
$Revision: 1.5.2.1 $

Author:
  
Remco Bouckaert

See Also:

Serialized Form
' where name='weka.GlobalScoreSearchAlgorithm';
UPDATE implementation set fulldescription='Conditional probability estimator for a numeric domain conditional upon a numeric domain.
Version:
  
$Revision: 1.4 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)
' where name='weka.KKConditionalEstimator';
UPDATE implementation set fulldescription='A little tool for viewing ARFF files.
Version:
  
$Revision: 1.1.2.4 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)

See Also:

Serialized Form
' where name='weka.ArffViewer';
UPDATE implementation set fulldescription='InstancesResultListener outputs the received results in arff format to a Writer. All results must be received before the instances can be written out.
Version:
  
$Revision: 1.8 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.InstancesResultListener';
UPDATE implementation set fulldescription='Implementation for performing parameter selection by cross-validation for any classifier. For more information, see
 R. Kohavi (1995). 
Wrappers for Performance Enhancement and Oblivious Decision Graphs
. PhD Thesis. Department of Computer Science, Stanford University. 
 Valid options are:
 -D 
 Turn on debugging output.
 -W classname 
 Specify the full class name of classifier to perform cross-validation selection on.
 -X num 
 Number of folds used for cross validation (default 10). 
 -S seed 
 Random number seed (default 1).
 -P "N 1 5 10" 
 Sets an optimisation parameter for the classifier with name -N, lower bound 1, upper bound 5, and 10 optimisation steps. The upper bound may be the character \'A\' or \'I\' to substitute  the number of attributes or instances in the training data, respectively. This parameter may be supplied more than once to optimise over several classifier options simultaneously. 
 Options after -- are passed to the designated sub-classifier. 

Version:
  
$Revision: 5788 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.CVParameterSelection';
UPDATE implementation set fulldescription='Generates points illustrating probablity cost tradeoffs that can be  obtained by varying the threshold value between classes. For example,  the typical threshold value of 0.5 means the predicted probability of  "positive" must be higher than 0.5 for the instance to be predicted as  "positive".
Version:
  
$Revision: 1.6 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)
' where name='weka.CostCurve';
UPDATE implementation set fulldescription='This panel allows log and status messages to be posted. Log messages appear in a scrollable text area, and status messages appear as one-line transient messages.
Version:
  
$Revision: 1.14.2.2 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.LogPanel';
UPDATE implementation set fulldescription='Utility routines for the converter package.
Version:
  
$Revision 1.0 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serializable
, 
Serialized Form
' where name='weka.ConverterUtils';
UPDATE implementation set fulldescription='Implementation implementing a "no-split"-split.
Version:
  
$Revision: 1.6 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.NoSplit';
UPDATE implementation set fulldescription='A bean that takes a stream of instances and displays in a table.
Version:
  
$Revision: 1.3 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.InstanceTable';
UPDATE implementation set fulldescription='Exception that is raised when trying to use something that has no reference to a dataset, when one is required.
Version:
  
$Revision: 1.3 $

Author:
  
Richard Kirkby (rkirkby@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.UnassignedDatasetException';
UPDATE implementation set fulldescription='Stores a set of integer of a given size.
Version:
  
$Revision: 1.1.2.1 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.SMOset';
UPDATE implementation set fulldescription='A filter that sorts the order of classes so that the class values are  no longer of in the order of that in the header file after filtered. The values of the class will be in the order specified by the user -- it could be either in ascending/descending order by the class frequency or in random order.
 The format of the header is thus not changed in this filter  (although it still uses 
setInputFormat()
), but the class value of each instance is converted to sorted  values within the same range.  The value can also be converted back using 
originalValue(double value)
 procedure.

Version:
  
$Revision: 1.4 $

Author:
  
Xin Xu (xx5@cs.waikato.ac.nz), Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.ClassOrder';
UPDATE implementation set fulldescription='Stores information on a property of an object: the class of the object with the property; the property descriptor, and the current value.
Version:
  
$Revision: 1.4 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.PropertyNode';
UPDATE implementation set fulldescription='The main class for the experiment environment. Lets the user create, open, save, configure, run experiments, and analyse experimental results.
Version:
  
$Revision: 1.8.2.2 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.Experimenter';
UPDATE implementation set fulldescription='Marker interface for a loader/saver that uses a database
Version:
  
$Revision 1.0 $

Author:
  
Mark Hall
' where name='weka.DatabaseConverter';
UPDATE implementation set fulldescription='Creates a panel that shows a visualization of an attribute in a dataset. For nominal attribute it shows a bar plot, with each bar corresponding to each nominal value of the attribute with its height equal to the frequecy that value appears in the dataset. For numeric attributes, it displays a histogram. The width of an interval in the histogram is calculated using Scott\'s(1979) method: 
    intervalWidth = Max(1, 3.49*Std.Dev*numInstances^(1/3)) Then the number of intervals is calculated by: 
   intervals = max(1, Math.round(Range/intervalWidth);
Version:
  
$Revision: 5999 $

Author:
  
Ashraf M. Kibriya (amk14@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.AttributeVisualizationPanel';
UPDATE implementation set fulldescription='BeanInfo class for the cross validation fold maker bean
Version:
  
$Revision: 1.1 $

Author:
  
Mark Hall
' where name='weka.CrossValidationFoldMakerBeanInfo';
UPDATE implementation set fulldescription='This class fixes a bug with the Swing JFileChooser: if you entered a new filename in the save dialog and press Enter the 
getSelectedFile
 method returns 
null
 instead of the filename.
 To solve this annoying behavior we call the save dialog once again s.t. the filename is set. Might look a little bit strange to the user, but no  NullPointerException! ;-)
Version:
  
$Revision: 1.1.2.2 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)

See Also:

Serialized Form
' where name='weka.FileChooser';
UPDATE implementation set fulldescription='Simple command line checking of classes that implement OptionHandler.
 Usage: 
     CheckOptionHandler -W optionHandlerClassName -- test options Valid options are: 
 -W classname 
 The name of a class implementing an OptionHandler. 
 Options after -- are used as user options in testing the OptionHandler 

Version:
  
$Revision: 1.8 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)
' where name='weka.CheckOptionHandler';
UPDATE implementation set fulldescription='This metaclassifier makes its base classifier cost-sensitive using the method specified in 
 Pedro Domingos (1999). 
MetaCost: A general method for making classifiers cost-sensitive
, Proceedings of the Fifth International Conference on  Knowledge Discovery and Data Mining, pp. 155-164. Also available online at http://www.cs.washington.edu/homes/pedrod/kdd99.ps.gz
. 
 This classifier should produce similar results to one created by passing the base learner to Bagging, which is in turn passed to a CostSensitiveClassifier operating on minimum expected cost. The difference is that MetaCost produces a single cost-sensitive classifier of the base learner, giving the benefits of fast classification and interpretable output (if the base learner itself is interpretable). This implementation  uses all bagging iterations when reclassifying training data (the MetaCost paper reports a marginal improvement when only those iterations containing each training instance are used in reclassifying that instance). 
 Valid options are:
 -W classname 
 Specify the full class name of a classifier (required).
 -C cost file 
 File name of a cost matrix to use. If this is not supplied, a cost matrix will be loaded on demand. The name of the on-demand file is the relation name of the training data plus ".cost", and the path to the on-demand file is specified with the -N option.
 -N directory 
 Name of a directory to search for cost files when loading costs on demand (default current directory). 
 -I num 
 Set the number of bagging iterations (default 10). 
 -S seed 
 Random number seed used when reweighting by resampling (default 1).
 -P num 
 Size of each bag, as a percentage of the training size (default 100). 
 -cost-matrix matrix
 The cost matrix, specified in Matlab single line format.
 Options after -- are passed to the designated classifier.

Version:
  
$Revision: 1.15.2.2 $

Author:
  
Len Trigg (len@reeltwo.com)

See Also:

Serialized Form
' where name='weka.MetaCost';
UPDATE implementation set fulldescription='This panel displays coloured labels for nominal attributes and a spectrum for numeric attributes. It can also be told to colour on the basis of an array of doubles (this can be useful for displaying coloured labels that correspond to a clusterers predictions).
Version:
  
$Revision: 1.12.2.1 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz), Malcolm Ware (mfw4@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.ClassPanel';
UPDATE implementation set fulldescription='Implementation for generating an alternating decision tree. The basic algorithm is based on:
 Freund, Y., Mason, L.: The alternating decision tree learning algorithm. Proceeding of the Sixteenth International Conference on Machine Learning, Bled, Slovenia, (1999) 124-133.
 This version currently only supports two-class problems. The number of boosting iterations needs to be manually tuned to suit the dataset and the desired  complexity/accuracy tradeoff. Induction of the trees has been optimized, and heuristic search methods have been introduced to speed learning.
 Valid options are: 
 -B num 
 Set the number of boosting iterations (default 10) 
 -E num 
 Set the nodes to expand: -3(all), -2(weight), -1(z_pure), >=0 seed for random walk (default -3) 
 -D 
 Save the instance data with the model 

Version:
  
$Revision: 1.2 $

Author:
  
Richard Kirkby (rkirkby@cs.waikato.ac.nz), Bernhard Pfahringer (bernhard@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.ADTree';
UPDATE implementation set fulldescription='Produces a random subsample of a dataset. The original dataset must fit entirely in memory. This filter allows you to specify the maximum "spread" between the rarest and most common class. For example, you may specify that there be at most a 2:1 difference in class frequencies. When used in batch mode, subsequent batches are 
not
 resampled. Valid options are:
 -S num 
 Specify the random number seed (default 1).
 -M num 
  The maximum class distribution spread. 
  0 = no maximum spread, 1 = uniform distribution, 10 = allow at most a  10:1 ratio between the classes (default 0) -X num 
  The maximum count for any class value. 
  (default 0 = unlimited) -W 
  Adjust weights so that total weight per class is maintained. Individual  instance weighting is not preserved. (default no weights adjustment)
Version:
  
$Revision: 1.3.2.2 $

Author:
  
Stuart Inglis (stuart@reeltwo.com)

See Also:

Serialized Form
' where name='weka.SpreadSubsample';
UPDATE implementation set fulldescription='a class for postprocessing the test-data
See Also:

#makeTestDataset(int, int, int, int, int, int, int, int, int, int, boolean)
' where name='weka.CheckClassifier.PostProcessor';
UPDATE implementation set fulldescription='Implementation for handling an ordered set of weighted instances. 
 Typical usage (code from the main() method of this class): 
 ... 
 // Read all the instances in the file 
 reader = new FileReader(filename); 
 instances = new Instances(reader); 
 // Make the last attribute be the class 
 instances.setClassIndex(instances.numAttributes() - 1); 
 // Print header and instances. 
 System.out.println("\nDataset:\n"); 
 System.out.println(instances); 
 ... 
 All methods that change a set of instances are safe, ie. a change of a set of instances does not affect any other sets of instances. All methods that change a datasets\'s attribute information clone the dataset before it is changed.
Version:
  
$Revision: 6995 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz), Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.Instances';
UPDATE implementation set fulldescription='Abstract attribute subset evaluator.
Version:
  
$Revision: 1.7 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.SubsetEvaluator';
UPDATE implementation set fulldescription='Interface for objects that determine a split point on an attribute
Version:
  
$Revision: 1.1 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)
' where name='weka.SplitEvaluate';
UPDATE implementation set fulldescription='An interface for objects interested in listening to streams of instances.
Version:
  
$Revision: 1.3 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)
' where name='weka.InstanceListener';
UPDATE implementation set fulldescription='With this class objects can be serialized to XML instead into a binary  format. It uses introspection (cf. beans) to retrieve the data from the given object, i.e. it can only access beans-conform fields automatically. The generic approach of writing data as XML can be overriden by adding  custom methods for reading/writing in a derived class (cf. 
m_Properties
, 
m_CustomMethods
).
 Custom read and write methods must have the same signature (and also be  
public
!) as the 
readFromXML
 and 
writeToXML
 methods. Methods that apply to the naming rule 
read + property name
 are added automatically to the list of methods by the method  
XMLSerializationMethodHandler.addMethods()
.   Other properties that are not conform the bean set/get-methods have to be  processed manually in a derived class (cf. 
readPostProcess(Object)
,  
writePostProcess(Object)
). For a complete XML serialization/deserialization have a look at the  
KOML
 class. If a stored class has a constructor that takes a String to initialize (e.g. String or Double) then the content of the tag will used for the constructor, e.g. from  
&lt;object name="name" class="String" primitive="no"&gt;Smith&lt;/object&gt;
 "Smith" will be used to instantiate a String object as constructor argument.
Version:
  
$Revision: 1.1.2.10 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)

See Also:

KOML
, 
fromXML(Document)
, 
toXML(Object)
, 
m_Properties
, 
m_CustomMethods
, 
readPostProcess(Object)
, 
writePostProcess(Object)
, 
readFromXML(Element)
, 
writeToXML(Element, Object, String)
, 
#addMethods()
' where name='weka.XMLSerialization';
UPDATE implementation set fulldescription='Cholesky Decomposition. For a symmetric, positive definite matrix A, the Cholesky decomposition is an lower triangular matrix L so that A = L*L\'. If the matrix is not symmetric or positive definite, the constructor returns a partial decomposition and sets an internal flag that may be queried by the isSPD() method. Adapted from the 
JAMA
 package.
Version:
  
$Revision: 1.1.2.3 $

Author:
  
The Mathworks and NIST, Fracpete (fracpete at waikato dot ac dot nz)

See Also:

Serialized Form
' where name='weka.CholeskyDecomposition';
UPDATE implementation set fulldescription='Interface implemented by a class that is interested in receiving submited shapes from a visualize panel.
Version:
  
$Revision: 1.4 $

Author:
  
Malcolm Ware (mfw4@cs.waikato.ac.nz)
' where name='weka.VisualizePanelListener';
UPDATE implementation set fulldescription='Bean for splitting instances into training ant test sets according to a cross validation
Version:
  
$Revision: 6006 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.CrossValidationFoldMaker';
UPDATE implementation set fulldescription='GUI Customizer for the cross validation fold maker bean
Version:
  
$Revision: 1.1 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.CrossValidationFoldMakerCustomizer';
UPDATE implementation set fulldescription='This class can generate the properties object that is normally loaded from the 
GenericObjectEditor.props
 file (= PROPERTY_FILE). It takes the 
GenericPropertiesCreator.props
 file as a template to determine all the derived classes by checking the classes in the given packages (a file with the same name in your home directory overrides the the one in the weka/gui directory/package). 
 E.g. if we want to have all the subclasses of the 
Classifier
 class then we specify the superclass ("weka.classifiers.Classifier") and the packages where to look for ("weka.classifiers.bayes" etc.):   weka.classifiers.Classifier=\     weka.classifiers.bayes,\     weka.classifiers.functions,\     weka.classifiers.lazy,\     weka.classifiers.meta,\     weka.classifiers.trees,\     weka.classifiers.rules This creates the same list as stored in the 
GenericObjectEditor.props
 file, but it will also add additional classes, that are not listed in the static list (e.g. a newly developed Classifier), but still in the classpath. 
 For discovering the subclasses the whole classpath is inspected, which means that you can have several parallel directories with the same package structure (e.g. a release directory and a developer directory with additional classes). 
 Code used and adapted from the following JavaWorld Tips:    
Tip 113 
: Identify subclasses at runtime
    
Tip 105 
: Mastering the classpath with JWhich

Version:
  
$Revision: 5376 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)

See Also:

CREATOR_FILE
, 
PROPERTY_FILE
, 
GenericObjectEditor
, 
RTSI

' where name='weka.GenericPropertiesCreator';
UPDATE implementation set fulldescription='Writes to a database (tested with MySQL, InstantDB, HSQLDB). Available options are: -T 
 
 Sets the name of teh table (default: the name of the relation)
 -P 
 If set, a primary key column is generated automatically (containing the row number as INTEGER). The name of this columns is defined in the DatabaseUtils file.
 -i 
 
 Specifies an ARFF file as input (for command line use) 

Version:
  
$Revision: 5174 $

Author:
  
Stefan Mutter (mutter@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.DatabaseSaver';
UPDATE implementation set fulldescription='A bean that evaluates the performance of batch trained classifiers
Version:
  
$Revision: 5477 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.ClassifierPerformanceEvaluator';
UPDATE implementation set fulldescription='This filter removes a given percentage of a dataset. Valid options are: 
 -V 
 Specifies if inverse of selection is to be output.
 -P percentage 
 The percentage of instances to select. (default 50)

Version:
  
$Revision: 1.3.2.1 $

Author:
  
Richard Kirkby (eibe@cs.waikato.ac.nz), Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.RemovePercentage';
UPDATE implementation set fulldescription='Interface for search methods capable of producing a ranked list of attributes.
Version:
  
$Revision: 1.9 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)
' where name='weka.RankedOutputSearch';
UPDATE implementation set fulldescription='Implementation representing a two-way split on a nominal attribute, of the form: either \'is some_value\' or \'is not some_value\'.
Version:
  
$Revision: 1.2 $

Author:
  
Richard Kirkby (rkirkby@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.TwoWayNominalSplit';
UPDATE implementation set fulldescription='Implementation representing a FIFO queue.
Version:
  
$Revision: 1.7 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.Queue';
UPDATE implementation set fulldescription='Creates a popup menu containing a tree that is aware of the screen dimensions.
See Also:

Serialized Form
' where name='weka.GenericObjectEditor.JTreePopupMenu';
UPDATE implementation set fulldescription='Customizers who want to be able to close the customizer window themselves can implement this window. The KnowledgeFlow will pass in the reference to the parent JFrame when constructing the customizer. The customizer can then call dispose() the Frame whenever it suits them.
Version:
  
$Revision: 1.1 $

Author:
  
Mark Hall
' where name='weka.CustomizerCloseRequester';
UPDATE implementation set fulldescription='Implementation for performing a Bias-Variance decomposition on any classifier  using the method specified in:
 R. Kohavi & D. Wolpert (1996), 
Bias plus variance decomposition for  zero-one loss functions
, in Proc. of the Thirteenth International  Machine Learning Conference (ICML96)  download postscript
.
 Valid options are:
 -D 
 Turn on debugging output.
 -W classname 
 Specify the full class name of a learner to perform the  decomposition on (required).
 -t filename 
 Set the arff file to use for the decomposition (required).
 -T num 
 Specify the number of instances in the training pool (default 100).
 -c num 
 Specify the index of the class attribute (default last).
 -x num 
 Set the number of train iterations (default 50). 
 -s num 
 Set the seed for the dataset randomisation (default 1). 
 Options after -- are passed to the designated sub-learner. 

Version:
  
$Revision: 1.9.2.1 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)
' where name='weka.BVDecompose';
UPDATE implementation set fulldescription='Represents a selected value from a finite set of values, where each value is a Tag (i.e. has some string associated with it). Primarily used in schemes to select between alternative behaviours, associating names with the alternative behaviours.
Version:
  
$Revision: 1.6.2.1 $

Author:
  
Len Trigg
' where name='weka.SelectedTag';
UPDATE implementation set fulldescription='This class offers some methods for generating, reading and writing  XML documents.
 It can only handle UTF-8.
Version:
  
$Revision 1.0$

Author:
  
FracPete (fracpete at waikato dot ac dot nz)

See Also:

PI

' where name='weka.XMLDocument';
UPDATE implementation set fulldescription='GUI Customizer for the saver bean
Version:
  
$Revision: 1.1.2.2 $

Author:
  
Stefan Mutter

See Also:

Serialized Form
' where name='weka.SaverCustomizer';
UPDATE implementation set fulldescription='Filters instances according to the value of an attribute.
 Valid filter-specific options are:
 -C num
 Choose attribute to be used for selection (default last).
 -S num
 Numeric value to be used for selection on numeric attribute. Instances with values smaller than given value will be selected. (default 0) 
 -L index1,index2-index4,...
 Range of label indices to be used for selection on nominal attribute. First and last are valid indexes. (default all values)
 -M 
 Missing values count as a match. This setting is independent of the -V option. (default missing values don\'t match)
 -V
 Invert matching sense.
 -H
 When selecting on nominal attributes, removes header references to excluded values. 

Version:
  
$Revision: 1.7.2.1 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.RemoveWithValues';
UPDATE implementation set fulldescription='Implementation representing the head of a rule.
Version:
  
$Revision: 1.3.2.1 $

Author:
  
Amelie Deltour

See Also:

Serialized Form
' where name='weka.Head';
UPDATE implementation set fulldescription='Abstract attribute selection search class.
Version:
  
$Revision: 1.8 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.ASSearch';
UPDATE implementation set fulldescription='Bean that wraps around weka.classifiers
Since:
  
1.0

Version:
  
$Revision: 1.16.2.6 $

Author:
  
Mark Hall

See Also:

JPanel
, 
BeanCommon
, 
Visible
, 
WekaWrapper
, 
Serializable
, 
UserRequestAcceptor
, 
TrainingSetListener
, 
TestSetListener
, 
Serialized Form
' where name='weka.Classifier';
UPDATE implementation set fulldescription='Implementation implementing some simple utility methods.
Version:
  
$Revision: 1.44.2.3 $

Author:
  
Eibe Frank, Yong Wang, Len Trigg, Julien Prados
' where name='weka.Utils';
UPDATE implementation set fulldescription='A PropertyEditor for objects. It can be used either in a static or a dynamic way. 
 In the 
static
 way (
USE_DYNAMIC
 is 
false
) the objects have been defined as editable in the GenericObjectEditor configuration file, which lists possible values that can be selected from, and themselves configured. The configuration file is called "GenericObjectEditor.props" and may live in either the location given by "user.home" or the current directory (this last will take precedence), and a default properties file is read from the weka distribution. For speed, the properties file is read only once when the class is first loaded -- this may need to be changed if we ever end up running in a Java OS ;-). 
 If it is used in a 
dynamic
 way (
USE_DYNAMIC
 is 
true
) then the classes to list are discovered by the  
GenericPropertiesCreator
 class (it checks the complete classpath).
Version:
  
$Revision: 5844 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz), Xin Xu (xx5@cs.waikato.ac.nz), Richard Kirkby (rkirkby@cs.waikato.ac.nz), FracPete (fracpete at waikato dot ac dot nz)

See Also:

USE_DYNAMIC
, 
GenericPropertiesCreator
, 
GenericPropertiesCreator.CREATOR_FILE
, 
RTSI

' where name='weka.GenericObjectEditor';
UPDATE implementation set fulldescription='Implementation for handling multi-class datasets with 2-class distribution classifiers.
 Valid options are:
 -M num 
 Sets the method to use. Valid values are 0 (1-against-all), 1 (random codes), 2 (exhaustive code), and 3 (1-against-1). (default 0) 
 -R num 
 Sets the multiplier when using random codes. (default 2.0)
 -W classname 
 Specify the full class name of a classifier as the basis for  the multi-class classifier (required).
 -S seed 
 Random number seed (default 1).

Version:
  
$Revision: 1.37.2.2 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz), Len Trigg (len@reeltwo.com), Richard Kirkby (rkirkby@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.MultiClassClassifier';
UPDATE implementation set fulldescription='The ScoreBasedSearchAlgorithm class supports Bayes net structure search algorithms that are based on maximizing scores (as opposed to for example conditional independence based search algorithms).
Version:
  
$Revision: 1.1.2.2 $

Author:
  
Remco Bouckaert

See Also:

Serialized Form
' where name='weka.LocalScoreSearchAlgorithm';
UPDATE implementation set fulldescription='Bean info class for the incremental classifier evaluator bean
Version:
  
$Revision: 1.2 $

Author:
  
Mark Hall
' where name='weka.IncrementalClassifierEvaluatorBeanInfo';
UPDATE implementation set fulldescription='Exception that is raised by an object that is unable to process the class type of the data it has been passed.
Version:
  
$Revision: 1.3 $

Author:
  
Richard Kirkby (rkirkby@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.UnsupportedClassTypeException';
UPDATE implementation set fulldescription='
Version:
  
$Revision: 1.2.2.1 $

Author:
  
Peter A. Flach, Nicolas Lachiche

See Also:

Serialized Form
' where name='weka.Predicate';
UPDATE implementation set fulldescription='Bean that accepts a data sets and produces a training set
Version:
  
$Revision: 1.3 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.TrainingSetMaker';
UPDATE implementation set fulldescription='Implementation for Evaluating attributes individually using ReliefF. 
 For more information see: 
 Kira, K. and Rendell, L. A. (1992). A practical approach to feature selection. In D. Sleeman and P. Edwards, editors, 
Proceedings of the International Conference on Machine Learning,
 pages 249-256. Morgan Kaufmann. 
 Kononenko, I. (1994). Estimating attributes: analysis and extensions of Relief. In De Raedt, L. and Bergadano, F., editors, 
 Machine Learning: ECML-94, 
 pages 171-182. Springer Verlag. 
 Marko Robnik Sikonja, Igor Kononenko: An adaptation of Relief for attribute estimation on regression. In D.Fisher (ed.): 
 Machine Learning,  Proceedings of 14th International Conference on Machine Learning ICML\'97,  
 Nashville, TN, 1997. 
 Valid options are: -M 
 
 Specify the number of instances to sample when estimating attributes. 
 If not specified then all instances will be used. 
 -D 
 
 Seed for randomly sampling instances. 
 -K 
 
 Number of nearest neighbours to use for estimating attributes. 
 (Default is 10). 
 -W 
 Weight nearest neighbours by distance. 
 -A 
 
 Specify sigma value (used in an exp function to control how quickly 
 weights decrease for more distant instances). Use in conjunction with 
 -W. Sensible values = 1/5 to 1/10 the number of nearest neighbours. 

Version:
  
$Revision: 1.15 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.ReliefFAttributeEval';
UPDATE implementation set fulldescription='A filter that adds a new nominal attribute representing the cluster assigned to each instance by the specified clustering algorithm.
 Valid filter-specific options are: 
 -W clusterer string 
 Full class name of clusterer to use, followed by scheme options. (required)
 -I range string 
 The range of attributes the clusterer should ignore. Note: if a class index is set then the class is automatically ignored during clustering.

Version:
  
$Revision: 1.3.2.2 $

Author:
  
Richard Kirkby (rkirkby@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.AddCluster';
UPDATE implementation set fulldescription='Abstract utility class for handling settings common to meta classifiers that use a single base learner.
Version:
  
$Revision: 1.2.2.1 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.SingleClassifierEnhancer';
UPDATE implementation set fulldescription='Implementation for building and using a 0-R classifier. Predicts the mean (for a numeric class) or the mode (for a nominal class).
Version:
  
$Revision: 1.11 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.ZeroR';
UPDATE implementation set fulldescription='A helper class for JTable, e.g. calculating the optimal colwidth.
Version:
  
$Revision: 1.1.2.1 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)
' where name='weka.JTableHelper';
UPDATE implementation set fulldescription='This panel controls simple analysis of experimental results.
Version:
  
$Revision: 1.28.2.5 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.ResultsPanel';
UPDATE implementation set fulldescription='Interface to something that can process a BatchClustererEvent
Version:
  
$Revision: 1.1 $

Author:
  
MStefan Mutter

See Also:

EventListener
' where name='weka.BatchClustererListener';
UPDATE implementation set fulldescription='Connects to a database.
Version:
  
$Revision: 1.1.2.3 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz), Stefan Mutter (mutter@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.DatabaseConnection';
UPDATE implementation set fulldescription='This panel enables an experiment to be distributed to multiple hosts; it also allows remote host names to be specified.
Version:
  
$Revision: 1.4 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.DistributeExperimentPanel';
UPDATE implementation set fulldescription='This event Is fired to a listeners \'userDataEvent\' function when The user on the VisualizePanel clicks submit. It contains the attributes selected at the time and a FastVector containing the various shapes that had been drawn into the panel.
Version:
  
$Revision: 1.3 $

Author:
  
Malcolm Ware (mfw4@cs.waikato.ac.nz)
' where name='weka.VisualizePanelEvent';
UPDATE implementation set fulldescription='This filter normalize all instances of a dataset to have a given norm. Only numeric values are considered, and the class attribute is ignored. Valid filter-specific options are:
 -L num 
 Specify the Lnorm to used on the normalization (default 2.0).
 -N num 
 Specify the norm of the instances after normalization (default 1.0).

Version:
  
$Revision: 1.2.2.1 $

Author:
  
Julien Prados

See Also:

Serialized Form
' where name='weka.Normalize';
UPDATE implementation set fulldescription='A simple instance filter that allows no instances to pass through. Basically just for testing purposes.
Version:
  
$Revision: 1.7 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.NullFilter';
UPDATE implementation set fulldescription='Implementation for Evaluating attributes individually by measuring the chi-squared statistic with respect to the class.  Valid options are:
 -M 
 Treat missing values as a seperate value. 
 -B 
 Just binarize numeric attributes instead of properly discretizing them. 

Version:
  
$Revision: 1.8 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.ChiSquaredAttributeEval';
UPDATE implementation set fulldescription='A bean encapsulating weka.gui.treevisualize.TreeVisualizer
Version:
  
$Revision: 1.6 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.GraphViewer';
UPDATE implementation set fulldescription='This panel controls configuration of lower and upper run numbers in an experiment.
Version:
  
$Revision: 1.4 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.RunNumberPanel';
UPDATE implementation set fulldescription='A bean that produces a stream of instances from a file.
Version:
  
$Revision: 1.3 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.InstanceLoader';
UPDATE implementation set fulldescription='Implementation for boosting a classifier using the MultiBoosting method.
 MultiBoosting is an extension to the highly successful AdaBoost technique for forming decision committees. MultiBoosting can be viewed as combining AdaBoost with wagging. It is able to harness both AdaBoost\'s high bias and variance reduction with wagging\'s superior variance reduction. Using C4.5 as the base learning algorithm, Multi-boosting is demonstrated to produce decision committees with lower error than either AdaBoost or wagging significantly more often than the reverse over a large representative cross-section of UCI data sets. It offers the further advantage over AdaBoost of suiting parallel execution.
 For more information, see
 Geoffrey I. Webb (2000). 
MultiBoosting: A Technique for Combining Boosting and Wagging
.  Machine Learning, 40(2): 159-196, Kluwer Academic Publishers, Boston
 Valid options are:
 -D 
 Turn on debugging output.
 -W classname 
 Specify the full class name of a classifier as the basis for boosting (required).
 -I num 
 Set the number of boost iterations (default 10). 
 -P num 
 Set the percentage of weight mass used to build classifiers (default 100). 
 -Q 
 Use resampling instead of reweighting.
 -S seed 
 Random number seed for resampling (default 1). 
 -C subcommittees 
 Number of sub-committees. (Default 3), 
 Options after -- are passed to the designated classifier.

Version:
  
$Revision: 1.6.2.5 $ `

Author:
  
Shane Butler (sbutle@deakin.edu.au), Eibe Frank (eibe@cs.waikato.ac.nz), Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.MultiBoostAB';
UPDATE implementation set fulldescription='This panel displays a plot matrix of the user selected attributes of a given data set.  The datapoints are coloured using a discrete colouring set if the  user has selected a nominal attribute for colouring. If the user has selected a numeric attribute then the datapoints are coloured using a colour spectrum ranging from blue to red (low values to high). Datapoints missing a class value are displayed in black.
Version:
  
$Revision: 1.11.2.3 $

Author:
  
Ashraf M. Kibriya (amk14@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.MatrixPanel';
UPDATE implementation set fulldescription='Reads a source that is in arff text format.
Version:
  
$Revision: 1.9.2.3 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Loader
, 
Serialized Form
' where name='weka.ArffLoader';
UPDATE implementation set fulldescription='Allows the user to select any (supported) property of an object, including properties that any of it\'s property values may have.
Version:
  
$Revision: 1.5.4.1 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.PropertySelectorDialog';
UPDATE implementation set fulldescription='The PostscriptGraphics class extends the Graphics2D class to  produce an encapsulated postscript file rather than on-screen display. Currently only a small (but useful) subset of Graphics methods have been  implemented.  To handle the ability to Clone a Graphics object, the graphics state of the  eps is set from the graphics state of the local PostscriptGraphics before output. To use, create a PostscriptGraphics object, and pass it to the PaintComponent method of a JComponent. If necessary additional font replacements can be inserted, since some fonts  might be displayed incorrectly.
Version:
  
$Revision: 1.2.2.2 $

Author:
  
Dale Fletcher (dale@cs.waikato.ac.nz), FracPete (fracpete at waikato dot ac dot nz)

See Also:

addPSFontReplacement(String, String)
, 
m_PSFontReplacement

' where name='weka.PostscriptGraphics';
UPDATE implementation set fulldescription='Implements the voted perceptron algorithm by Freund and Schapire. Globally replaces all missing values, and transforms nominal attributes into binary ones. For more information, see
 Y. Freund and R. E. Schapire (1998). 
 Large margin classification using the perceptron algorithm
.  Proc. 11th Annu. Conf. on Comput. Learning Theory, pp. 209-217, ACM Press, New York, NY. 
 Valid options are:
 -I num 
 The number of iterations to be performed. (default 1)
 -E num 
 The exponent for the polynomial kernel. (default 1)
 -S num 
 The seed for the random number generator. (default 1)
 -M num 
 The maximum number of alterations allowed. (default 10000) 

Version:
  
$Revision: 1.17 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.VotedPerceptron';
UPDATE implementation set fulldescription='Implementation implementing a HyperPipe classifier. For each category a HyperPipe is constructed that contains all points of that category  (essentially records the attribute bounds observed for each category). Test instances are classified according to the category that most  contains the instance).  Does not handle numeric class, or missing values in test cases. Extremely simple algorithm, but has the advantage of being extremely fast, and works quite well when you have smegloads of attributes.
Version:
  
$Revision: 1.15.2.1 $

Author:
  
Lucio de Souza Coelho (lucio@intelligenesis.net), Len Trigg (len@reeltwo.com)

See Also:

Serialized Form
' where name='weka.HyperPipes';
UPDATE implementation set fulldescription='Defines an interface for objects able to produce two output streams of instances.
Version:
  
$Revision: 1.3 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)
' where name='weka.SerialInstanceListener';
UPDATE implementation set fulldescription='Interface to something that makes use of the information provided by instance weights.
Version:
  
$Revision: 1.4 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)
' where name='weka.WeightedInstancesHandler';
UPDATE implementation set fulldescription='Implementation implementing a "no-split"-split (leaf node) for naive bayes trees.
Version:
  
$Revision: 1.1 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.NBTreeNoSplit';
UPDATE implementation set fulldescription='Interface for filters can work with a stream of instances.
Version:
  
$Revision: 1.1 $

Author:
  
Richard Kirkby (rkirkby@cs.waikato.ac.nz)
' where name='weka.StreamableFilter';
UPDATE implementation set fulldescription='Locally-weighted learning. Uses an instance-based algorithm to assign instance weights which are then used by a specified WeightedInstancesHandler.  A good choice for classification is NaiveBayes. LinearRegression is suitable for regression problems. For more information, see
 Eibe Frank, Mark Hall, and Bernhard Pfahringer (2003). Locally Weighted Naive Bayes. Working Paper 04/03, Department of Computer Science, University of Waikato. Atkeson, C., A. Moore, and S. Schaal (1996) 
Locally weighted learning
 
download  postscript
. 
 Valid options are:
 -D 
 Produce debugging output. 
 -N 
 Do not normalize numeric attributes\' values in distance calculation.
 -K num 
 Set the number of neighbours used for setting kernel bandwidth. (default all) 
 -U num 
 Set the weighting kernel shape to use. 0 = Linear, 1 = Epnechnikov,  2 = Tricube, 3 = Inverse, 4 = Gaussian and 5 = Constant. (default 0 = Linear) 
 -W classname 
 Specify the full class name of a base classifier (which needs to be a WeightedInstancesHandler).

Version:
  
$Revision: 1.12.2.1 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz), Eibe Frank (eibe@cs.waikato.ac.nz), Ashraf M. Kibriya (amk14@waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.LWL';
UPDATE implementation set fulldescription='
Class Summary



AttributePanel


This panel displays one dimensional views of the attributes in a dataset.


AttributePanelEvent


Class encapsulating a change in the AttributePanel\'s selected x and y attributes.


ClassPanel


This panel displays coloured labels for nominal attributes and a spectrum for numeric attributes.


JComponentWriter


This class takes any JComponent and outputs it to a file.


JPEGWriter


This class takes any JComponent and outputs it to a JPEG-file.


LegendPanel


This panel displays legends for a list of plots.


MatrixPanel


This panel displays a plot matrix of the user selected attributes of a given data set.


Plot2D


This class plots datasets in two dimensions.


PlotData2D


This class is a container for plottable data.


PostscriptGraphics


The PostscriptGraphics class extends the Graphics2D class to  produce an encapsulated postscript file rather than on-screen display.


PostscriptWriter


This class takes any Component and outputs it to a Postscript file.


PrintableComponent


This class extends the component which is handed over in the constructor by a print dialog.


PrintablePanel


This Panel enables the user to print the panel to various file formats.


ThresholdVisualizePanel


This panel is a VisualizePanel, with the added ablility to display the area under the ROC curve if an ROC curve is chosen.


VisualizePanel


This panel allows the user to visualize a dataset (and if provided) a classifier\'s/clusterer\'s predictions in two dimensions.


VisualizePanelEvent


This event Is fired to a listeners \'userDataEvent\' function when The user on the VisualizePanel clicks submit.


VisualizeUtils


This class contains utility routines for visualization
&nbsp;' where name='weka.package-summary';
UPDATE implementation set fulldescription='Interface to something that can process a TextEvent
Since:
  
1.0

Version:
  
$Revision: 1.2 $

Author:
  
Mark Hall

See Also:

EventListener
' where name='weka.TextListener';
UPDATE implementation set fulldescription='This class stores information about properties to ignore or properties that are allowed for a certain class.
Version:
  
$Revision: 1.1.2.3 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)
' where name='weka.PropertyHandler';
UPDATE implementation set fulldescription='BeanInfo class for AbstractTestSetProducer
Version:
  
$Revision: 1.1 $

Author:
  
Mark Hall
' where name='weka.AbstractTestSetProducerBeanInfo';
UPDATE implementation set fulldescription='This filter randomly shuffles the order of instances passed through it. The random number generator is reset with the seed value whenever setInputFormat() is called. 
 Valid filter-specific options are:
 -S num 
 Specify the random number seed (default 42).

Version:
  
$Revision: 1.2.2.1 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.Randomize';
UPDATE implementation set fulldescription='Interface implemented by classes that support undo.
Version:
  
$Revision: 1.1.2.1 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)
' where name='weka.Undoable';
UPDATE implementation set fulldescription='Implementation for Evaluating attributes individually by measuring gain ratio  with respect to the class. 
 Valid options are:
 -M 
 Treat missing values as a seperate value. 

Version:
  
$Revision: 1.16 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.GainRatioAttributeEval';
UPDATE implementation set fulldescription='Implementation for Evaluating attributes individually by measuring information gain  with respect to the class. Valid options are:
 -M 
 Treat missing values as a seperate value. 
 -B 
 Just binarize numeric attributes instead of properly discretizing them. 

Version:
  
$Revision: 1.14 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.InfoGainAttributeEval';
UPDATE implementation set fulldescription='This panel controls the configuration of an experiment. If 
KOML
 is in the classpath the experiments can also be saved to XML instead of a binary format.
Version:
  
$Revision: 5844 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz), Mark Hall (mhall@cs.waikato.ac.nz), FracPete (fracpete at waikato dot ac dot nz)

See Also:

Serialized Form
' where name='weka.SetupPanel';
UPDATE implementation set fulldescription='This class serializes and deserializes a Classifier instance to and fro XML.

Version:
  
$Revision: 1.3.2.2 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)
' where name='weka.XMLClassifier';
UPDATE implementation set fulldescription='A meta bean that encapsulates several other regular beans, useful for  grouping large KnowledgeFlows.
Version:
  
$Revision: 1.4.2.4 $

Author:
  
Mark Hall (mhall at cs dot waikato dot ac dot nz)

See Also:

Serialized Form
' where name='weka.MetaBean';
UPDATE implementation set fulldescription='Implementation encapsulating a built clusterer and a batch of instances to test on.
Since:
  
1.0

Version:
  
$Revision: 1.1 $

Author:
  
Stefan Mutter

See Also:

EventObject
, 
Serialized Form
' where name='weka.BatchClustererEvent';
UPDATE implementation set fulldescription='Implementation for Weka-specific exceptions.
Version:
  
$Revision: 1.3 $

Author:
  
Richard Kirkby (rkirkby@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.WekaException';
UPDATE implementation set fulldescription='Saves data sets using weka.core.converter classes
Version:
  
$Revision: 1.1.2.2 $

Author:
  
Stefan Mutter

See Also:

Serialized Form
' where name='weka.Saver';
UPDATE implementation set fulldescription='Implementation that manages a set of beans.
Since:
  
1.0

Version:
  
$Revision: 1.3.2.1 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.BeanInstance';
UPDATE implementation set fulldescription='This is an interface for classes that wish to take a node structure and  arrange them
Version:
  
$Revision: 1.4 $

Author:
  
Malcolm F Ware (mfw4@cs.waikato.ac.nz)
' where name='weka.NodePlace';
UPDATE implementation set fulldescription='The polynomial kernel : K(x, y) = 
^p or K(x, y) = ( 
+1)^p
Version:
  
$Revision: 1.1.2.2 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz), Shane Legg (shane@intelligenesis.net) (sparse vector code), Stuart Inglis (stuart@reeltwo.com) (sparse vector code)

See Also:

Serialized Form
' where name='weka.PolyKernel';
UPDATE implementation set fulldescription='Simple k means clustering class. Valid options are:
 -N 
 
 Specify the number of clusters to generate. 
 -S 
 
 Specify random number seed. 

Version:
  
$Revision: 1.19.2.5 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz), Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Clusterer
, 
OptionHandler
, 
Serialized Form
' where name='weka.SimpleKMeans';
UPDATE implementation set fulldescription='Implementation for ranking the attributes evaluated by a AttributeEvaluator Valid options are: 
 -P 
 
 Specify a starting set of attributes. Eg 1,4,7-9. 
 -T 
 
 Specify a threshold by which the AttributeSelection module can. 
 discard attributes. 

Version:
  
$Revision: 1.21 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.Ranker';
UPDATE implementation set fulldescription='Implementation for examining the capabilities and finding problems with  classifiers. If you implement a classifier using the WEKA.libraries, you should run the checks on it to ensure robustness and correct operation. Passing all the tests of this object does not mean bugs in the classifier don\'t exist, but this will help find some common ones. 
 Typical usage: 
 
java weka.classifiers.CheckClassifier -W classifier_name  classifier_options 
 CheckClassifier reports on the following:    
 Classifier abilities 
         
 Possible command line options to the classifier         
 Whether the classifier can predict nominal and/or predict               numeric class attributes. Warnings will be displayed if               performance is worse than ZeroR         
 Whether the classifier can be trained incrementally         
 Whether the classifier can handle numeric predictor attributes         
 Whether the classifier can handle nominal predictor attributes         
 Whether the classifier can handle string predictor attributes         
 Whether the classifier can handle missing predictor values         
 Whether the classifier can handle missing class values         
 Whether a nominal classifier only handles 2 class problems         
 Whether the classifier can handle instance weights    
 Correct functioning 
         
 Correct initialisation during buildClassifier (i.e. no result              changes when buildClassifier called repeatedly)         
 Whether incremental training produces the same results              as during non-incremental training (which may or may not               be OK)         
 Whether the classifier alters the data pased to it               (number of instances, instance order, instance weights, etc)    
 Degenerate cases 
         
 building classifier with zero training instances         
 all but one predictor attribute values missing         
 all predictor attribute values missing         
 all but one class values missing         
 all class values missing Running CheckClassifier with the debug option set will output the  training and test datasets for any failed tests.
 The 
weka.classifiers.AbstractClassifierTest
 uses this class to test all the classifiers. Any changes here, have to be  checked in that abstract test class, too. 
 Valid options are:
 -D 
 Turn on debugging output.
 -S 
 Silent mode, i.e., no output at all.
 -N num 
 Number of instances to use for datasets (default 20).
 -W classname 
 Specify the full class name of a classifier to perform the  tests on (required).
 Options after -- are passed to the designated classifier.

Version:
  
$Revision: 1.16.2.2 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)
' where name='weka.CheckClassifier';
UPDATE implementation set fulldescription='Meta classifier that enhances the performance of a regression base classifier. Each iteration fits a model to the residuals left by the classifier on the previous iteration. Prediction is accomplished by adding the predictions of each classifier. Smoothing is accomplished through varying the shrinkage (learning rate) parameter. 
 For more information see: 
 Friedman, J.H. (1999). Stochastic Gradient Boosting. Technical Report Stanford University. http://www-stat.stanford.edu/~jhf/ftp/stobst.ps. 
 Valid options from the command line are: 
 -W classifierstring 
 Classifierstring should contain the full class name of a classifier.
 -S shrinkage rate 
 Smaller values help prevent overfitting and have a smoothing effect  (but increase learning time). (default = 1.0, ie no shrinkage). 
 -I max models 
 Set the maximum number of models to generate. (default = 10). 
 -D 
 Debugging output. 

Version:
  
$Revision: 1.17.2.1 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.AdditiveRegression';
UPDATE implementation set fulldescription='Simple symbolic probability estimator based on symbol counts.
Version:
  
$Revision: 1.6 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.DiscreteEstimator';
UPDATE implementation set fulldescription='A PropertyEditor that uses tags, where the tags are obtained from a weka.core.SelectedTag object.
Version:
  
$Revision: 1.6.2.1 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)
' where name='weka.SelectedTagEditor';
UPDATE implementation set fulldescription='This class represents an edge in the graph
Version:
  
$Revision: 1.2.2.1 $ - 23 Apr 2003 - Initial version (Ashraf M. Kibriya)

Author:
  
Ashraf M. Kibriya (amk14@cs.waikato.ac.nz)
' where name='weka.GraphEdge';
UPDATE implementation set fulldescription='This panel allows the user to select and configure an attribute evaluator and a search method, set the attribute of the current dataset to be used as the class, and perform attribute selection using one of two  selection modes (select using all the training data or perform a n-fold cross validation---on each trial selecting features using n-1 folds of the data). The results of attribute selection runs are stored in a results history so that previous results are accessible.
Version:
  
$Revision: 7100 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.AttributeSelectionPanel';
UPDATE implementation set fulldescription='Generates a decision list for regression problems using  separate-and-conquer. In each iteration it builds an  model tree using M5 and makes the "best"  leaf into a rule. Reference: 
 M. Hall, G. Holmes, E. Frank (1999).  "Generating Rule Sets  from Model Trees". Proceedings of the Twelfth Australian Joint  Conference on Artificial Intelligence, Sydney, Australia.  Springer-Verlag, pp. 1-12.
 Valid options are:
 -U 
 Use unsmoothed predictions. 
 -R 
 Build regression tree/rule rather than model tree/rule -M num 
 Minimum number of objects per leaf. 
 -N  
 Turns pruning off. 

Version:
  
$Revision: 1.3.2.1 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.M5Rules';
UPDATE implementation set fulldescription='Implementation for running an arbitrary classifier on data that has been reduced through attribute selection. 
 Valid options from the command line are:
 -W classifierstring 
 Classifierstring should contain the full class name of a classifier. Any options for the classifier should appear at the end of the command line following a "--"..
 -E evaluatorstring 
 Evaluatorstring should contain the full class name of an attribute evaluator followed by any options. (required).
 -S searchstring 
 Searchstring should contain the full class name of a search method followed by any options. (required). 

Version:
  
$Revision: 1.16.2.4 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.AttributeSelectedClassifier';
UPDATE implementation set fulldescription='Interface to something that can wrap around a class of Weka algorithms (classifiers, filters etc). Typically implemented by a bean for handling classes of Weka algorithms.
Since:
  
1.0

Version:
  
$Revision: 1.2.2.1 $

Author:
  
Mark Hall
' where name='weka.WekaWrapper';
UPDATE implementation set fulldescription='
Version:
  
$Revision: 1.3.2.3 $

See Also:

Serialized Form
' where name='weka.MultiNomialBMAEstimator';
UPDATE implementation set fulldescription='Implementation for logistic model tree structure.
Version:
  
$Revision: 1.1.2.1 $

Author:
  
Niels Landwehr

See Also:

Serialized Form
' where name='weka.LMTNode';
UPDATE implementation set fulldescription='Simple probability estimator that places a single normal distribution over the observed values.
Version:
  
$Revision: 1.5 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.NormalEstimator';
UPDATE implementation set fulldescription='A general purpose server for executing Task objects sent via RMI.
Version:
  
$Revision: 1.7 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.RemoteEngine';
UPDATE implementation set fulldescription='Bean that can can accept batch or incremental classifier events and produce dataset or instance events which contain instances with predictions appended.
Version:
  
$Revision: 1.9.2.5 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.PredictionAppender';
UPDATE implementation set fulldescription='
Version:
  
$Revision: 1.2.2.1 $

Author:
  
Mark Hall
' where name='weka.CustomizerClosingListener';
UPDATE implementation set fulldescription='Creates a panel that displays the attributes contained in a set of instances, letting the user select a single attribute for inspection.
Version:
  
$Revision: 1.1 $

Author:
  
Richard Kirkby (rkirkby@cs.waikato.ac.nz), Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.AttributeListPanel';
UPDATE implementation set fulldescription='Simple kernel density estimator. Uses one gaussian kernel per observed data value.
Version:
  
$Revision: 1.5 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.KernelEstimator';
UPDATE implementation set fulldescription='This class will place the Nodes of a tree. 
 It will place these nodes so that they fall at evenly below their parent. It will then go through and look for places where nodes fall on the wrong  side of other nodes when it finds one it will trace back up the tree to find the first common  sibling group these two nodes have And it will adjust the spacing between these two siblings so that the two  nodes no longer overlap. This is nasty to calculate with , and takes a while with the current  algorithm I am using to do this.

Version:
  
$Revision: 1.3 $

Author:
  
Malcolm Ware (mfw4@cs.waikato.ac.nz)
' where name='weka.PlaceNode2';
UPDATE implementation set fulldescription='Abstract class for objects that store instances to some destination.
Since:
  
1.0

Version:
  
$Revision: 1.4.2.1 $

Author:
  
Mark Hall

See Also:

JPanel
, 
Serializable
, 
Serialized Form
' where name='weka.AbstractDataSink';
UPDATE implementation set fulldescription='This class will place the Nodes of a tree. 
 It will place these nodes so that they symetrically fill each row.  This is simple to calculate but is not visually nice for most trees.

Version:
  
$Revision: 1.3 $

Author:
  
Malcolm F Ware (mfw4@cs.waikato.ac.nz)
' where name='weka.PlaceNode1';
UPDATE implementation set fulldescription='LU Decomposition. For an m-by-n matrix A with m &gt;= n, the LU decomposition is an m-by-n unit lower triangular matrix L, an n-by-n upper triangular matrix U, and a permutation vector piv of length m so that A(piv,:) = L*U.  If m &lt; n, then L is m-by-m and U is m-by-n. The LU decompostion with pivoting always exists, even if the matrix is singular, so the constructor will never fail.  The primary use of the LU decomposition is in the solution of square systems of simultaneous linear equations.  This will fail if isNonsingular() returns false. Adapted from the 
JAMA
 package.
Version:
  
$Revision: 1.1.2.2 $

Author:
  
The Mathworks and NIST, Fracpete (fracpete at waikato dot ac dot nz)

See Also:

Serialized Form
' where name='weka.LUDecomposition';
UPDATE implementation set fulldescription='Provides a file filter for FileChoosers that accepts or rejects files based on their extension. Compatible with both java.io.FilenameFilter and javax.swing.filechooser.FileFilter (why there are two I have no idea).
Version:
  
$Revision: 1.5 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)
' where name='weka.ExtensionFileFilter';
UPDATE implementation set fulldescription='The ADNode class implements the ADTree datastructure which increases the speed with which sub-contingency tables can be constructed from a data set in an Instances object. For details, see Cached Sufficient Statistics for Efficient Machine Learning with Large Datasets Andrew Moore, and Mary Soon Lee Journal of Artificial Intelligence Research 8 (1998) 67-91
Version:
  
$Revision: 1.3 $

Author:
  
Remco Bouckaert (rrb@xm.co.nz)

See Also:

Serialized Form
' where name='weka.ADNode';
UPDATE implementation set fulldescription='A SplitEvaluator that produces results for a classification scheme on a nominal class attribute. -W classname 
 Specify the full class name of the classifier to evaluate. 
 -C class index 
 The index of the class for which IR statistics are to be output. (default 1) 
 -I attr index 
 The index of an attribute to output in the tresults. This  attribute should identify an instance in order to know  which instances are tested in a fold (default 1). -P  Add the prediction and target columns to the result file for each fold.
Version:
  
$Revision: 1.21.2.2 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.ClassifierSplitEvaluator';
UPDATE implementation set fulldescription='Abstract class for data generators. ------------------------------------------------------------------- 
 General options are: 
 -r string 
 Name of the relation of the generated dataset. 
 (default = name built using name of used generator and options) 
 -a num 
 Number of attributes. (default = 10) 
 -c num 
 Number of classes. (default = 2) 
 -n num 
 Number of examples. (default = 100) 
 -o filename
 writes the generated dataset to the given file using ARFF-Format. (default = stdout). ------------------------------------------------------------------- 
 Example usage as the main of a datagenerator called RandomGenerator: public static void main(String [] args) {   try {     DataGenerator.makeData(new RandomGenerator(), argv);   } catch (Exception e) {     System.err.println(e.getMessage());   } } ------------------------------------------------------------------ 

Version:
  
$Revision: 1.2 $

Author:
  
Gabi Schmidberger (gabi@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.Generator';
UPDATE implementation set fulldescription='Bean info class for the AbstractDataSink
Version:
  
$Revision: 1.1 $

Author:
  
Mark Hall
' where name='weka.AbstractDataSinkBeanInfo';
UPDATE implementation set fulldescription='Search for TAN = Tree Augmented Naive Bayes network structure      N. Friedman, D. Geiger, M. Goldszmidt.      Bayesian Network Classifiers.      Machine Learning, 29: 131--163, 1997
Version:
  
$Revision: 6236 $

Author:
  
Remco Bouckaert

See Also:

Serialized Form
' where name='weka.TAN';
UPDATE implementation set fulldescription='Bean info class for PredictionAppender.
Since:
  
1.0

Version:
  
$Revision: 1.1.2.1 $

Author:
  
Mark Hall

See Also:

SimpleBeanInfo
' where name='weka.PredictionAppenderBeanInfo';
UPDATE implementation set fulldescription='Conditional probability estimator for a discrete domain conditional upon a discrete domain.
Version:
  
$Revision: 1.4 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)
' where name='weka.DDConditionalEstimator';
UPDATE implementation set fulldescription='Implementation for selecting a C4.5-like binary (!) split for a given dataset.
Version:
  
$Revision: 1.8 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.BinC45ModelSelection';
UPDATE implementation set fulldescription='Implementation representing a single cardinal number. The number is set by a  string representation such as: 
   first   last   1   3 The number is internally converted from 1-based to 0-based (so methods that  set or get numbers not in string format should use 0-based numbers).
Version:
  
$Revision: 1.5 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.SingleIndex';
UPDATE implementation set fulldescription='Implements a fast vector class without synchronized methods. Replaces java.util.Vector. (Synchronized methods tend to be slow.)
Version:
  
$Revision: 1.11.2.1 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.FastVector';
UPDATE implementation set fulldescription='A SplitEvaluator that produces results for a classification scheme on a numeric class attribute.
Version:
  
$Revision: 1.17.2.2 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.RegressionSplitEvaluator';
UPDATE implementation set fulldescription='TabuSearch implements tabu search for learning Bayesian network structures. For details, see for example  R.R. Bouckaert.  Bayesian Belief Networks: from Construction to Inference.  Ph.D. thesis,  University of Utrecht,  1995
Author:
  
Remco Bouckaert (rrb@xm.co.nz) Version: $Revision: 1.2 $

See Also:

Serialized Form
' where name='weka.TabuSearch';
UPDATE implementation set fulldescription='Abstract class gives default implementation of setSource  methods. All other methods must be overridden.
Version:
  
$Revision: 1.6.2.2 $

Author:
  
Richard Kirkby (rkirkby@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.AbstractLoader';
UPDATE implementation set fulldescription='This serializer contains some read/write methods for common classes that are not beans-conform. Currently supported are:    
java.util.HashMap
    
java.util.HashSet
    
java.util.Hashtable
    
java.util.LinkedList
    
java.util.Properties
    
java.util.Stack
    
java.util.TreeMap
    
java.util.TreeSet
    
java.util.Vector
    
javax.swing.DefaultListModel
 Weka classes:    
weka.core.Matrix
    
weka.core.matrix.Matrix

Version:
  
$Revision: 1.1.2.4 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)
' where name='weka.XMLBasicSerialization';
UPDATE implementation set fulldescription='Helper class for logistic model trees (weka.classifiers.trees.lmt.LMT) to implement the  splitting criterion based on residuals of the LogitBoost algorithm.
Version:
  
$Revision: 1.1 $

Author:
  
Niels Landwehr

See Also:

Serialized Form
' where name='weka.ResidualSplit';
UPDATE implementation set fulldescription='This panel controls setting a list of hosts for a RemoteExperiment to use.
Version:
  
$Revision: 1.3 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.HostListPanel';
UPDATE implementation set fulldescription='A sorter for the ARFF-Viewer - necessary because of the custom CellRenderer.
Version:
  
$Revision: 1.1.2.2 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)

See Also:

Serialized Form
' where name='weka.ArffTableSorter';
UPDATE implementation set fulldescription='Interface for evaluators that calculate the "merit" of attributes/subsets as the error of a learning scheme
Version:
  
$Revision: 1.4 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)
' where name='weka.ErrorBasedMeritEvaluator';
UPDATE implementation set fulldescription='Implementation for handling instances and the associated attributes. 
 Enables a set of indexes to a given dataset to be created and used with an algorithm.  This reduces the memory overheads and time required  when manipulating and referencing Instances and their Attributes.
See Also:

Serialized Form
' where name='weka.LBR.Indexes';
UPDATE implementation set fulldescription='Handles the background colors for missing values differently than the DefaultTableCellRenderer.
Version:
  
$Revision: 1.1.2.1 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)

See Also:

Serialized Form
' where name='weka.ArffTableCellRenderer';
UPDATE implementation set fulldescription='Implementation of Active-sets method with BFGS update to solve optimization problem with only bounds constraints in  multi-dimensions.  In this implementation we consider both the lower and higher  bound constraints.  
 Here is the sketch of our searching strategy, and the detailed description of the algorithm can be found in the Appendix of Xin Xu\'s MSc thesis:
 Initialize everything, incl. initial value, direction, etc.
 LOOP (main algorithm):
 1. Perform the line search using the directions for free variables
 1.1  Check all the bounds that are not "active" (i.e. binding variables)      and compute the feasible step length to the bound for each of them
 1.2  Pick up the least feasible step length, say \alpha, and set it as       the upper bound of the current step length, i.e. 0
 1.3  Search for any possible step length<=\alpha that can result the       "sufficient function decrease" (\alpha condition) AND "positive definite       inverse Hessian" (\beta condition), if possible, using SAFEGUARDED polynomial       interpolation.  This step length is "safe" and thus      is used to compute the next value of the free variables .
 1.4  Fix the variable(s) that are newly bound to its constraint(s).
      2. Check whether there is convergence of all variables or their gradients.    If there is, check the possibilities to release any current bindings of    the fixed variables to their bounds based on the "reliable" second-order     Lagarange multipliers if available.  If it\'s available and negative for one    variable, then release it.  If not available, use first-order Lagarange     multiplier to test release.  If there is any released variables, STOP the loop.    Otherwise update the inverse of Hessian matrix and gradient for the newly     released variables and CONTINUE LOOP.
 3. Use BFGS formula to update the inverse of Hessian matrix.  Note the     already-fixed variables must have zeros in the corresponding entries    in the inverse Hessian.
   4. Compute the new (newton) search direction d=H^{-1}*g, where H^{-1} is the     inverse Hessian and g is the Jacobian.  Note that again, the already-    fixed variables will have zero direction.
 ENDLOOP
 A typical usage of this class is to create your own subclass of this class and provide the objective function and gradients as follows:
 class MyOpt extends Optimization{ 
   // Provide the objective function 
   protected double objectiveFunction(double[] x){
       // How to calculate your objective function...
       // ...
   }
   // Provide the first derivatives
   protected double[] evaluateGradient(double[] x){
       // How to calculate the gradient of the objective function...
       // ...
   } 
   // If possible, provide the index^{th} row of the Hessian matrix
   protected double[] evaluateHessian(double[] x, int index){
      // How to calculate the index^th variable\'s second derivative
      // ... 
   }
 } 
 // When it\'s the time to use it, in some routine(s) of other class...
 MyOpt opt = new MyOpt();
 // Set up initial variable values and bound constraints
 double[] x = new double[numVariables];
 // Lower and upper bounds: 1st row is lower bounds, 2nd is upper
 double[] constraints = new double[2][numVariables];
 ...
 // Find the minimum, 200 iterations as default
 x = opt.findArgmin(x, constraints); 
 while(x == null){  // 200 iterations are not enough
    x = opt.getVarbValues();  // Try another 200 iterations
    x = opt.findArgmin(x, constraints);
 }
 // The minimal function value
 double minFunction = opt.getMinFunction();
 ...
 It is recommended that Hessian values be provided so that the second-order  Lagrangian multiplier estimate can be calcluated.  However, if it is not provided,  there is no need to override the 
evaluateHessian()
 function.
 REFERENCES:
 The whole model algorithm is adapted from Chapter 5 and other related chapters in  Gill, Murray and Wright(1981) "Practical Optimization", Academic Press. and Gill and Murray(1976) "Minimization Subject to Bounds on the Variables", NPL  Report NAC72, while Chong and Zak(1996) "An Introduction to Optimization",  John Wiley & Sons, Inc. provides us a brief but helpful introduction to the method. 
 Dennis and Schnabel(1983) "Numerical Methods for Unconstrained Optimization and  Nonlinear Equations", Prentice-Hall Inc. and Press et al.(1992) "Numeric Recipe in C", Second Edition, Cambridge University Press. are consulted for the polynomial interpolation used in the line search implementation.  
 The Hessian modification in BFGS update uses Cholesky factorization and two rank-one  modifications:
 Bk+1 = Bk + (Gk*Gk\')/(Gk\'Dk) + (dGk*(dGk)\'))/[alpha*(dGk)\'*Dk]. 
 where Gk is the gradient vector, Dk is the direction vector and alpha is the step rate.  
 This method is due to Gill, Golub, Murray and Saunders(1974) ``Methods for Modifying  Matrix Factorizations\'\', Mathematics of Computation, Vol.28, No.126, pp 505-535.
Version:
  
$Revision: 1.6 $

Author:
  
Xin Xu (xx5@cs.waikato.ac.nz)
' where name='weka.Optimization';
UPDATE implementation set fulldescription='Interface for objects that want to be able to specify at any given time whether their current configuration allows a particular event to be generated.
Version:
  
$Revision: 1.2 $

Author:
  
Mark Hall
' where name='weka.EventConstraints';
UPDATE implementation set fulldescription='Reduces the dimensionality of the data by projecting  it onto a lower dimensional subspace using a random  matrix with columns of unit length (It will reduce  the number of attributes in the data while preserving  much of its variation like PCA, but at a much less computational cost). 
 It first applies the  NominalToBinary filter to  convert all attributes to numeric before reducing the dimension. It preserves the class attribute. 
 Valid filter-specific options are: 
 -N num 
 The number of dimensions (attributes) the data should be reduced to (default 10; exclusive of the class attribute, if it is set). -P percent 
 The percentage of dimensions (attributes) the data should be reduced to  (exclusive of the class attribute, if it is set). This  -N option is ignored if this option is present or is greater  than zero.
 -D distribution num 
 The distribution to use for calculating the random matrix.
 
 1 - Sparse distribution of: (default) 
      sqrt(3)*{+1 with prob(1/6), 0 with prob(2/3), -1 with prob(1/6)}
 
 2 - Sparse distribution of: 
      {+1 with prob(1/2), -1 with prob(1/2)}
 
 3 - Gaussian distribution 
 -M 
 Replace missing values using the ReplaceMissingValues filter 
 -R num 
 Specify the random seed for the random number generator for calculating the random matrix (default 42). 

Version:
  
$Revision: 6752 $ [1.0 - 22 July 2003 - Initial version (Ashraf M.          Kibriya)]

Author:
  
Ashraf M. Kibriya (amk14@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.RandomProjection';
UPDATE implementation set fulldescription='Part of ADTree implementation. See ADNode.java for more details.
Version:
  
$Revision: 1.2 $

Author:
  
Remco Bouckaert (rrb@xm.co.nz)

See Also:

Serialized Form
' where name='weka.VaryNode';
UPDATE implementation set fulldescription='The implementation of a RIpple-DOwn Rule learner. It generates the default rule first and then the exceptions for the default rule with the least (weighted) error rate.  Then it generates the "best" exceptions for each exception and iterates until pure.  Thus it performs a tree-like expansion of exceptions and the leaf has only default rule but no exceptions. 
 The exceptions are a set of rules that predict the class other than class in default rule.  IREP is used to find out the exceptions. 
 There are five inner classes defined in this class. 
 The first is Ridor_node, which implements one node in the Ridor tree.  It\'s basically composed of a default class and a set of exception rules to the default class.
 The second inner class is RidorRule, which implements a single exception rule  using REP.
 The last three inner classes are only used in RidorRule.  They are Antd, NumericAntd  and NominalAntd, which all implement a single antecedent in the RidorRule. 
 The Antd class is an abstract class, which has two subclasses, NumericAntd and  NominalAntd, to implement the corresponding abstract functions.  These two subclasses implement the functions related to a antecedent with a nominal attribute and a numeric  attribute respectively.

Version:
  
$Revision: 5180 $

See Also:

Serialized Form
' where name='weka.Ridor';
UPDATE implementation set fulldescription='Implementation that encapsulates a sub task for distributed boundary visualization. Produces probability distributions for each pixel in one row of the visualization.
Since:
  
1.0

Version:
  
$Revision: 1.2 $

Author:
  
Mark Hall

See Also:

Task
, 
Serialized Form
' where name='weka.RemoteBoundaryVisualizerSubTask';
UPDATE implementation set fulldescription='GUI customizer for the filter bean
Version:
  
$Revision: 1.4.2.1 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.FilterCustomizer';
UPDATE implementation set fulldescription='Exception that is raised by an object that is unable to process some of the attribute types it has been passed.
Version:
  
$Revision: 1.3 $

Author:
  
Richard Kirkby (rkirkby@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.UnsupportedAttributeTypeException';
UPDATE implementation set fulldescription='Bean info class for the saver bean
Version:
  
$Revision: 1.1 $

Author:
  
Stefan Mutter
' where name='weka.SaverBeanInfo';
UPDATE implementation set fulldescription='Converts a string attribute (i.e. unspecified number of values) to nominal (i.e. set number of values). You should ensure that all string values that will appear are represented in the dataset.
 Valid filter-specific options are: 
 -C col 
 Index of the attribute to be changed. (default last)

Version:
  
$Revision: 7104 $

Author:
  
Len Trigg (len@reeltwo.com)

See Also:

Serialized Form
' where name='weka.StringToNominal';
UPDATE implementation set fulldescription='This class implements a parser to read properties that have a hierarchy(i.e. tree) structure.  Conceptually it\'s similar to  the XML DOM/SAX parser but of course is much simpler and  uses dot as the seperator of levels instead of back-slash.
 It provides interfaces to both build a parser tree and traverse the tree. 
 Note that this implementation does not lock the tree when different threads are traversing it simultaneously, i.e. it\'s NOT synchronized and multi-thread safe.  It is recommended that later implementation extending this class provide a locking scheme and override the  functions with the "synchronized" modifier (most of them are  goToXXX() and information accessing functions).

Version:
  
$Revision: 1.2 $

Author:
  
Xin Xu (xx5@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.HierarchyPropertyParser';
UPDATE implementation set fulldescription='This panel allows the user to visualize a dataset (and if provided) a classifier\'s/clusterer\'s predictions in two dimensions. If the user selects a nominal attribute as the colouring attribute then each point is drawn in a colour that corresponds to the discrete value of that attribute for the instance. If the user selects a numeric attribute to colour on, then the points are coloured using a spectrum ranging from blue to red (low values to high). When a classifier\'s predictions are supplied they are plotted in one of two ways (depending on whether the class is nominal or numeric).
 For nominal class: an error made by a classifier is plotted as a square in the colour corresponding to the class it predicted.
 For numeric class: predictions are plotted as varying sized x\'s, where the size of the x is related to the magnitude of the error.
Version:
  
$Revision: 1.21.2.4 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz), Malcolm Ware (mfw4@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.VisualizePanel';
UPDATE implementation set fulldescription='A downsized version of the ArffViewer, displaying only one Instances-Object.
Version:
  
$Revision: 1.1.2.1 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)

See Also:

ArffViewer
, 
Serialized Form
' where name='weka.ViewerDialog';
UPDATE implementation set fulldescription='Stores split information.
Version:
  
$Revision: 1.1 $

Author:
  
Yong Wang (yongwang@cs.waikato.ac.nz), Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.YongSplitInfo';
UPDATE implementation set fulldescription='Behaves the same as PairedTTester, only it uses the corrected resampled t-test statistic.
 For more information see:
 Claude Nadeau and Yoshua Bengio, "Inference for the Generalization Error," Machine Learning, 2001.
Version:
  
$Revision: 1.6 $

Author:
  
Richard Kirkby (rkirkby@cs.waikato.ac.nz)
' where name='weka.PairedCorrectedTTester';
UPDATE implementation set fulldescription='Builds a description of a Bayes Net classifier stored in XML BIF 0.3 format. See http://www-2.cs.cmu.edu/~fgcozman/Research/InterchangeFormat/ for details on XML BIF.
Version:
  
$Revision: 1.7.2.1 $

Author:
  
Remco Bouckaert (rrb@xm.co.nz)

See Also:

Serialized Form
' where name='weka.BIFReader';
UPDATE implementation set fulldescription='This class takes any JComponent and outputs it to a JPEG-file. Scaling is by default disabled, since we always take a screenshot.
Version:
  
$Revision: 5918 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)

See Also:

#setScalingEnabled()
' where name='weka.JPEGWriter';
UPDATE implementation set fulldescription='Bean that accepts data sets, training sets, test sets and produces both a training and test set by randomly spliting the data
Version:
  
$Revision: 1.5.2.1 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.TrainTestSplitMaker';
UPDATE implementation set fulldescription='In a chain of data manipulators some behaviour is common. TableMap provides most of this behaviour and can be subclassed by filters that only need to override a handful of specific methods. TableMap implements TableModel by routing all requests to its model, and TableModelListener by routing all events to its listeners. Inserting a TableMap which has not been subclassed into a chain of table filters should have no effect.
 Source can be found 
here
. version 1.4 12/17/97
Version:
  
$Revision: 1.1.2.1 $

Author:
  
Philip Milne

See Also:

Serialized Form
' where name='weka.TableMap';
UPDATE implementation set fulldescription='Writes to a destination in arff text format. Valid options: -i input arff file 
 The input filw in ARFF format. 
 -o the output file 
 The output file. The prefix of the output file is sufficient. If no output file is given, Saver tries to use standard out. 

Version:
  
$Revision: 7116 $

Author:
  
Stefan Mutter (mutter@cs.waikato.ac.nz)

See Also:

Saver
, 
Serialized Form
' where name='weka.ArffSaver';
UPDATE implementation set fulldescription='This panel controls setting a list of values for an arbitrary resultgenerator property for an experiment to iterate over.
Version:
  
$Revision: 1.8 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.GeneratorPropertyIteratorPanel';
UPDATE implementation set fulldescription='Creates a new dataset with a boolean attribute replacing a nominal attribute.  In the new dataset, a value of 1 is assigned to an instance that exhibits a particular range of attribute values, a 0 to an instance that doesn\'t. The boolean attribute is coded as numeric by default.
 Valid filter-specific options are: 
 -C col 
 Index of the attribute to be changed. (default "last")
 -V index1,index2-index4,...
 Specify list of values to indicate. First and last are valid indices. (default "last")
 -N 
 Set if new boolean attribute nominal.

Version:
  
$Revision: 1.2 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.MakeIndicator';
UPDATE implementation set fulldescription='Displays a property sheet where (supported) properties of the target object may be edited.
Version:
  
$Revision: 6080 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.PropertySheetPanel';
UPDATE implementation set fulldescription='GUI customizer for the class assigner bean
Version:
  
$Revision: 1.3.2.1 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.ClassAssignerCustomizer';
UPDATE implementation set fulldescription='Bean info class for the train test split maker bean
Since:
  
1.0

Version:
  
$Revision: 1.1 $

Author:
  
Mark Hall

See Also:

AbstractTrainAndTestSetProducerBeanInfo

' where name='weka.TrainTestSplitMakerBeanInfo';
UPDATE implementation set fulldescription='Interface to something that can produce test sets
Version:
  
$Revision: 1.2 $

Author:
  
Mark Hall
' where name='weka.TestSetProducer';
UPDATE implementation set fulldescription='Implementation implementing the predictive apriori algorithm to mine association rules.  It searches with an increasing support threshold for the best 
n
 rules  concerning a support-based corrected confidence value.  Reference: T. Scheffer (2001). 
Finding Association Rules That Trade Support  Optimally against Confidence
. Proc of the 5th European Conf. on Principles and Practice of Knowledge Discovery in Databases (PKDD\'01), pp. 424-435. Freiburg, Germany: Springer-Verlag. 
 The implementation follows the paper expect for adding a rule to the output of the 
n
 best rules. A rule is added if: the expected predictive accuracy of this rule is among the 
n
 best and it is  not subsumed by a rule with at least the same expected predictive accuracy (out of an unpublished manuscript from T. Scheffer).  Valid option is:
 -N required number of rules 
 The required number of rules (default: 100). 

Version:
  
$Revision: 1.3.2.3 $

Author:
  
Stefan Mutter (mutter@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.PredictiveApriori';
UPDATE implementation set fulldescription='This class extends BoundaryPanel with code for distributing the processing necessary to create a visualization among a list of remote machines. Specifically, a visualization is broken down and processed row by row using the available remote computers.
Since:
  
1.0

Version:
  
$Revision: 1.5 $

Author:
  
Mark Hall

See Also:

BoundaryPanel
, 
Serialized Form
' where name='weka.BoundaryPanelDistributed';
UPDATE implementation set fulldescription='Implementation for a regression scheme that employs any distribution classifier on a copy of the data that has the class attribute (equal-width) discretized. The predicted value is the expected value of the  mean class value for each discretized interval (based on the  predicted probabilities for each interval).
 Valid options are:
 -D 
 Produce debugging output. 
 -B 
 
 Number of bins for equal-width discretization (default 10).

Version:
  
$Revision: 1.30.2.1 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz), Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.RegressionByDiscretization';
UPDATE implementation set fulldescription='An abstract instance filter that assumes instances form time-series data and performs some merging of attribute values in the current instance with  attribute attribute values of some previous (or future) instance. For instances where the desired value is unknown either the instance may be dropped, or missing values used.
 Valid filter-specific options are:
 -R index1,index2-index4,...
 Specify list of columns to calculate new values for. First and last are valid indexes. (default none)
 -V 
 Invert matching sense (i.e. calculate for all non-specified columns)
 -I num 
 The number of instances forward to merge values between. A negative number indicates taking values from a past instance. (default -1) 
 -M 
 For instances at the beginning or end of the dataset where the translated values are not known, remove those instances (default is to use missing values). 

Version:
  
$Revision: 1.3.2.2 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.AbstractTimeSeries';
UPDATE implementation set fulldescription='ICSSearchAlgorithm implements Conditional Independence based search algorithm for Bayes Network structure learning.
Version:
  
$Revision: 1.3.2.1 $

Author:
  
Remco Bouckaert

See Also:

Serialized Form
' where name='weka.ICSSearchAlgorithm';
UPDATE implementation set fulldescription='
Version:
  
$Revision: 1.2.2.1 $

Author:
  
Peter A. Flach, Nicolas Lachiche

See Also:

Serialized Form
' where name='weka.IndividualInstance';
UPDATE implementation set fulldescription='This class contains a color name and the rgb values of that color
Version:
  
$Revision: 1.3 $

Author:
  
Malcolm Ware (mfw4@cs.waikato.ac.nz)
' where name='weka.NamedColor';
UPDATE implementation set fulldescription='A panel that displays an instance summary for a set of instances and lets the user open a set of instances from either a file or URL.
Version:
  
$Revision: 1.7 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.SetInstancesPanel';
UPDATE implementation set fulldescription='Event encapsulating a test set
Version:
  
$Revision: 1.2 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.TestSetEvent';
UPDATE implementation set fulldescription='Implementation for performing a hill climbing search (either forwards or backwards). 
 Valid options are: 
 -B 
 Use a backward search instead of a forward one. 
 -P 
 
 Specify a starting set of attributes. Eg 1,4,7-9. 
 -R 
 Produce a ranked list of attributes. 
 -T 
 
 Specify a threshold by which the AttributeSelection module can. 
 discard attributes. Use in conjunction with -R 

Version:
  
$Revision: 7269 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.GreedyStepwise';
UPDATE implementation set fulldescription='A bean that counts instances streamed to it.
Version:
  
$Revision: 1.3 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.InstanceCounter';
UPDATE implementation set fulldescription='Abstract attribute evaluator. Evaluate attributes individually.
Version:
  
$Revision: 1.7 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.AttributeEvaluator';
UPDATE implementation set fulldescription='Changes the date format used by a date attribute. This is most useful for converting to a format with less precision, for example, from an absolute date to day of year, etc. This changes the format string, and changes the date values to those that would be parsed by the new format.
 Valid filter-specific options are: 
 -C col 
 The column containing the date attribute to be changed. (default last)
 -F format 
 The output date format (default corresponds to ISO-8601 format).

Version:
  
$Revision: 1.1 $

Author:
  
Len Trigg

See Also:

Serialized Form
' where name='weka.ChangeDateFormat';
UPDATE implementation set fulldescription='An instance filter that assumes instances form time-series data and replaces attribute values in the current instance with the equivalent attribute values of some previous (or future) instance. For instances where the desired value is unknown either the instance may be dropped, or missing values used.
 Valid filter-specific options are:
 -R index1,index2-index4,...
 Specify list of columns to calculate new values for. First and last are valid indexes. (default none)
 -V 
 Invert matching sense (i.e. calculate for all non-specified columns)
 -I num 
 The number of instances forward to translate values between. A negative number indicates taking values from a past instance. (default -1) 
 -M 
 For instances at the beginning or end of the dataset where the translated values are not known, use missing values (default is to remove those instances).

Version:
  
$Revision: 1.3.2.3 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.TimeSeriesTranslate';
UPDATE implementation set fulldescription='A class for storing stats on a paired comparison (t-test and correlation)
Version:
  
$Revision: 1.6 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)
' where name='weka.PairedStats';
UPDATE implementation set fulldescription='Abstract class for model selection criteria.
Version:
  
$Revision: 1.5 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.ModelSelection';
UPDATE implementation set fulldescription='Simple probability estimator that places a single normal distribution over the observed values.
Version:
  
$Revision: 1.4 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.MahalanobisEstimator';
UPDATE implementation set fulldescription='Implementation providing keys to the hash table.
See Also:

Serialized Form
' where name='weka.ConsistencySubsetEval.hashKey';
UPDATE implementation set fulldescription='This class is for loading resources from a JAR archive.
Version:
  
$Revision: 1.1.2.1 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)
' where name='weka.Loader';
UPDATE implementation set fulldescription='Implements Winnow and Balanced Winnow algorithms by N. Littlestone. For more information, see
 N. Littlestone (1988). 
 Learning quickly when irrelevant attributes are abound: A new linear threshold algorithm
. Machine Learning 2, pp. 285-318.
 and N. Littlestone (1989). 
 Mistake bounds and logarithmic  linear-threshold learning algorithms
. Technical report UCSC-CRL-89-11, University of California, Santa Cruz.
 Valid options are:
 -L 
 Use the baLanced variant (default: false)
 -I num 
 The number of iterations to be performed. (default 1)
 -A double 
 Promotion coefficient alpha. (default 2.0)
 -B double 
 Demotion coefficient beta. (default 0.5)
 -W double 
 Starting weights of the prediction coeffs. (default 2.0)
 -H double 
 Prediction threshold. (default -1.0 == number of attributes)
 -S int 
 Random seed to shuffle the input. (default 1), -1 == no shuffling

Version:
  
$Revision: 1.8 $

Author:
  
J. Lindgren (jtlindgr
cs.helsinki.fi)

See Also:

Serialized Form
' where name='weka.Winnow';
UPDATE implementation set fulldescription='Implementation for performing a racing search. 
 For more information see: 
 Moore, A. W. and Lee, M. S. (1994). Efficient algorithms for minimising cross validation error. Proceedings of the Eleventh International Conference on Machine Learning. pp 190--198. 
 Valid options are:
 -R race type
 0 = forward, 1 = backward, 2 = schemata, 3 = rank. 
 -L significance level 
 significance level to use for t-tests. 
 -T threshold 
 threshold for considering mean errors of two subsets the same 
 -F xval type 
 0 = 10 fold, 1 = leave-one-out (selected automatically for schemata race -A attribute evaluator 
 the attribute evaluator to use when doing a rank search 
 -Q 
 produce a ranked list of attributes. Selecting this option forces the race type to be forward. Racing continues until *all* attributes have been selected, thus producing a ranked list of attributes. 
 -N number to retain 
 Specify the number of attributes to retain. Overides any threshold.  Use in conjunction with -Q. 
 -J threshold 
 Specify a threshold by which the AttributeSelection module can discard attributes. Use in conjunction with -Q. 
 -Z 
 Turn on verbose output for monitoring the search 

Version:
  
$Revision: 1.14.2.2 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.RaceSearch';
UPDATE implementation set fulldescription='The main class for the Weka explorer. Lets the user create, open, save, configure, datasets, and perform ML analysis.
Version:
  
$Revision: 6209 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.Explorer';
UPDATE implementation set fulldescription='This class is a container for plottable data. Instances form the primary data. An optional array of classifier/clusterer predictions (associated 1 for 1 with the instances) can also be provided.
Version:
  
$Revision: 5707 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)
' where name='weka.PlotData2D';
UPDATE implementation set fulldescription='RepeatedHillClimber searches for Bayesian network structures by repeatedly generating a random network and apply hillclimber on it. The best network found is returned.
Author:
  
Remco Bouckaert (rrb@xm.co.nz) Version: $Revision: 1.2.2.1 $

See Also:

Serialized Form
' where name='weka.RepeatedHillClimber';
UPDATE implementation set fulldescription='This can be used by the  neuralnode to perform all it\'s computations (as a sigmoid unit).
Version:
  
$Revision: 1.4 $

Author:
  
Malcolm Ware (mfw4@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.SigmoidUnit';
UPDATE implementation set fulldescription='This panel just displays relation name, number of instances, and number of attributes.
Version:
  
$Revision: 1.4 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.InstancesSummaryPanel';
UPDATE implementation set fulldescription='Interface specifying routines that all weka beans should implement in order to allow the bean environment to exercise some control over the bean and also to allow the bean to excercise some control over connections. Beans may want to  impose constraints in terms of the number of connections they will allow via a particular  listener interface. Some beans may only want to be registered as a listener for a particular event type with only one source, or perhaps a limited number of sources.
Since:
  
1.0

Version:
  
$Revision: 1.1.2.1 $

Author:
  
Mark Hall
' where name='weka.BeanCommon';
UPDATE implementation set fulldescription='Implementation implementing an Id3 decision tree classifier. For more information, see
 R. Quinlan (1986). 
Induction of decision trees
. Machine Learning. Vol.1, No.1, pp. 81-106.

Version:
  
$Revision: 1.14.2.1 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.Id3';
UPDATE implementation set fulldescription='Simple class that extends the Instances class making it possible to create subsets of instances that reference their source set. Is used by ADTree to make reweighting of instances easy to manage.
Version:
  
$Revision: 1.2 $

Author:
  
Richard Kirkby (rkirkby@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.ReferenceInstances';
UPDATE implementation set fulldescription='This class encapsulates a linear regression function. It is a classifier but does not learn the function itself, instead it is constructed with coefficients and intercept obtained elsewhere. The buildClassifier method must still be called however as this stores a copy of the training data\'s header for use in printing the model to the console.
Version:
  
$Revision: 1.2 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.PreConstructedLinearModel';
UPDATE implementation set fulldescription='This class contains utility routines for visualization
Version:
  
$Revision: 1.7 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)
' where name='weka.VisualizeUtils';
UPDATE implementation set fulldescription='This interface class has been added to facilitate the addition of other layout engines to this package. Any class that wants to lay out a graph should implement this interface.
Version:
  
$Revision: 1.3.2.1 $ - 24 Apr 2003 - Initial version (Ashraf M. Kibriya)

Author:
  
Ashraf M. Kibriya (amk14@cs.waikato.ac.nz)
' where name='weka.LayoutEngine';
UPDATE implementation set fulldescription='' where name='weka.KStarConstants';
UPDATE implementation set fulldescription='A 
Tag
 simply associates a numeric ID with a String description.
Version:
  
$Revision: 1.5.2.1 $

Author:
  
Len Trigg
' where name='weka.Tag';
UPDATE implementation set fulldescription='This panel displays summary statistics about an attribute: name, type number/% of missing/unique values, number of distinct values. For numeric attributes gives some other stats (mean/std dev), for nominal attributes gives counts for each attribute value.
Version:
  
$Revision: 1.7.2.3 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.AttributeSummaryPanel';
UPDATE implementation set fulldescription='Implementation for building and using a Complement class Naive Bayes classifier. For more information see,
 ICML-2003 
Tackling the poor assumptions of Naive Bayes Text Classifiers
 P.S.: TF, IDF and length normalization transforms, as described in the paper, can be performed through weka.filters.unsupervised.StringToWordVector. Valid options for the classifier are:
 -N 
 Normalizes word weights for each class.
 -S val 
 The smoothing value to use to avoid zero WordGivenClass probabilities (default 1.0).
Version:
  
$Revision: 1.2.2.1 $

Author:
  
Ashraf M. Kibriya (amk14@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.ComplementNaiveBayes';
UPDATE implementation set fulldescription='A Splash window. Usage: MyApplication is your application class. Create a Splasher class which opens the splash window, invokes the main method of your Application class, and disposes the splash window afterwards. Please note that we want to keep the Splasher class and the SplashWindow class as small as possible. The less code and the less classes must be loaded into the JVM to open the splash screen, the faster it will appear. class Splasher {    public static void main(String[] args) {         SplashWindow.splash(Startup.class.getResource("splash.gif"));         MyApplication.main(args);         SplashWindow.disposeSplash();    } }
Version:
  
$Revision: 1.1.2.2 $

Author:
  
Werner Randelshofer, Mark Hall

See Also:

Serialized Form
' where name='weka.SplashWindow';
UPDATE implementation set fulldescription='This panel records the number of weka tasks running and displays a simple bird animation while their are active tasks
Version:
  
$Revision: 1.6.4.1 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.WekaTaskMonitor';
UPDATE implementation set fulldescription='Lazy Bayesian Rules implement a lazy learning approach to lessening the attribute-independence assumption of naive Bayes.  For each object to be classified, LBR selects a set of attributes for which the attribute independence assumption should not be made.  All remaining attributes are treated as independent of each other given the class and the selected set of attributes.  LBR has demonstrated very high accuracy.  Its training time is low but its classification time is high due to the use of a lazy methodology.  This implementation does not include caching, that can substantially reduce classification time when multiple classifications are performed for a single training set. For more information, see
 Zijian Zheng &amp; G. Webb, (2000). 
Lazy Learning of Bayesian Rules.
 Machine Learning, 41(1): 53-84.

Version:
  
$Revision: 1.4.2.2 $

Author:
  
Zhihai Wang (zhw@deakin.edu.au) : July 2001 implemented the algorithm, Jason Wells (wells@deakin.edu.au) : November 2001 added instance referencing via indexes

See Also:

Serialized Form
' where name='weka.LBR';
UPDATE implementation set fulldescription='Bean info class for the clusterer performance evaluator
Version:
  
$Revision: 1.1 $

Author:
  
Stefan Mutter
' where name='weka.ClustererPerformanceEvaluatorBeanInfo';
UPDATE implementation set fulldescription='This class plots datasets in two dimensions. It can also plot classifier errors and clusterer predictions.
Version:
  
$Revision: 1.20.2.2 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.Plot2D';
UPDATE implementation set fulldescription='Implementation representing the body of a rule.
Version:
  
$Revision: 1.3.2.1 $

Author:
  
Amelie Deltour

See Also:

Serialized Form
' where name='weka.Body';
UPDATE implementation set fulldescription='Implementation implementing the voting feature interval classifier. For numeric attributes, upper and lower boundaries (intervals)  are constructed  around each class. Discrete attributes have point intervals. Class counts are recorded for each interval on each feature. Classification is by voting. Missing values are ignored. Does not handle numeric class. 
 Have added a simple attribute weighting scheme. Higher weight is assigned to more confident intervals, where confidence is a function of entropy: weight (att_i) = (entropy of class distrib att_i / max uncertainty)^-bias. Faster than NaiveBayes but slower than HyperPipes. 
  Confidence: 0.01 (two tailed) Dataset                   (1) VFI \'-B  | (2) Hyper (3) Naive                         ------------------------------------ anneal.ORIG               (10)   74.56 |   97.88 v   74.77 anneal                    (10)   71.83 |   97.88 v   86.51 v audiology                 (10)   51.69 |   66.26 v   72.25 v autos                     (10)   57.63 |   62.79 v   57.76 balance-scale             (10)   68.72 |   46.08 *   90.5  v breast-cancer             (10)   67.25 |   69.84 v   73.12 v wisconsin-breast-cancer   (10)   95.72 |   88.31 *   96.05 v horse-colic.ORIG          (10)   66.13 |   70.41 v   66.12 horse-colic               (10)   78.36 |   62.07 *   78.28 credit-rating             (10)   85.17 |   44.58 *   77.84 * german_credit             (10)   70.81 |   69.89 *   74.98 v pima_diabetes             (10)   62.13 |   65.47 v   75.73 v Glass                     (10)   56.82 |   50.19 *   47.43 * cleveland-14-heart-diseas (10)   80.01 |   55.18 *   83.83 v hungarian-14-heart-diseas (10)   82.8  |   65.55 *   84.37 v heart-statlog             (10)   79.37 |   55.56 *   84.37 v hepatitis                 (10)   83.78 |   63.73 *   83.87 hypothyroid               (10)   92.64 |   93.33 v   95.29 v ionosphere                (10)   94.16 |   35.9  *   82.6  * iris                      (10)   96.2  |   91.47 *   95.27 * kr-vs-kp                  (10)   88.22 |   54.1  *   87.84 * labor                     (10)   86.73 |   87.67     93.93 v lymphography              (10)   78.48 |   58.18 *   83.24 v mushroom                  (10)   99.85 |   99.77 *   95.77 * primary-tumor             (10)   29    |   24.78 *   49.35 v segment                   (10)   77.42 |   75.15 *   80.1  v sick                      (10)   65.92 |   93.85 v   92.71 v sonar                     (10)   58.02 |   57.17     67.97 v soybean                   (10)   86.81 |   86.12 *   92.9  v splice                    (10)   88.61 |   41.97 *   95.41 v vehicle                   (10)   52.94 |   32.77 *   44.8  * vote                      (10)   91.5  |   61.38 *   90.19 * vowel                     (10)   57.56 |   36.34 *   62.81 v waveform                  (10)   56.33 |   46.11 *   80.02 v zoo                       (10)   94.05 |   94.26     95.04 v                          ------------------------------------                                (v| |*) |  (9|3|23)  (22|5|8)  For more information, see 
 Demiroz, G. and Guvenir, A. (1997) "Classification by voting feature  intervals", 
ECML-97
. 
 Valid options are:
 -C 
 Don\'t Weight voting intervals by confidence. 
 -B 
 
 Set exponential bias towards confident intervals. default = 0.6 

Version:
  
$Revision: 7181 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.VFI';
UPDATE implementation set fulldescription='Implementation for handling a rule (partial tree) for a decision list.
Version:
  
$Revision: 1.9 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.ClassifierDecList';
UPDATE implementation set fulldescription='This class takes any JComponent and outputs it to a file. Scaling is by default enabled.
Version:
  
$Revision: 1.1.2.1 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)

See Also:

#setScalingEnabled()
' where name='weka.JComponentWriter';
UPDATE implementation set fulldescription='A PropertyEditor for arrays of objects that themselves have property editors.
Version:
  
$Revision: 5176 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.GenericArrayEditor';
UPDATE implementation set fulldescription='The main panel of the ArffViewer. It has a reference to the menu, that an implementing JFrame only needs to add via the setJMenuBar(JMenuBar) method.
Version:
  
$Revision: 1.1.2.2 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)

See Also:

Serialized Form
' where name='weka.ArffViewerMainPanel';
UPDATE implementation set fulldescription='Converts all numeric attributes into binary attributes (apart from the class attribute): if the value of the numeric attribute is exactly zero, the value of the new attribute will be zero. If the value of the numeric attribute is missing, the value of the new attribute will be missing. Otherwise, the value of the new attribute will be one. The new attributes will nominal.

Version:
  
$Revision: 1.3 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.NumericToBinary';
UPDATE implementation set fulldescription='BayesNetEstimator is the base class for estimating the conditional probability tables of a Bayes network once the structure has been learned.
Version:
  
$Revision: 1.1 $

Author:
  
Remco Bouckaert (rrb@xm.co.nz)

See Also:

Serialized Form
' where name='weka.BayesNetEstimator';
UPDATE implementation set fulldescription='Implementation for selecting a C4.5-type split for a given dataset.
Version:
  
$Revision: 1.8 $y

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.C45ModelSelection';
UPDATE implementation set fulldescription='Converts String attributes into a set of attributes representing word occurrence information from the text contained in the strings. The set of words (attributes) is determined by the first batch filtered (typically training data).
Version:
  
$Revision: 1.8.2.1 $

Author:
  
Len Trigg (len@reeltwo.com), Stuart Inglis (stuart@reeltwo.com)

See Also:

Serialized Form
' where name='weka.StringToWordVector';
UPDATE implementation set fulldescription='Conditional probability estimator for a numeric domain conditional upon a discrete domain (utilises separate normal estimators for each discrete conditioning value).
Version:
  
$Revision: 1.4 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)
' where name='weka.NDConditionalEstimator';
UPDATE implementation set fulldescription='This panel allows the user to select, configure, and run a scheme that learns associations.
Version:
  
$Revision: 1.17.2.2 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.AssociationsPanel';
UPDATE implementation set fulldescription='Bean info class for the training set maker bean
Version:
  
$Revision: 1.1.2.1 $

Author:
  
Mark Hall
' where name='weka.TrainingSetMakerBeanInfo';
UPDATE implementation set fulldescription='Implementation for a node in a linked list. Used in best first search.
Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.BestFirst.Link2';
UPDATE implementation set fulldescription='BeanInfo class for AbstractTrainingSetProducer
Version:
  
$Revision: 1.1 $

Author:
  
Mark Hall
' where name='weka.AbstractTrainingSetProducerBeanInfo';
UPDATE implementation set fulldescription='Interface to something that can produce measures other than those calculated by evaluation modules.
Version:
  
$Revision: 1.7 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)
' where name='weka.AdditionalMeasureProducer';
UPDATE implementation set fulldescription='A comparator for the Instance class. it can be used with or without the class label. Missing values are sorted at the beginning.
 Can be used as comparator in the sorting and binary search algorithms of 
Arrays
 and 
Collections
.
Version:
  
$Revision: 1.2.2.2 $

Author:
  
FracPete (fracpete at cs dot waikato dot ac dot nz)

See Also:

Instance
, 
Arrays
, 
Collections
, 
Serialized Form
' where name='weka.InstanceComparator';
UPDATE implementation set fulldescription='Bean info class for the loader bean
Version:
  
$Revision: 1.1 $

Author:
  
Mark Hall
' where name='weka.LoaderBeanInfo';
UPDATE implementation set fulldescription='Implementation for selecting a threshold on a probability output by a distribution classifier. The threshold is set so that a given performance measure is optimized. Currently this is the F-measure. Performance is measured either on the training data, a hold-out set or using cross-validation. In addition, the probabilities returned by the base learner can have their range expanded so that the output probabilities will reside between 0 and 1 (this is useful if the scheme normally produces probabilities in a very narrow range).
 Valid options are:
 -C num 
 The class for which threshold is determined. Valid values are: 1, 2 (for first and second classes, respectively), 3 (for whichever class is least frequent), 4 (for whichever class value is most  frequent), and 5 (for the first class named any of "yes","pos(itive)", "1", or method 3 if no matches). (default 5). 
 -W classname 
 Specify the full class name of the base classifier. 
 -X num 
  Number of folds used for cross validation. If just a hold-out set is used, this determines the size of the hold-out set (default 3).
 -R integer 
 Sets whether confidence range correction is applied. This can be used to ensure the confidences range from 0 to 1. Use 0 for no range correction, 1 for correction based on the min/max values seen during threshold selection (default 0).
 -S seed 
 Random number seed (default 1).
 -E integer 
 Sets the evaluation mode. Use 0 for evaluation using cross-validation, 1 for evaluation using hold-out set, and 2 for evaluation on the training data (default 1).
 Options after -- are passed to the designated sub-classifier. 

Version:
  
$Revision: 1.30.2.3 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.ThresholdSelector';
UPDATE implementation set fulldescription='Implements Alex J.Smola and Bernhard Scholkopf sequential minimal optimization algorithm for training a support vector regression using polynomial or RBF kernels.  This implementation globally replaces all missing values and transforms nominal attributes into binary ones. It also normalizes all attributes by default. (Note that the coefficients in the output are based on the normalized/standardized data, not the original data.) For more information on the SMO algorithm, see
 Alex J. Smola, Bernhard Scholkopf (1998). 
A Tutorial on Support Vector Regression
.  NeuroCOLT2 Technical Report Series - NC2-TR-1998-030. 
 S.K. Shevade, S.S. Keerthi, C. Bhattacharyya, K.R.K. Murthy,  
Improvements to SMO Algorithm for SVM Regression
.  Technical Report CD-99-16, Control Division Dept of Mechanical and Production Engineering,  National University of Singapore.

Version:
  
$Revision: 1.3.2.3 $

Author:
  
Sylvain Roy (sro33@student.canterbury.ac.nz)

See Also:

Serialized Form
' where name='weka.SMOreg';
UPDATE implementation set fulldescription='Encapsulates performance functions for two-class problems.
Version:
  
$Revision: 1.7 $

Author:
  
Len Trigg (len@reeltwo.com)
' where name='weka.TwoClassStats';
UPDATE implementation set fulldescription='Describe interface 
TextListener
 here.
Version:
  
$Revision: 1.2.2.1 $

Author:
  
Mark Hall
' where name='weka.GraphListener';
UPDATE implementation set fulldescription='IB1-type classifier. Uses a simple distance measure to find the training instance closest to the given test instance, and predicts the same class as this training instance. If multiple instances are the same (smallest) distance to the test instance, the first one found is used.  For more information, see 
 Aha, D., and D. Kibler (1991) "Instance-based learning algorithms", 
Machine Learning
, vol.6, pp. 37-66.

Version:
  
$Revision: 1.13.2.1 $

Author:
  
Stuart Inglis (singlis@cs.waikato.ac.nz), Len Trigg (trigg@cs.waikato.ac.nz), Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.IB1';
UPDATE implementation set fulldescription='Implementation for handling a linked list. Used in best first search. Extends the Vector class.
Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.BestFirst.LinkedList2';
UPDATE implementation set fulldescription='Implementation for a Bayes Network classifier based on K2 for learning structure. K2 is a hill climbing algorihtm by Greg Cooper and Ed Herskovitz, Proceedings Uncertainty in Artificial Intelligence, 1991, Also in Machine Learning, 1992 pages 309-347. Works with nominal variables and no missing values only.
Version:
  
$Revision: 1.2.2.1 $

Author:
  
Remco Bouckaert (rrb@xm.co.nz)

See Also:

Serialized Form
' where name='weka.K2';
UPDATE implementation set fulldescription='Implementation for performing additive logistic regression.. This class performs classification using a regression scheme as the  base learner, and can handle multi-class problems.  For more information, see
 Friedman, J., T. Hastie and R. Tibshirani (1998) 
Additive Logistic Regression: a Statistical View of Boosting
  
download  postscript
. 
 Valid options are:
 -D 
 Turn on debugging output.
 -W classname 
 Specify the full class name of a weak learner as the basis for  boosting (required).
 -I num 
 Set the number of boost iterations (default 10). 
 -Q 
 Use resampling instead of reweighting.
 -S seed 
 Random number seed for resampling (default 1).
 -P num 
 Set the percentage of weight mass used to build classifiers (default 100). 
 -F num 
 Set number of folds for the internal cross-validation (default 0 -- no cross-validation). 
 -R num 
 Set number of runs for the internal cross-validation (default 1). 
 -L num 
  Set the threshold for the improvement of the average loglikelihood (default -Double.MAX_VALUE). 
 -H num 
  Set the value of the shrinkage parameter (default 1). 
 Options after -- are passed to the designated learner.

Version:
  
$Revision: 6092 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz), Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.LogitBoost';
UPDATE implementation set fulldescription='This class parses input in DOT format, and builds the datastructures that are passed to it. It is NOT 100% compatible with the DOT format. The GraphNode and GraphEdge classes do not have any provision for dealing with different colours or  shapes of nodes, there can however, be a different label and ID for a node. It  also does not do anything for labels for edges. However, this class won\'t crash or throw an exception if it encounters any of the above attributes of an edge or a node. This class however, won\'t be able to deal with things like subgraphs and grouping of nodes.
Version:
  
$Revision: 1.2.2.1 $ - 23 Apr 2003 - Initial version (Ashraf M. Kibriya)

Author:
  
Ashraf M. Kibriya (amk14@cs.waikato.ac.nz)
' where name='weka.DotParser';
UPDATE implementation set fulldescription='Bean that encapsulates weka.gui.visualize.MatrixPanel for displaying a scatter plot matrix.
Version:
  
$Revision: 1.6 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.ScatterPlotMatrix';
UPDATE implementation set fulldescription='Implementation for encapsulating a connection between two beans. Also maintains a list of all connections
Version:
  
$Revision: 1.2.2.2 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.BeanConnection';
UPDATE implementation set fulldescription='Implementation providing keys to the hash table
See Also:

Serialized Form
' where name='weka.DecisionTable.hashKey';
UPDATE implementation set fulldescription='Bean that assigns a class attribute to a data set.
Version:
  
$Revision: 1.9.2.1 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.ClassAssigner';
UPDATE implementation set fulldescription='The core equation for this classifier: P[Ci|D] = (P[D|Ci] x P[Ci]) / P[D] (Bayes rule) where Ci is class i and D is a document
See Also:

Serialized Form
' where name='weka.NaiveBayesMultinomial';
UPDATE implementation set fulldescription='Implementation for computing the gain ratio for a given distribution.
Version:
  
$Revision: 1.5 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.GainRatioSplitCrit';
UPDATE implementation set fulldescription='DatabaseResultProducer examines a database and extracts out the results produced by the specified ResultProducer and submits them to the specified ResultListener. If a result needs to be generated, the ResultProducer is used to obtain the result.
Version:
  
$Revision: 1.15 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.DatabaseResultProducer';
UPDATE implementation set fulldescription='An event encapsulating an instance stream event.
Version:
  
$Revision: 1.4 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.InstanceEvent';
UPDATE implementation set fulldescription='Simple class that extends the Properties class so that the properties are unable to be modified.
Version:
  
$Revision: 1.2 $

Author:
  
Richard Kirkby (rkirkby@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.ProtectedProperties';
UPDATE implementation set fulldescription='Serialzes to a destination. Valid options: -i input arff file 
 The input filw in arff format. 
 -o the output file 
 The output file. The prefix of the output file is sufficient. If no output file is given, Saver tries to use standard out. 

Version:
  
$Revision: 1.2 $

Author:
  
Stefan Mutter (mutter@cs.waikato.ac.nz)

See Also:

Saver
, 
Serialized Form
' where name='weka.SerializedInstancesSaver';
UPDATE implementation set fulldescription='A dialog to enter URL, username and password for a database connection.
Version:
  
$Revision: 1.2.2.2 $

Author:
  
Dale Fletcher (dale@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.DatabaseConnectionDialog';
UPDATE implementation set fulldescription='This is the base class for all search algorithms for learning Bayes networks. It contains some common code, used by other network structure search algorithms, and should not be used by itself.
Version:
  
$Revision: 1.4.2.1 $

Author:
  
Remco Bouckaert

See Also:

Serialized Form
' where name='weka.SearchAlgorithm';
UPDATE implementation set fulldescription='Implementation for handling a tree structure that can be pruned using C4.5 procedures.
Version:
  
$Revision: 1.11 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.C45PruneableClassifierTree';
UPDATE implementation set fulldescription='This is a very simple instance viewer - just displays the dataset as text output as it would be written to a file. A more complex viewer might be more spreadsheet-like
Version:
  
$Revision: 1.3 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.InstanceViewer';
UPDATE implementation set fulldescription='Consistency attribute subset evaluator. 
 For more information see: 
 Liu, H., and Setiono, R., (1996). A probabilistic approach to feature  selection - A filter solution. In 13th International Conference on  Machine Learning (ICML\'96), July 1996, pp. 319-327. Bari, Italy.
Version:
  
$Revision: 1.10 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.ConsistencySubsetEval';
UPDATE implementation set fulldescription='This panel allows the user to select and configure a clusterer, and evaluate the clusterer using a number of testing modes (test on the training data, train/test on a percentage split, test on a separate split). The results of clustering runs are stored in a result history so that previous results are accessible.
Version:
  
$Revision: 6604 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz), Richard Kirkby (rkirkby@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.ClustererPanel';
UPDATE implementation set fulldescription='Event encapsulating a training set
Version:
  
$Revision: 1.2 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.TrainingSetEvent';
UPDATE implementation set fulldescription='Implementation for building and using a decision stump. Usually used in conjunction with a boosting algorithm. Typical usage: 
 
java weka.classifiers.trees.LogitBoost -I 100 -W weka.classifiers.trees.DecisionStump  -t training_data 

Version:
  
$Revision: 1.18.2.1 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.DecisionStump';
UPDATE implementation set fulldescription='Implementation for computing the information gain for a given distribution.
Version:
  
$Revision: 1.6 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.InfoGainSplitCrit';
UPDATE implementation set fulldescription='Applys a mathematical expression involving attributes and numeric constants to a dataset. A new attribute is appended after the last attribute that contains the result of applying the expression.  Supported operators are: +, -, *, /, ^, log, abs, cos, exp, sqrt,  floor, ceil, rint, tan, sin, (, ). Attributes are specified by prefixing with \'a\', eg. a7 is attribute number 7 (starting from 1). 
 Valid filter-specific options are:
 -E expression 
 Specify the expression to apply. Eg. a1^2*a5/log(a7*4.0). 
 -N name 
 Specify a name for the new attribute. Default is to name it with the expression provided with the -E option. 
 -D 
 Debug. Names the attribute with the postfix parse of the expression. 

Version:
  
$Revision: 5991 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.AddExpression';
UPDATE implementation set fulldescription='Discretizes numeric attributes using equal frequency binning where the number of bins is equal to the square root of the number of non-missing values.
 Valid filter-specific options are: 
 -R col1,col2-col4,... 
 Specifies list of columns to Discretize. First and last are valid indexes. (default: first-last) 
 -V 
 Invert matching sense.
 -D 
 Make binary nominal attributes. 

Version:
  
$Revision: 1.4 $

Author:
  
Richard Kirkby (rkirkby@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.PKIDiscretize';
UPDATE implementation set fulldescription='A filter that deletes a range of attributes from the dataset. Will re-order the remaining attributes if invert matching sense is turned on and the attribute column indices are not specified in ascending order.
 Valid filter-specific options are:
 -R index1,index2-index4,...
 Specify list of columns to delete. First and last are valid indexes. (default none)
 -V
 Invert matching sense (i.e. only keep specified columns)

Version:
  
$Revision: 7110 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.Remove';
UPDATE implementation set fulldescription='Implementation encapsulating an incrementally built classifier and current instance
Since:
  
1.0

Version:
  
$Revision: 1.5.2.1 $

Author:
  
Mark Hall

See Also:

EventObject
, 
Serialized Form
' where name='weka.IncrementalClassifierEvent';
UPDATE implementation set fulldescription='Implementation for Evaluating attributes individually by using the OneR classifier. 
 -S 
 
 Set the seed for cross validation (default = 1). 
 -F 
 
 Set the number of folds for cross validation (default = 10). 
 -B 
 
 Set the minimum number of objects per bucket (passed on to OneR, default = 6). 
 -D 
 Use the training data to evaluate attributes rather than cross validation. 

Version:
  
$Revision: 1.14 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.OneRAttributeEval';
UPDATE implementation set fulldescription='Implementation implementing some statistical routines for contingency tables.
Version:
  
$Revision: 1.5 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)
' where name='weka.ContingencyTables';
UPDATE implementation set fulldescription='A bean that saves a stream of instances to a file.
Version:
  
$Revision: 1.3 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.InstanceSavePanel';
UPDATE implementation set fulldescription='Interface for objects able to listen for results obtained by a ResultProducer
Version:
  
$Revision: 1.6 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)
' where name='weka.ResultListener';
UPDATE implementation set fulldescription='Wrapper attribute subset evaluator. 
 For more information see: 
 Kohavi, R., John G., Wrappers for Feature Subset Selection.  In 
Artificial Intelligence journal
, special issue on relevance,  Vol. 97, Nos 1-2, pp.273-324. 
 Valid options are:
 -B 
 
 Class name of base learner to use for accuracy estimation. Place any classifier options last on the command line following a "--". Eg  -B weka.classifiers.bayes.NaiveBayes ... -- -K 
 -F 
 
 Number of cross validation folds to use for estimating accuracy. -T 
 
 Threshold by which to execute another cross validation (standard deviation ---expressed as a percentage of the mean). 
 -R 
 
 Seed for cross validation accuracy estimation. (default = 1) 

Version:
  
$Revision: 1.22.2.1 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.WrapperSubsetEval';
UPDATE implementation set fulldescription='Implementation for manipulating chi-square mixture distributions. 
 REFERENCES 
 Wang, Y. (2000). "A new approach to fitting linear models in high dimensional spaces." PhD Thesis. Department of Computer Science, University of Waikato, New Zealand. 
 Wang, Y. and Witten, I. H. (2002). "Modeling for optimal probability prediction." Proceedings of ICML\'2002. Sydney. 

Version:
  
$Revision: 1.1 $

Author:
  
Yong Wang (yongwang@cs.waikato.ac.nz)
' where name='weka.ChisqMixture';
UPDATE implementation set fulldescription='Implements the "Farthest First Traversal Algorithm" by  Hochbaum and Shmoys 1985: A best possible heuristic for the k-center problem, Mathematics of Operations Research, 10(2):180-184, as cited by Sanjoy Dasgupta "performance guarantees for hierarchical clustering", colt 2002, sydney works as a fast simple approximate clusterer modelled after SimpleKMeans, might be a useful initializer for it Valid options are:
 -N 
 
 Specify the number of clusters to generate. 
 -S 
 
 Specify random number seed. 

Version:
  
$Revision: 1.2 $

Author:
  
Bernhard Pfahringer (bernhard@cs.waikato.ac.nz)

See Also:

Clusterer
, 
OptionHandler
, 
Serialized Form
' where name='weka.FarthestFirst';
UPDATE implementation set fulldescription='Interface to a clusterer that can generate a requested number of clusters
Version:
  
$Revision: 1.1 $

Author:
  
Mark Hall
' where name='weka.NumberOfClustersRequestable';
UPDATE implementation set fulldescription='This class implements a propositional rule learner, Repeated Incremental Pruning to Produce Error Reduction (RIPPER), which is proposed by William W. Cohen as an optimized version of IREP. 
 The algorithm is briefly described as follows: 
 Initialize RS = {}, and for each class from the less prevalent one to  the more frequent one, DO: 
 1. Building stage: repeat 1.1 and 1.2 until the descrition length (DL)    of the ruleset and examples is 64 bits greater than the smallest DL    met so far, or there are no positive examples, or the error rate >= 50%.  1.1. Grow phase:
      Grow one rule by greedily adding antecedents (or conditions) to      the rule until the rule is perfect (i.e. 100% accurate).  The      procedure tries every possible value of each attribute and selects      the condition with highest information gain: p(log(p/t)-log(P/T)). 1.2. Prune phase:
      Incrementally prune each rule and allow the pruning of any      final sequences of the antecedents;
      The pruning metric is (p-n)/(p+n) -- but it\'s actually       2p/(p+n) -1, so in this implementation we simply use p/(p+n)      (actually (p+1)/(p+n+2), thus if p+n is 0, it\'s 0.5).
 2. Optimization stage: after generating the initial ruleset {Ri},     generate and prune two variants of each rule Ri from randomized data    using procedure 1.1 and 1.2.  But one variant is generated from an     empty rule while the other is generated by greedily adding antecedents    to the original rule.  Moreover, the pruning metric used here is     (TP+TN)/(P+N).
    Then the smallest possible DL for each variant and the original rule    is computed.  The variant with the minimal DL is selected as the final     representative of Ri in the ruleset. 
    After all the rules in {Ri} have been examined and if there are still    residual positives, more rules are generated based on the residual     positives using Building Stage again. 
 3. Delete the rules from the ruleset that would increase the DL of the    whole ruleset if it were in it. and add resultant ruleset to RS. 
 ENDDO
 Note that there seem to be 2 bugs in the ripper program that would affect the ruleset size and accuracy slightly.  This implementation avoids these bugs and thus is a little bit different from Cohen\'s original  implementation.  Even after fixing the bugs, since the order of classes with the same frequency is not defined in ripper, there still seems to be  some trivial difference between this implementation and the original ripper, especially for audiology data in UCI repository, where there are lots of classes of few instances.
 If wrapped by other classes, typical usage of this class is:
 
JRip rip = new JRip(); Instances data = ... // Data from somewhere double[] orderedClasses = ... // Get the ordered class counts for the data  double expFPRate = ... // Calculate the expected FP/(FP+FN) rate double classIndex = ...  // The class index for which ruleset is built // DL of default rule, no theory DL, only data DL double defDL = RuleStats.dataDL(expFPRate, 0.0, data.sumOfWeights(),                                   0.0, orderedClasses[(int)classIndex]); rip.rulesetForOneClass(expFPRate, data, classIndex, defDL);  RuleStats rulesetStats = rip.getRuleStats(0); // Can get heaps of information from RuleStats, e.g. combined DL,  // simpleStats, etc. double comDL = rulesetStats.combinedDL(expFPRate, classIndex); int whichRule = ... // Want simple stats of which rule? double[] simpleStats = rulesetStats.getSimpleStats(whichRule); ... Details please see "Fast Effective Rule Induction", William W. Cohen,  \'Machine Learning: Proceedings of the Twelfth International Conference\' (ML95). 
 PS.  We have compared this implementation with the original ripper  implementation in aspects of accuracy, ruleset size and running time  on both artificial data "ab+bcd+defg" and UCI datasets.  In all these aspects it seems to be quite comparable to the original ripper  implementation.  However, we didn\'t consider memory consumption optimization in this implementation.

Version:
  
$Revision: 4610 $

Author:
  
Xin Xu (xx5@cs.waikato.ac.nz), Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.JRip';
UPDATE implementation set fulldescription='Generates a single train/test split and calls the appropriate SplitEvaluator to generate some results.
Version:
  
$Revision: 1.17 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.RandomSplitResultProducer';
UPDATE implementation set fulldescription='Conditional probability estimator for a numeric domain conditional upon a discrete domain (utilises separate kernel estimators for each discrete conditioning value).
Version:
  
$Revision: 1.4 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)
' where name='weka.KDConditionalEstimator';
UPDATE implementation set fulldescription='This filter takes a dataset and removes a subset of it. Valid options are: 
 -R inst1,inst2-inst4,... 
 Specifies list of instances to select. First and last are valid indexes. (required)
 -V 
 Specifies if inverse of selection is to be output.

Version:
  
$Revision: 1.3.2.1 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.RemoveRange';
UPDATE implementation set fulldescription='GUI Customizer for the strip chart bean
Version:
  
$Revision: 1.1.2.1 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.StripChartCustomizer';
UPDATE implementation set fulldescription='
K
-nearest neighbours classifier. For more information, see 
 Aha, D., and D. Kibler (1991) "Instance-based learning algorithms", 
Machine Learning
, vol.6, pp. 37-66.
 Valid options are:
 -K num 
 Set the number of nearest neighbors to use in prediction (default 1) 
 -W num 
 Set a fixed window size for incremental train/testing. As new training instances are added, oldest instances are removed to maintain the number of training instances at this size. (default no window) 
 -I 
 Neighbors will be weighted by the inverse of their distance when voting. (default equal weighting) 
 -F 
 Neighbors will be weighted by their similarity when voting. (default equal weighting) 
 -X 
 Select the number of neighbors to use by hold-one-out cross validation, with an upper limit given by the -K option. 
 -E 
 When k is selected by cross-validation for numeric class attributes, minimize mean-squared error. (default mean absolute error) 
 -N 
 Turns off normalization. 

Version:
  
$Revision: 6575 $

Author:
  
Stuart Inglis (singlis@cs.waikato.ac.nz), Len Trigg (trigg@cs.waikato.ac.nz), Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.IBk';
UPDATE implementation set fulldescription='SimpleEstimator is used for estimating the conditional probability tables of a Bayes network once the structure has been learned. Estimates probabilities directly from data.
Version:
  
$Revision: 1.3 $

Author:
  
Remco Bouckaert (rrb@xm.co.nz)

See Also:

Serialized Form
' where name='weka.SimpleEstimator';
UPDATE implementation set fulldescription='BoundaryVisualizer. Allows the visualization of classifier decision boundaries in two dimensions. A supplied classifier is first trained on supplied training data, then a data generator (currently using kernels) is used to generate new instances at points fixed in the two visualization dimensions but random in the other dimensions. These instances are classified by the classifier and plotted as points with colour corresponding to the probability distribution predicted by the classifier. At present, 2 * 2^(# non-fixed dimensions) points are generated from each kernel per pixel in the display. In practice, fewer points than this are actually classified because kernels are weighted (on a per-pixel basis) according to the fixexd dimensions and kernels corresponding to the lowest 1% of the weight mass are discarded. Predicted probability distributions are weighted (acording to the fixed visualization dimensions) and averaged to produce an RGB value for the pixel. For more information, see
 Eibe Frank and Mark Hall (2003). Visualizing Class Probability Estimators. Working Paper 02/03, Department of Computer Science, University of Waikato.
Since:
  
1.0

Version:
  
$Revision: 1.12.2.1 $

Author:
  
Mark Hall

See Also:

JPanel
, 
Serialized Form
' where name='weka.BoundaryVisualizer';
UPDATE implementation set fulldescription='Interface to something that can be notified of a successful startup
Version:
  
$Revision: 1.1.2.2 $

Author:
  
Mark Hall
' where name='weka.StartUpListener';
UPDATE implementation set fulldescription='A sorter for TableModels. The sorter has a model (conforming to TableModel) and itself implements TableModel. TableSorter does not store or copy the data in the TableModel, instead it maintains an array of integers which it keeps the same size as the number of rows in its model. When the model changes it notifies the sorter that something has changed eg. "rowsAdded" so that its internal array of integers can be reallocated. As requests are made of the sorter (like getValueAt(row, col) it redirects them to its model via the mapping array. That way the TableSorter appears to hold another copy of the table with the rows in a different order. The sorting algorthm used is stable which means that it does not move around rows when its comparison function returns 0 to denote that they are equivalent.
 Source can be found 
here
. version 1.5 12/17/97
Version:
  
$Revision: 1.1.2.2 $

Author:
  
Philip Milne

See Also:

Serialized Form
' where name='weka.TableSorter';
UPDATE implementation set fulldescription='Interface to something that can accept remote connections and execute a task.
Version:
  
$Revision: 1.4 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)
' where name='weka.Compute';
UPDATE implementation set fulldescription='Implementation for enumerating the vector\'s elements.' where name='weka.FastVector.FastVectorEnumeration';
UPDATE implementation set fulldescription='Implementation for handling a decision list.
Version:
  
$Revision: 1.13 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.MakeDecList';
UPDATE implementation set fulldescription='Base/helper class for building logistic regression models with the LogitBoost algorithm. Used for building logistic model trees (weka.classifiers.trees.lmt.LMT) and standalone logistic regression (weka.classifiers.functions.SimpleLogistic).
Version:
  
$Revision: 1.2.2.1 $

Author:
  
Niels Landwehr

See Also:

Serialized Form
' where name='weka.LogisticBase';
UPDATE implementation set fulldescription='GraphConstants.java
Version:
  
$Revision: 1.3.2.1 $ - 24 Apr 2003 - Initial version (Ashraf M. Kibriya)

Author:
  
Ashraf M. Kibriya (amk14@cs.waikato.ac.nz)
' where name='weka.GraphConstants';
UPDATE implementation set fulldescription='Implementation implementing some distributions, tests, etc. The code is mostly adapted from the CERN Jet Java libraries: Copyright 2001 University of Waikato Copyright 1999 CERN - European Organization for Nuclear Research. Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose is hereby granted without fee, provided that the above copyright notice appear in all copies and that both that copyright notice and this permission notice appear in supporting documentation.  CERN and the University of Waikato make no representations about the suitability of this  software for any purpose. It is provided "as is" without expressed or implied warranty.
Version:
  
$Revision: 5617 $

Author:
  
peter.gedeck@pharma.Novartis.com, wolfgang.hoschek@cern.ch, Eibe Frank (eibe@cs.waikato.ac.nz), Richard Kirkby (rkirkby@cs.waikato.ac.nz)
' where name='weka.Statistics';
UPDATE implementation set fulldescription='Interface to something that has a visible (via BeanVisual) reprentation
Since:
  
1.0

Version:
  
$Revision: 1.1 $

Author:
  
Mark Hall
' where name='weka.Visible';
UPDATE implementation set fulldescription='Implementation implementing a C4.5-type split on an attribute.
Version:
  
$Revision: 1.9.2.1 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.C45Split';
UPDATE implementation set fulldescription='Interface to something that has random behaviour that is able to be seeded with an integer.
Version:
  
$Revision: 1.2 $

Author:
  
Richard Kirkby (rkirkby@cs.waikato.ac.nz)
' where name='weka.Randomizable';
UPDATE implementation set fulldescription='Implementation for wrapping a Clusterer to make it return a distribution and density. Fits normal distributions and discrete distributions within each cluster produced by the wrapped clusterer. Supports the NumberOfClustersRequestable interface only if the wrapped Clusterer does.
Version:
  
$Revision: 1.5.2.3 $

Author:
  
Richard Kirkby (rkirkby@cs.waikato.ac.nz), Mark Hall (mhall@cs.waikato.ac.nz), Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.MakeDensityBasedClusterer';
UPDATE implementation set fulldescription='A Utility class that contains summary information on an the values that appear in a dataset for a particular attribute.
Version:
  
$Revision: 1.7 $

Author:
  
Len Trigg

See Also:

Serialized Form
' where name='weka.AttributeStats';
UPDATE implementation set fulldescription='Singular Value Decomposition. For an m-by-n matrix A with m &gt;= n, the singular value decomposition is an m-by-n orthogonal matrix U, an n-by-n diagonal matrix S, and an n-by-n orthogonal matrix V so that A = U*S*V\'. The singular values, sigma[k] = S[k][k], are ordered so that sigma[0] &gt;= sigma[1] &gt;= ... &gt;= sigma[n-1]. The singular value decompostion always exists, so the constructor will never fail.  The matrix condition number and the effective numerical rank can be computed from this decomposition. Adapted from the 
JAMA
 package.
Version:
  
$Revision: 1.1.2.3 $

Author:
  
The Mathworks and NIST, Fracpete (fracpete at waikato dot ac dot nz)

See Also:

Serialized Form
' where name='weka.SingularValueDecomposition';
UPDATE implementation set fulldescription='This class prints some information about the system setup, like Java version, JVM settings etc. Useful for Bug-Reports.
Version:
  
$Revision: 1.1.2.4 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)
' where name='weka.SystemInfo';
UPDATE implementation set fulldescription='Implementation for storing a set of items. Item sets are stored in a lexicographic order, which is determined by the header information of the set of instances used for generating the set of items. All methods in this class assume that item sets are stored in lexicographic order. The class provides the general methods used for item sets in class - and  standard association rule mining.
Version:
  
$Revision: 1.10 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.ItemSet';
UPDATE implementation set fulldescription='Implementation for displaying a Node structure in Swing. 
 To work this class simply create an instance of it.
 Assign it to a window or other such object.
 Resize it to the desired size.
 When using the Displayer hold the left mouse button to drag the  tree around. 
 Click the left mouse button with ctrl to shrink the size of the tree  by half. 
 Click and drag with the left mouse button and shift to draw a box, when the left mouse button is released the contents of the box  will be magnified  to fill the screen. 
 
 Click the right mouse button to bring up a menu. 
 Most options are self explanatory.
 Select Auto Scale to set the tree to it\'s optimal display size.
Version:
  
$Revision: 1.8.2.3 $

Author:
  
Malcolm Ware (mfw4@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.TreeVisualizer';
UPDATE implementation set fulldescription='Interface for classifiers that can induce models of growing complexity one step at a time.
Version:
  
$Revision: 1.2 $

Author:
  
Gabi Schmidberger (gabi@cs.waikato.ac.nz), Bernhard Pfahringer (bernhard@cs.waikato.ac.nz)
' where name='weka.IterativeClassifier';
UPDATE implementation set fulldescription='Implementation for computing the entropy for a given distribution.
Version:
  
$Revision: 1.5 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.EntropySplitCrit';
UPDATE implementation set fulldescription='A PropertyEditor for File objects that lets the user select a file.
Version:
  
$Revision: 1.7 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)
' where name='weka.FileEditor';
UPDATE implementation set fulldescription='Implementation to represent a test.
 The string representation of the test can be supplied in standard notation or for a subset of types of attributes  in Prolog notation.
 Following examples for all possible tests that can be represented by this class, given in standard notation.
 Examples of tests for numeric attributes:
 B >= 2.333
        B 
 Examples of tests for nominal attributes with more then 2 values:
 A = rain 
            A != rain
 Examples of tests for nominal attribute with exactly 2 values:
 A = false 
            A = true
 The Prolog notation is only supplied for numeric attributes and for nominal attributes that have the values "true" and "false".
 Following examples for the Prolog notation provided.
 Examples of tests for numeric attributes:
 The same as for standard notation above.
 Examples of tests for nominal attributes with values "true"and "false":
 A
 not(A)
 (Other nominal attributes are not supported by the Prolog notation.)

Version:
  
$Revision: 1.1 $

Author:
  
Gabi Schmidberger (gabi@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.Test';
UPDATE implementation set fulldescription='Indicator interface to something that can store instances to some destination
Version:
  
$Revision: 1.1 $

Author:
  
Mark Hall
' where name='weka.DataSink';
UPDATE implementation set fulldescription='The normalized polynomial kernel.  K(x,y) = 
/sqrt(
) where 
 = PolyKernel(x,y)
Version:
  
$Revision: 1.1.2.2 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.NormalizedPolyKernel';
UPDATE implementation set fulldescription='Implementation for building and using a simple decision table majority classifier. For more information see: 
 Kohavi R. (1995).
 The Power of Decision Tables.
 In Proc European Conference on Machine Learning.
 Valid options are: 
 -S num 
 Number of fully expanded non improving subsets to consider before terminating a best first search. (Default = 5) 
 -X num 
 Use cross validation to evaluate features. Use number of folds = 1 for leave one out CV. (Default = leave one out CV) 
 -I 
 Use nearest neighbour instead of global table majority. 
 -R 
 Prints the decision table. 

Version:
  
$Revision: 1.29.2.1 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.DecisionTable';
UPDATE implementation set fulldescription='Implementation for handling a distribution of class values.
Version:
  
$Revision: 1.8.2.1 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.Distribution';
UPDATE implementation set fulldescription='compares two strings with the following order:
    
case insensitive
    
german umlauts (&auml; , &ouml; etc.) or other non-ASCII letters are treated as special chars
    
special chars &lt; numbers &lt; letters
' where name='weka.RTSI.StringCompare';
UPDATE implementation set fulldescription='Implementation for handling an attribute. Once an attribute has been created, it can\'t be changed. 
 The following attribute types are supported:    
 numeric: 
         This type of attribute represents a floating-point number.    
 nominal: 
         This type of attribute represents a fixed set of nominal values.    
 string: 
         This type of attribute represents a dynamically expanding set of         nominal values. Usually used in text classification.    
 date: 
         This type of attribute represents a date, internally represented as         floating-point number storing the milliseconds since January 1,         1970, 00:00:00 GMT. The string representation of the date must be         ISO-8601
 compliant, the default is 
yyyy-MM-dd\'T\'HH:mm:ss
. Typical usage (code from the main() method of this class): 
 ... 
 // Create numeric attributes "length" and "weight" 
 Attribute length = new Attribute("length"); 
 Attribute weight = new Attribute("weight"); 
 // Create vector to hold nominal values "first", "second", "third" 
 FastVector my_nominal_values = new FastVector(3); 
 my_nominal_values.addElement("first"); 
 my_nominal_values.addElement("second"); 
 my_nominal_values.addElement("third"); 
 // Create nominal attribute "position" 
 Attribute position = new Attribute("position", my_nominal_values);
 ... 

Version:
  
$Revision: 6995 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.Attribute';
UPDATE implementation set fulldescription='Abstract kernel.  Kernels implementing this class must respect Mercer\'s condition in order  to ensure a correct behaviour of SMOreg.
Version:
  
$Revision: 1.1.2.2 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.Kernel';
UPDATE implementation set fulldescription='Implementation implementing a NBTree split on an attribute.
Version:
  
$Revision: 1.2 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.NBTreeSplit';
UPDATE implementation set fulldescription='Conditional probability estimator for a discrete domain conditional upon a numeric domain.
Version:
  
$Revision: 1.4 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)
' where name='weka.DNConditionalEstimator';
UPDATE implementation set fulldescription='Implementation encapsulating a built classifier and a batch of instances to test on.
Since:
  
1.0

Version:
  
$Revision: 1.3.2.1 $

Author:
  
Mark Hall

See Also:

EventObject
, 
Serialized Form
' where name='weka.BatchClassifierEvent';
UPDATE implementation set fulldescription='Support for PropertyEditors with custom editors: puts the editor into a separate frame.
Version:
  
$Revision: 6594 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.PropertyDialog';
UPDATE implementation set fulldescription='Interface to something that can accept ThresholdDataEvents
Since:
  
1.0

Version:
  
$Revision: 1.1 $

Author:
  
Mark Hall

See Also:

EventListener
' where name='weka.ThresholdDataListener';
UPDATE implementation set fulldescription='Stores some statistics.
Version:
  
$Revision: 1.5 $

Author:
  
Yong Wang (yongwang@cs.waikato.ac.nz)
' where name='weka.Values';
UPDATE implementation set fulldescription='This class is a helper class for XML serialization using 
KOML
 . KOML does not need to be present, since the class-calls are done generically via Reflection.
Version:
  
$Revision 1.0$

Author:
  
FracPete (fracpete at waikato dot ac dot nz)
' where name='weka.KOML';
UPDATE implementation set fulldescription='Interface for filters that make use of a class attribute.
Version:
  
$Revision: 1.1 $

Author:
  
Richard Kirkby (rkirkby@cs.waikato.ac.nz)
' where name='weka.SupervisedFilter';
UPDATE implementation set fulldescription='A filter that uses a clusterer to obtain cluster membership values for each input instance and outputs them as new instances. The clusterer needs to be a density-based clusterer. If a (nominal) class is set, then the clusterer will be run individually for each class.
 Valid filter-specific options are: 
 Full class name of clusterer to use. Clusterer options may be specified at the end following a -- .(required)
 -I range string 
 The range of attributes the clusterer should ignore. Note:  the class attribute (if set) is automatically ignored during clustering.

Version:
  
$Revision: 1.7 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz), Eibe Frank

See Also:

Serialized Form
' where name='weka.ClusterMembership';
UPDATE implementation set fulldescription='This class maintains a list that contains all the colornames from the  dotty standard and what color (in RGB) they represent
Version:
  
$Revision: 1.4 $

Author:
  
Malcolm Ware (mfw4@cs.waikato.ac.nz)
' where name='weka.Colors';
UPDATE implementation set fulldescription='
Version:
  
$Revision: 1.3.2.1 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.ClassValuePickerCustomizer';
UPDATE implementation set fulldescription='Interface for classes that need to draw to the Plot2D panel *before* Plot2D renders anything (eg. VisualizePanel may need to draw polygons etc.)
Version:
  
$Revision: 1.4 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)
' where name='weka.Plot2DCompanion';
UPDATE implementation set fulldescription='Produces a random subsample of a dataset using sampling with replacement. The original dataset must fit entirely in memory. The number of instances in the generated dataset may be specified. When used in batch mode, subsequent batches are 
not
 resampled. Valid options are:
 -S num 
 Specify the random number seed (default 1).
 -Z percent 
 Specify the size of the output dataset, as a percentage of the input dataset (default 100). 

Version:
  
$Revision: 1.4.2.1 $

Author:
  
Len Trigg (len@reeltwo.com)

See Also:

Serialized Form
' where name='weka.Resample';
UPDATE implementation set fulldescription='AveragingResultProducer takes the results from a ResultProducer and submits the average to the result listener. For non-numeric result fields, the first value is used.
Version:
  
$Revision: 1.15 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.AveragingResultProducer';
UPDATE implementation set fulldescription='BayesNetGenerator offers facilities for generating random Bayes networks and random instances based on a Bayes network.
Version:
  
$Revision: 1.4.2.3 $

Author:
  
Remco Bouckaert (rrb@xm.co.nz)

See Also:

Serialized Form
' where name='weka.BayesNetGenerator';
UPDATE implementation set fulldescription='This class will parse a dotty file and construct a tree structure from it  with Edge\'s and Node\'s
Version:
  
$Revision: 1.3 $

Author:
  
Malcolm Ware (mfw4@cs.waikato.ac.nz)
' where name='weka.TreeBuild';
UPDATE implementation set fulldescription='Bean that evaluates incremental classifiers
Version:
  
$Revision: 1.9 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.IncrementalClassifierEvaluator';
UPDATE implementation set fulldescription='Interface to something that can be remotely executed as a task.
Version:
  
$Revision: 1.7 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)
' where name='weka.Task';
UPDATE implementation set fulldescription='Bean that wraps around weka.clusterers
Version:
  
$Revision: 1.1.2.2 $

Author:
  
Stefan Mutter

See Also:

JPanel
, 
BeanCommon
, 
Visible
, 
WekaWrapper
, 
Serializable
, 
UserRequestAcceptor
, 
TrainingSetListener
, 
TestSetListener
, 
Serialized Form
' where name='weka.Clusterer';
UPDATE implementation set fulldescription='Interface to something that can accept and process training set events
Version:
  
$Revision: 1.2.2.1 $

Author:
  
Mark Hall
' where name='weka.TrainingSetListener';
UPDATE implementation set fulldescription='
See Also:

Serialized Form
' where name='weka.SimpleLinkedList.LinkedListIterator';
UPDATE implementation set fulldescription='This class handles the saving of StringBuffers to files. It will pop up a file chooser allowing the user to select a destination file. If the file exists, the user is prompted for the correct course of action, ie. overwriting, appending, selecting a new filename or canceling.
Version:
  
$Revision 1.0 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)
' where name='weka.SaveBuffer';
UPDATE implementation set fulldescription='Bean that accepts data sets and produces test sets
Version:
  
$Revision: 1.3 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.TestSetMaker';
UPDATE implementation set fulldescription='Interface to something that provides a short textual summary (as opposed to toString() which is usually a fairly complete description) of itself.
Version:
  
$Revision: 1.5 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)
' where name='weka.Summarizable';
UPDATE implementation set fulldescription='Abstract class representing a splitter node in an alternating tree.
Version:
  
$Revision: 1.2 $

Author:
  
Richard Kirkby (rkirkby@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.Splitter';
UPDATE implementation set fulldescription='"Abstract" class for computing splitting criteria based on the entropy of a class distribution.
Version:
  
$Revision: 1.5 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.EntropyBasedSplitCrit';
UPDATE implementation set fulldescription='Event encapsulating a data set
Version:
  
$Revision: 1.2 $

Author:
  
Mark Hall

See Also:

EventObject
, 
Serialized Form
' where name='weka.DataSetEvent';
UPDATE implementation set fulldescription='Event encapsulating error information for a learning scheme that can be visualized in the DataVisualizer
Version:
  
$Revision: 1.1.2.2 $

Author:
  
Mark Hall

See Also:

EventObject
, 
Serialized Form
' where name='weka.VisualizableErrorEvent';
UPDATE implementation set fulldescription='Interface for objects that display log (permanent historical) and status (transient) messages.
Version:
  
$Revision: 1.4 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)
' where name='weka.Logger';
UPDATE implementation set fulldescription='This is the Exception thrown by BIFParser, if there was an error in parsing the XMLBIF string or input stream.
Version:
  
$Revision: 1.2.2.1 $ - 24 Apr 2003 - Initial version (Ashraf M. Kibriya)

Author:
  
Ashraf M. Kibriya (amk14@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.BIFFormatException';
UPDATE implementation set fulldescription='GUI customizer for the Clusterer wrapper bean
Version:
  
$Revision: 1.1.2.1 $

Author:
  
Stefan Mutter

See Also:

Serialized Form
' where name='weka.ClustererCustomizer';
UPDATE implementation set fulldescription='Abstract class for Saver
Version:
  
$Revision: 1.2 $

Author:
  
Richard Kirkby (rkirkby@cs.waikato.ac.nz), Stefan Mutter (mutter@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.AbstractSaver';
UPDATE implementation set fulldescription='Implementation for handling a partial tree structure that can be pruned using a pruning set.
Version:
  
$Revision: 1.6 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.PruneableDecList';
UPDATE implementation set fulldescription='KDDataGenerator. Class that uses kernels to generate new random instances based on a supplied set of instances.
Since:
  
1.0

Version:
  
$Revision: 1.4 $

Author:
  
Mark Hall

See Also:

DataGenerator
, 
Serializable
, 
Serialized Form
' where name='weka.KDDataGenerator';
UPDATE implementation set fulldescription='A Classifier that uses backpropagation to classify instances. This network can be built by hand, created by an algorithm or both. The network can also be monitored and modified during training time. The nodes in this network are all sigmoid (except for when the class is numeric in which case the the output nodes become unthresholded linear units).
Version:
  
$Revision: 7071 $

Author:
  
Malcolm Ware (mfw4@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.MultilayerPerceptron';
UPDATE implementation set fulldescription='Replaces all missing values for nominal and numeric attributes in a  dataset with the modes and means from the training data.
Version:
  
$Revision: 1.4.2.1 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.ReplaceMissingValues';
UPDATE implementation set fulldescription='Handles the GUI side of editing values.
See Also:

Serialized Form
' where name='weka.GenericObjectEditor.GOEPanel';
UPDATE implementation set fulldescription='Implementation implementing a Tertius-type algorithm. 
 References: P. A. Flach, N. Lachiche (1999). 
Confirmation-Guided  Discovery of first-order rules with Tertius
.  Machine Learning, 42, 61-95. 
 Valid options are:
 -K number of values in result 
 Set maximum number of confirmation  values in the result. (default: 10) 
 -F frequency threshold 
 Set frequency threshold for pruning. (default: 0) 
 -C confirmation threshold 
 Set confirmation threshold. (default: 0) 
 -N noise threshold 
 Set noise threshold : maximum frequency of counter-examples. 0 gives only satisfied rules. (default: 1) 
 -R 
 Allow attributes to be repeated in a same rule. 
 -L number of literals 
 Set maximum number of literals in a rule. (default: 4) 
 -G 0=no negation | 1=body | 2=head | 3=body and head 
 Set the negations in the rule. (default: 0) 
 -S 
 Consider only classification rules. 
 -c class index 
 Set index of class attribute. (default: last). 
 -H 
 Consider only horn clauses. 
 -E 
 Keep equivalent rules. 
 -M 
 Keep same clauses. 
 -T 
 Keep subsumed rules. 
 -I 0=always match | 1=never match | 2=significant 
 Set the way to handle missing values. (default: 0) 
 -O 
 Use ROC analysis. 
 -p name of file 
 Set the file containing the parts of the individual for individual-based  learning. 
 -P 0=no output | 1=on stdout | 2=in separate window 
 Set output of current values. (default: 0) 

Version:
  
$Revision: 1.3.2.1 $

Author:
  
Amelie Deltour

See Also:

Serialized Form
' where name='weka.Tertius';
UPDATE implementation set fulldescription='Interface to something that can accept requests from a user to perform some action
Since:
  
1.0

Version:
  
$Revision: 1.2 $

Author:
  
Mark Hall
' where name='weka.UserRequestAcceptor';
UPDATE implementation set fulldescription='An interface for objects that are capable of supplying their own custom GUI components. The original purpose for this interface is to provide a mechanism allowing the GenericObjectEditor to override the standard PropertyPanel GUI.
Version:
  
$Revision: 1.2 $

Author:
  
Richard Kirkby (rkirkby@cs.waikato.ac.nz)
' where name='weka.CustomPanelSupplier';
UPDATE implementation set fulldescription='This class implements a single conjunctive rule learner that can predict for numeric and nominal class labels.
   A rule consists of antecedents "AND"ed together and the consequent (class value)  for the classification/regression.  In this case, the consequent is the  distribution of the available classes (or numeric value) in the dataset.   If the test instance is not covered by this rule, then it\'s predicted using the default class distributions/value of the data not covered by the rule in the training data. 
 This learner selects an antecedent by computing the Information Gain of each  antecendent and prunes the generated rule using Reduced Error Prunning (REP). 
 For classification, the Information of one antecedent is the weighted average of the entropies of both the data covered and not covered by the rule. 
 For regression, the Information is the weighted average of the mean-squared errors  of both the data covered and not covered by the rule. 
 In pruning, weighted average of accuracy rate of the pruning data is used  for classification while the weighted average of the mean-squared errors of the pruning data is used for regression. 

Version:
  
$Revision: 1.10 $

See Also:

Serialized Form
' where name='weka.ConjunctiveRule';
UPDATE implementation set fulldescription='Marker interface for a loader/saver that can retrieve instances in batch mode
Version:
  
$Revision 1.0 $

Author:
  
Mark Hall
' where name='weka.BatchConverter';
UPDATE implementation set fulldescription='A custom class which provides the environment for computing the transformation probability of a specified test instance numeric attribute to a specified train instance numeric attribute.
Version:
  
$Revision 1.0 $

Author:
  
Len Trigg (len@reeltwo.com), Abdelaziz Mahoui (am14@cs.waikato.ac.nz)
' where name='weka.KStarNumericAttribute';
UPDATE implementation set fulldescription='Implementation representing a prediction node in an alternating tree.
Version:
  
$Revision: 1.4 $

Author:
  
Richard Kirkby (rkirkby@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.PredictionNode';
UPDATE implementation set fulldescription='Implementation implementing the Cobweb and Classit clustering algorithms.
 Note: the application of node operators (merging, splitting etc.) in terms of ordering and priority differs (and is somewhat ambiguous) between the original Cobweb and Classit papers. This algorithm always compares the best host, adding a new leaf, merging the two best hosts, and splitting the best host when considering where to place a new instance.
 Valid options are:
 -A 
 
 Acuity. 
 -C 
 
 Cutoff. 

Version:
  
$Revision: 6609 $

Author:
  
Mark Hall

See Also:

Clusterer
, 
OptionHandler
, 
Drawable
, 
Serialized Form
' where name='weka.Cobweb';
UPDATE implementation set fulldescription='Implementation encapsulating information on progress of a remote experiment
Version:
  
$Revision: 1.4 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.RemoteExperimentEvent';
UPDATE implementation set fulldescription='Interface to something that can produce a training set
Version:
  
$Revision: 1.2 $

Author:
  
Mark Hall
' where name='weka.TrainingSetProducer';
UPDATE implementation set fulldescription='Converts all nominal attributes into binary numeric attributes. An attribute with k values is transformed into k binary attributes (using the one-attribute-per-value approach). Binary attributes are left binary. Valid filter-specific options are: 
 -N 
 If binary attributes are to be coded as nominal ones.
 -R col1,col2-col4,... 
 Specifies list of columns to convert. First and last are valid indexes. (default: first-last) 
 -V 
 Invert matching sense.

Version:
  
$Revision: 1.6 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.NominalToBinary';
UPDATE implementation set fulldescription='AODE achieves highly accurate classification by averaging over all of a small space of alternative naive-Bayes-like models that have weaker (and hence less detrimental) independence assumptions than naive Bayes. The resulting algorithm is computationally efficient while delivering highly accurate classification on many learning tasks.
 For more information, see
 G. Webb, J. Boughton & Z. Wang (2004). 
Not So Naive Bayes.
 To be published in Machine Learning.
 G. Webb, J. Boughton & Z. Wang (2002). 
Averaged One-Dependence Estimators: Preliminary Results.
 AI2002 Data Mining Workshop, Canberra. Valid options are:
 -D 
 Debugging information is printed if this flag is specified.
 -F 
 Specify the frequency limit for parent attributes.

Version:
  
$Revision: 1.8.2.4 $  this version resolves errors in the handling of missing attribute values.

Author:
  
Janice Boughton (jrbought@csse.monash.edu.au) & Zhihai Wang (zhw@csse.monash.edu.au)

See Also:

Serialized Form
' where name='weka.AODE';
UPDATE implementation set fulldescription='Implementation for storing a binary-data-only instance as a sparse vector. A sparse instance only requires storage for those attribute values that are non-zero.  Since the objective is to reduce storage requirements for datasets with large numbers of default values, this also includes nominal attributes -- the first nominal value (i.e. that which has index 0) will not require explicit storage, so rearrange your nominal attribute value orderings if necessary. Missing values are not supported, and will be treated as  1 (true).
Version:
  
$Revision: 1.7.2.2 $

See Also:

Serialized Form
' where name='weka.BinarySparseInstance';
UPDATE implementation set fulldescription='Filter for doing attribute selection.
 Valid options are:
 -S 
 
 Set search method for subset evaluators. 
 eg. -S "weka.attributeSelection.BestFirst -S 8" 
 -E 
 
 Set the attribute/subset evaluator. 
 eg. -E "weka.attributeSelection.CfsSubsetEval -L" 

Version:
  
$Revision: 1.3 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.AttributeSelection';
UPDATE implementation set fulldescription='Implementation for evaluating clustering models.
 Valid options are: 
 -t 
 
 Specify the training file. 
 -T 
 
 Specify the test file to apply clusterer to. 
 -d 
 
 Specify output file. 
 -l 
 
 Specifiy input file. 
 -p 
 
 Output predictions. Predictions are for the training file if only the training file is specified, otherwise they are for the test file. The range specifies attribute values to be output with the predictions. Use \'-p 0\' for none. 
 -x 
 
 Set the number of folds for a cross validation of the training data. Cross validation can only be done for distribution clusterers and will be performed if the test file is missing. 
 -c 
 
 Set the class attribute. If set, then class based evaluation of clustering is performed. 

Version:
  
$Revision: 1.27.2.2 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.ClusterEvaluation';
UPDATE implementation set fulldescription='Implementation for performing a random search. 
 Valid options are: 
 -P 
 
 Specify a starting set of attributes. Eg 1,4,7-9. 
 -F 
 Percentage of the search space to consider. (default = 25). 
 -V 
 Verbose output. Output new best subsets as the search progresses. 

Version:
  
$Revision: 1.12 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.RandomSearch';
UPDATE implementation set fulldescription='A simple instance filter that passes all instances directly through. Basically just for testing purposes.
Version:
  
$Revision: 1.8 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.AllFilter';
UPDATE implementation set fulldescription='Extension of KeyAdapter that implements Serializable.
See Also:

Serialized Form
' where name='weka.ResultHistoryPanel.RKeyAdapter';
UPDATE implementation set fulldescription='
Version:
  
$Revision: 1.3.2.1 $

Author:
  
Peter A. Flach, Nicolas Lachiche

See Also:

Serialized Form
' where name='weka.AttributeValueLiteral';
UPDATE implementation set fulldescription='QR Decomposition. For an m-by-n matrix A with m &gt;= n, the QR decomposition is an m-by-n orthogonal matrix Q and an n-by-n upper triangular matrix R so that A = Q*R. The QR decompostion always exists, even if the matrix does not have full rank, so the constructor will never fail.  The primary use of the QR decomposition is in the least squares solution of nonsquare systems of simultaneous linear equations.  This will fail if isFullRank() returns false. Adapted from the 
JAMA
 package.
Version:
  
$Revision: 1.1.2.2 $

Author:
  
The Mathworks and NIST, Fracpete (fracpete at waikato dot ac dot nz)

See Also:

Serialized Form
' where name='weka.QRDecomposition';
UPDATE implementation set fulldescription='A filter that converts all incoming instances into sparse format.
Version:
  
$Revision: 1.1 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.NonSparseToSparse';
UPDATE implementation set fulldescription='This class handles relationships between display names of properties  (or classes) and Methods that are associated with them.
Version:
  
$Revision: 1.1.2.2 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)
' where name='weka.MethodHandler';
UPDATE implementation set fulldescription='
See Also:

Serialized Form
' where name='weka.AlgorithmListPanel.ObjectCellRenderer';
UPDATE implementation set fulldescription='Helper class for Bayes Network classifiers. Provides datastructures to represent a set of parents in a graph.
Version:
  
$Revision: 1.4 $

Author:
  
Remco Bouckaert (rrb@xm.co.nz)

See Also:

Serialized Form
' where name='weka.ParentSet';
UPDATE implementation set fulldescription='Introduces noise data  a random subsample of the dataset  by changing a given attribute (attribute must be nominal) Valid options are:
 -C col 
 Index of the attribute to be changed. (default last)
 -M 
 flag: missing values are treated as an extra value 
 -P num 
 Percentage of noise to be introduced to the data (default 10).
 -S seed 
 Random number seed for choosing the data to be changed 
 and for choosing the value it is changed to (default 1). 

Version:
  
$Revision: 1.2.2.2 $

Author:
  
Gabi Schmidberger (gabi@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.AddNoise';
UPDATE implementation set fulldescription='Interface for filters that do not need a class attribute.
Version:
  
$Revision: 1.1 $

Author:
  
Richard Kirkby (rkirkby@cs.waikato.ac.nz)
' where name='weka.UnsupervisedFilter';
UPDATE implementation set fulldescription='Implementation for building pace regression linear models and using them for prediction. 
 Under regularity conditions, pace regression is provably optimal when the number of coefficients tends to infinity. It consists of a group of estimators that are either overall optimal or optimal under certain conditions. 
 The current work of the pace regression theory, and therefore also this implementation, do not handle: 
 - missing values 
 - non-binary nominal attributes 
 - the case that n - k is small where n is number of instances and k is     number of coefficients (the threshold used in this implmentation is 20)  Valid options are:
 -D 
 Produce debugging output. 
 -E estimator 
 The estimator can be one of the following: 
 
eb -- Empirical Bayes estimator for noraml mixture (default) 
 
nested -- Optimal nested model selector for normal mixture 
 
subset -- Optimal subset selector for normal mixture 
 
pace2 -- PACE2 for Chi-square mixture 
 
pace4 -- PACE4 for Chi-square mixture
 
pace6 -- PACE6 for Chi-square mixture 
 
ols -- Ordinary least squares estimator 
 
aic -- AIC estimator 
 
bic -- BIC estimator 
 
ric -- RIC estimator 
 
olsc -- Ordinary least squares subset selector with a threshold 
 -S 
 Threshold for the olsc estimator
 REFERENCES 
 Wang, Y. (2000). "A new approach to fitting linear models in high dimensional spaces." PhD Thesis. Department of Computer Science, University of Waikato, New Zealand. 
 Wang, Y. and Witten, I. H. (2002). "Modeling for optimal probability prediction." Proceedings of ICML\'2002. Sydney. 

Version:
  
$Revision: 1.1.2.1 $

Author:
  
Yong Wang (yongwang@cs.waikato.ac.nz), Gabi Schmidberger (gabi@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.PaceRegression';
UPDATE implementation set fulldescription='Implementation for handling a linked list. Used in best first search. Extends the Vector class.
See Also:

Serialized Form
' where name='weka.DecisionTable.LinkedList';
UPDATE implementation set fulldescription='
See Also:

Serialized Form
' where name='weka.SimpleLinkedList.LinkedListInverseIterator';
UPDATE implementation set fulldescription='Symbolic probability estimator based on symbol counts and a prior.
Version:
  
$Revision: 1.1 $

Author:
  
Remco Bouckaert (rrb@xm.co.nz)

See Also:

Serialized Form
' where name='weka.DiscreteEstimatorFullBayes';
UPDATE implementation set fulldescription='Implementation implementing some mathematical functions.
Version:
  
$Revision: 1.6 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)
' where name='weka.SpecialFunctions';
UPDATE implementation set fulldescription='Implementation for generating a PART decision list. For more information, see
 Eibe Frank and Ian H. Witten (1998).  <a href="http://www.cs.waikato.ac.nz/~eibe/pubs/ML98-57.ps.gz">Generating Accurate Rule Sets Without Global Optimization.
 In Shavlik, J., ed., 
Machine Learning: Proceedings of the Fifteenth International Conference
, Morgan Kaufmann Publishers, San Francisco, CA. 
 Valid options are: 
 -C confidence 
 Set confidence threshold for pruning. (Default: 0.25) 
 -M number 
 Set minimum number of instances per leaf. (Default: 2) 
 -R 
 Use reduced error pruning. 
 -N number 
 Set number of folds for reduced error pruning. One fold is used as the pruning set. (Default: 3) 
 -B 
 Use binary splits for nominal attributes. 
 -U 
 Generate unpruned decision list. 
 -Q 
 The seed for reduced-error pruning. 

Version:
  
$Revision: 1.2.2.1 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.PART';
UPDATE implementation set fulldescription='This filter takes a dataset and outputs folds suitable for cross validation. If you do not want the folds to be stratified then use the unsupervised  version. Valid options are: 
 -V 
 Specifies if inverse of selection is to be output.
 -N number of folds 
 Specifies number of folds dataset is split into (default 10). 
 -F fold 
 Specifies which fold is selected. (default 1)
 -S seed 
 Specifies a random number seed for shuffling the dataset. (default 0, don\'t randomize)

Version:
  
$Revision: 1.1.2.1 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.StratifiedRemoveFolds';
UPDATE implementation set fulldescription='SimulatedAnnealing uses simulated annealing for learning Bayesian network structures. For details, see for example  R.R. Bouckaert.  Bayesian Belief Networks: from Construction to Inference.  Ph.D. thesis,  University of Utrecht,  1995
Author:
  
Remco Bouckaert (rrb@xm.co.nz) Version: $Revision: 1.2 $

See Also:

Serialized Form
' where name='weka.SimulatedAnnealing';
UPDATE implementation set fulldescription='Eigenvalues and eigenvectors of a real matrix.  If A is symmetric, then A = V*D*V\' where the eigenvalue matrix D is diagonal and the eigenvector matrix V is orthogonal.  I.e. A = V.times(D.times(V.transpose())) and V.times(V.transpose()) equals the identity matrix. If A is not symmetric, then the eigenvalue matrix D is block diagonal with the real eigenvalues in 1-by-1 blocks and any complex eigenvalues, lambda + i*mu, in 2-by-2 blocks, [lambda, mu; -mu, lambda].  The columns of V represent the eigenvectors in the sense that A*V = V*D, i.e. A.times(V) equals V.times(D).  The matrix V may be badly conditioned, or even singular, so the validity of the equation A = V*D*inverse(V) depends upon V.cond(). Adapted from the 
JAMA
 package.
Version:
  
$Revision: 1.1.2.2 $

Author:
  
The Mathworks and NIST, Fracpete (fracpete at waikato dot ac dot nz)

See Also:

Serialized Form
' where name='weka.EigenvalueDecomposition';
UPDATE implementation set fulldescription='Interface to incremental classification models that can learn using one instance at a time.
Version:
  
$Revision: 1.4 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)
' where name='weka.UpdateableClassifier';
UPDATE implementation set fulldescription='A little helper class for Memory management. Very crude, since JDK 1.4  doesn\'t offer real Memory Management.
 The memory management can be disabled by using the setEnabled(boolean) method.
Version:
  
$Revision: 1.1.2.4 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)

See Also:

setEnabled(boolean)

' where name='weka.Memory';
UPDATE implementation set fulldescription='Interface to a loader/saver that loads/saves from a file source.
Version:
  
$Revision: 1.2 $

Author:
  
Mark Hall
' where name='weka.FileSourcedConverter';
UPDATE implementation set fulldescription='An instance filter that adds a new attribute to the dataset.  The new attribute contains all missing values.
 Valid filter-specific options are:
 -C index 
 Specify where to insert the column. First and last are valid indexes. (default last)
 -L label1,label2,...
 Create nominal attribute with the given labels (default numeric attribute)
 -N name
 Name of the new attribute. (default = \'Unnamed\')

Version:
  
$Revision: 5157 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.Add';
UPDATE implementation set fulldescription='Conditional probability estimator for a discrete domain conditional upon a numeric domain.
Version:
  
$Revision: 1.4 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)
' where name='weka.DKConditionalEstimator';
UPDATE implementation set fulldescription='This class parses an inputstream or a string in XMLBIF ver. 0.3 format, and builds the datastructures that are passed to it through the constructor.
Version:
  
$Revision: 1.4.2.1 $ - 24 Apr 2003 - Initial version (Ashraf M. Kibriya)

Author:
  
Ashraf M. Kibriya (amk14@cs.waikato.ac.nz)
' where name='weka.BIFParser';
UPDATE implementation set fulldescription='This panel displays legends for a list of plots. If a given plot has a custom colour defined then this panel allows the colour to be changed.
Version:
  
$Revision: 1.5 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.LegendPanel';
UPDATE implementation set fulldescription='Conditional probability estimator for a numeric domain conditional upon a numeric domain (using Mahalanobis distance).
Version:
  
$Revision: 1.4 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)
' where name='weka.NNConditionalEstimator';
UPDATE implementation set fulldescription='The model for the Arff-Viewer.
Version:
  
$Revision: 1.1.2.2 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)
' where name='weka.ArffTableModel';
UPDATE implementation set fulldescription='This class implements the statistics functions used in the  propositional rule learner, from the simpler ones like count of true/false positive/negatives, filter data based on the ruleset, etc. to the more sophisticated ones such as MDL calculation and rule variants generation for each rule in the ruleset. 
 Obviously the statistics functions listed above need the specific data and the specific ruleset, which are given in order to instantiate an object of this class. 

Version:
  
$Revision: 4610 $

Author:
  
Xin Xu (xx5@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.RuleStats';
UPDATE implementation set fulldescription='This class contains the version number of the current WEKA release and some methods for comparing another version string. The normal layout of a version string is "MAJOR.MINOR.REVISION", but it can also handle partial version strings, e.g. "3.4".
 Should be used e.g. in exports to XML for keeping track, with which version  of WEKA the file was produced.
Version:
  
$Revision: 1.1.2.3 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)
' where name='weka.Version';
UPDATE implementation set fulldescription='Abstract base class for TrainAndTestSetProducers that contains default implementations of add/remove listener methods and defualt visual representation.
Version:
  
$Revision: 1.2.2.1 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.AbstractTrainAndTestSetProducer';
UPDATE implementation set fulldescription='Implementation for performing an exhaustive search. 
 Valid options are: 
 -V 
 Verbose output. Output new best subsets as the search progresses. 

Version:
  
$Revision: 1.8.2.2 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.ExhaustiveSearch';
UPDATE implementation set fulldescription='
Version:
  
$Revision: 1.2.2.1 $

Author:
  
Yong Wang

See Also:

Serialized Form
' where name='weka.ExponentialFormat';
UPDATE implementation set fulldescription='BMAEstimator estimates conditional probability tables of a Bayes network using Bayes Model Averaging (BMA).
Version:
  
$Revision: 1.4.2.1 $

Author:
  
Remco Bouckaert (rrb@xm.co.nz)

See Also:

Serialized Form
' where name='weka.BMAEstimator';
UPDATE implementation set fulldescription='A class holding information for tasks being executed on RemoteEngines. Also holds an object encapsulating any returnable result produced by the task (Note: result object must be serializable). Task objects execute methods return instances of this class. RemoteEngines also use this class for storing progress information for tasks that they execute.
Version:
  
$Revision: 1.2 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.TaskStatusInfo';
UPDATE implementation set fulldescription='This class displays the graph we want to visualize. It should be sufficient to use only this class in weka.gui.graphvisulizer package to visualize a graph. The description of a graph should be provided as a string argument using readBIF or readDOT method in either XMLBIF03 or DOT format. Alternatively, an InputStream in XMLBIF03 can also be provided to another variation of readBIF. It would be necessary in case input is in DOT format to call the layoutGraph() method to display the graph correctly after the call to readDOT. It is also necessary to do so if readBIF is called and the graph description doesn\'t have x y positions for nodes. 
 The graph\'s data is held in two FastVectors, nodes are stored as objects of GraphNode class and edges as objects of GraphEdge class. 
 The graph is displayed by positioning and drawing each node according to its x y position and then drawing all the edges coming out of it give by its edges[][] array, the arrow heads are ofcourse marked in the opposite(ie original direction) or both directions if the edge is reversed or is in both directions. The graph is centered if it is smaller than it\'s display area. The edges are drawn from the bottom of the current node to the top of the node given by edges[][] array in GraphNode class, to avoid edges crossing over other nodes. This might need to be changed if another layout engine is added or the current Hierarchical engine is updated to avoid such crossings over nodes.
Version:
  
$Revision: 1.3.2.2 $

Author:
  
Ashraf M. Kibriya (amk14@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.GraphVisualizer';
UPDATE implementation set fulldescription='Interface for conditional probability estimators. Example code: 
   NNConditionalEstimator newEst = new NNConditionalEstimator();   // Create 50 random points and add them   Random r = new Random(seed);   for(int i = 0; i < 50; i++) {     int x = Math.abs(r.nextInt() % 100);     int y = Math.abs(r.nextInt() % 100);     System.out.println("# " + x + "  " + y);     newEst.addValue(x, y, 1);   }   // Pick a random conditional value   int cond = Math.abs(r.nextInt() % 100);   System.out.println("## Conditional = " + cond);   // Print the probabilities conditional on that value   Estimator result = newEst.getEstimator(cond);   for(int i = 0; i <= 100; i+= 5) {     System.out.println(" " + i + "  " + result.getProbability(i));   }
Version:
  
$Revision: 1.4 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)
' where name='weka.ConditionalEstimator';
UPDATE implementation set fulldescription='
Version:
  
$Revision: 1.2.2.1 $

Author:
  
Peter A. Flach, Nicolas Lachiche

See Also:

Serialized Form
' where name='weka.Literal';
UPDATE implementation set fulldescription='A class representing the caching system used to keep track of each attribute value and its corresponding scale factor or stop parameter.
Version:
  
$Revision 1.0 $

Author:
  
Len Trigg (len@reeltwo.com), Abdelaziz Mahoui (am14@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.KStarCache';
UPDATE implementation set fulldescription='Bean info class for the strip chart bean
Version:
  
$Revision: 1.1 $

Author:
  
Mark Hall
' where name='weka.StripChartBeanInfo';
UPDATE implementation set fulldescription='Implementation encapsulating a change in the AttributePanel\'s selected x and y attributes.
Version:
  
$Revision: 1.3 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)
' where name='weka.AttributePanelEvent';
UPDATE implementation set fulldescription='This filter should be extended by other unsupervised attribute filters to allow processing of the class attribute if that\'s required. It the class is to be ignored it is essential that the extending filter does not change the position (i.e. index) of the attribute that is originally the class attribute !
Version:
  
$Revision: 1.2.2.1 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz), Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.PotentialClassIgnorer';
UPDATE implementation set fulldescription='This panel controls the configuration of an experiment. If 
KOML
 is in the classpath the experiments can also be saved to XML instead of a binary format.
Version:
  
$Revision: 1.6.2.4 $

Author:
  
Richard kirkby (rkirkby@cs.waikato.ac.nz), FracPete (fracpete at waikato dot ac dot nz)

See Also:

Serialized Form
' where name='weka.SimpleSetupPanel';
UPDATE implementation set fulldescription='Exception that is raised when trying to use some data that has no class assigned to it, but a class is needed to perform the operation.
Version:
  
$Revision: 1.3 $

Author:
  
Richard Kirkby (rkirkby@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.UnassignedClassException';
UPDATE implementation set fulldescription='HillClimber implements hill climbing using local search  for learning Bayesian network.
Author:
  
Remco Bouckaert (rrb@xm.co.nz) Version: $Revision: 1.4.2.1 $

See Also:

Serialized Form
' where name='weka.HillClimber';
UPDATE implementation set fulldescription='' where name='weka.KStarWrapper';
UPDATE implementation set fulldescription='Fast decision tree learner. Builds a decision/regression tree using information gain/variance reduction and prunes it using reduced-error pruning (with backfitting).  Only sorts values for numeric attributes once. Missing values are dealt with by splitting the corresponding instances into pieces (i.e. as in C4.5). Valid options are: 
 -M number 
 Set minimum number of instances per leaf (default 2). 
 -V number 
 Set minimum numeric class variance proportion of train variance for split (default 1e-3). 
 -N number 
 Number of folds for reduced error pruning (default 3). 
 -S number 
  Seed for random data shuffling (default 1). 
 -P 
 No pruning. 
 -L 
 Maximum tree depth (default -1, no maximum). 

Version:
  
$Revision: 6953 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.REPTree';
UPDATE implementation set fulldescription='Implementation for a node in a linked list. Used in best first search.' where name='weka.DecisionTable.Link';
UPDATE implementation set fulldescription='Implementation for building and using a 1R classifier. For more information, see
 R.C. Holte (1993). 
Very simple classification rules perform well on most commonly used datasets
. Machine Learning, Vol. 11, pp. 63-91.
 Valid options are:
 -B num 
 Specify the minimum number of objects in a bucket (default: 6). 

Version:
  
$Revision: 1.17.2.1 $

Author:
  
Ian H. Witten (ihw@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.OneR';
UPDATE implementation set fulldescription='Implementation for "logistic model tree" classifier. For more information, see master thesis "Logistic Model Trees" (Niels Landwehr, 2003)
 Valid options are: 
 -B 
 Binary splits (convert nominal attributes to binary ones).
 -R 
 Split on residuals instead of class values 
 -C 
  Use cross-validation for boosting at all nodes (i.e., disable heuristic) 
 -P 
 Use error on probabilities instead of misclassification error for stopping criterion of LogitBoost. 
 -I iterations 
 Set fixed number of iterations for LogitBoost (instead of using cross-validation). 
 -M numInstances 
 Set minimum number of instances at which a node can be split (default 15)
Version:
  
$Revision: 1.2.2.1 $

Author:
  
Niels Landwehr

See Also:

Serialized Form
' where name='weka.LMT';
UPDATE implementation set fulldescription='Interface to something that can accpet test set events
Version:
  
$Revision: 1.2 $

Author:
  
Mark Hall
' where name='weka.TestSetListener';
UPDATE implementation set fulldescription='Event encapsulating info for plotting a data point on the StripChart
Version:
  
$Revision: 1.2 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.ChartEvent';
UPDATE implementation set fulldescription='Implements stacking. For more information, see
 David H. Wolpert (1992). 
Stacked generalization
. Neural Networks, 5:241-259, Pergamon Press. 
 Valid options are:
 -X num_folds 
 The number of folds for the cross-validation (default 10).
 -S seed 
 Random number seed (default 1).
 -B classifierstring 
 Classifierstring should contain the full class name of a base scheme followed by options to the classifier. (required, option should be used once for each classifier).
 -M classifierstring 
 Classifierstring for the meta classifier. Same format as for base classifiers. (required) 

Version:
  
$Revision: 6995 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.Stacking';
UPDATE implementation set fulldescription='Second implementation for building and using a multinomial logistic regression model with a ridge estimator.  
 There are some modifications, however, compared to the paper of le Cessie and van Houwelingen(1992): 
 If there are k classes for n instances with m attributes, the parameter matrix B to be calculated will be an m*(k-1) matrix.
 The probability for class j except the last class is 
 Pj(Xi) = exp(XiBj)/((sum[j=1..(k-1)]exp(Xi*Bj))+1) 
 The last class has probability 
 1-(sum[j=1..(k-1)]Pj(Xi)) = 1/((sum[j=1..(k-1)]exp(Xi*Bj))+1) 
 The (negative) multinomial log-likelihood is thus: 
 L = -sum[i=1..n]{ sum[j=1..(k-1)](Yij * ln(Pj(Xi))) + (1 - (sum[j=1..(k-1)]Yij)) * ln(1 - sum[j=1..(k-1)]Pj(Xi)) } + ridge * (B^2) 
 In order to find the matrix B for which L is minimised, a Quasi-Newton Method is used to search for the optimized values of the m*(k-1) variables.  Note that before we use the optimization procedure, we "squeeze" the matrix B into a m*(k-1) vector.  For details of the optimization procedure, please check weka.core.Optimization class. 
 Although original Logistic Regression does not deal with instance weights, we modify the algorithm a little bit to handle the instance weights. 
 Reference: le Cessie, S. and van Houwelingen, J.C. (1992). 
 Ridge Estimators in Logistic Regression.
 Applied Statistics, Vol. 41, No. 1, pp. 191-201. 
 Missing values are replaced using a ReplaceMissingValuesFilter, and nominal attributes are transformed into numeric attributes using a NominalToBinaryFilter.
 Valid options are:
 -D 
 Turn on debugging output.
 -R 
 
 Set the ridge parameter for the log-likelihood.
 -M 
 
 Set the maximum number of iterations (default -1, iterates until convergence).

Version:
  
$Revision: 1.32 $

Author:
  
Xin Xu (xx5@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.Logistic';
UPDATE implementation set fulldescription='Implementation for storing a set of items together with a  class label. Item sets are stored in a lexicographic order, which is determined by the header information of the set of instances used for generating the set of items. All methods in this class assume that item sets are stored in lexicographic order. The class provides the methods used for item sets in class association rule mining. Because every item set knows its class label the training set can be splitted up virtually.
Version:
  
$Revision: 1.2 $

Author:
  
Stefan Mutter (mutter@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.LabeledItemSet';
UPDATE implementation set fulldescription='A filter that converts all incoming sparse instances into  non-sparse format.
Version:
  
$Revision: 1.2 $

Author:
  
Len Trigg (len@reeltwo.com)

See Also:

Serialized Form
' where name='weka.SparseToNonSparse';
UPDATE implementation set fulldescription='Encapsulates an evaluatable numeric prediction: the predicted class value plus the actual class value.
Version:
  
$Revision: 1.6 $

Author:
  
Len Trigg (len@reeltwo.com)

See Also:

Serialized Form
' where name='weka.NumericPrediction';
UPDATE implementation set fulldescription='This interface defines the methods required for an object  that produces results for different randomizations of a dataset. 
 Possible implementations of ResultProducer: 
   
Random test/train splits   
CrossValidation splits   
LearningCurve splits (multiple results per run?)   
Averaging results of other result producers
Version:
  
$Revision: 1.6 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)
' where name='weka.ResultProducer';
UPDATE implementation set fulldescription='Bean info class for the text viewer
Version:
  
$Revision: 1.1 $

Author:
  
Mark Hall
' where name='weka.TextViewerBeanInfo';
UPDATE implementation set fulldescription='Abstract class for Savers that save to a file Valid options are: -i input arff file 
 The input filw in arff format. 
 -o the output file 
 The output file. The prefix of the output file is sufficient. If no output file is given, Saver tries to use standard out. 

Version:
  
$Revision: 1.1.2.1 $

Author:
  
Richard Kirkby (rkirkby@cs.waikato.ac.nz), Stefan Mutter (mutter@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.AbstractFileSaver';
UPDATE implementation set fulldescription='A Panel representing an ARFF-Table and the associated filename.
Version:
  
$Revision: 1.1.2.3 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)

See Also:

Serialized Form
' where name='weka.ArffPanel';
UPDATE implementation set fulldescription='Bean that can display a horizontally scrolling strip chart. Can display multiple plots simultaneously
Version:
  
$Revision: 1.10.2.2 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.StripChart';
UPDATE implementation set fulldescription='BoundaryPanel. A class to handle the plotting operations associated with generating a 2D picture of a classifier\'s decision boundaries.
Since:
  
1.0

Version:
  
$Revision: 5986 $

Author:
  
Mark Hall

See Also:

JPanel
, 
Serialized Form
' where name='weka.BoundaryPanel';
UPDATE implementation set fulldescription='Implementation for storing a set of items. Item sets are stored in a lexicographic order, which is determined by the header information of the set of instances used for generating the set of items. All methods in this class assume that item sets are stored in lexicographic order. The class provides methods that are used in the Apriori algorithm to construct association rules.
Version:
  
$Revision: 1.2.2.1 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz), Stefan Mutter (mutter@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.AprioriItemSet';
UPDATE implementation set fulldescription='Meta classifier for transforming an ordinal class problem to a series of binary class problems. For more information see: 
 Frank, E. and Hall, M. (in press). 
A simple approach to ordinal  prediction.
 12th European Conference on Machine Learning.  Freiburg, Germany. 
 Valid options are: 
 -W classname 
 Specify the full class name of a learner as the basis for  the ordinalclassclassifier (required).

Version:
  
$Revision 1.0 $

Author:
  
Mark Hall

See Also:

OptionHandler
, 
Serialized Form
' where name='weka.OrdinalClassClassifier';
UPDATE implementation set fulldescription='This Panel enables the user to print the panel to various file formats. The Print dialog is accessible via Ctrl-Shft-Left Mouse Click. 
 The individual JComponentWriter-descendants can be accessed by the 
getWriter(String)
 method, if the parameters need to be changed.
Version:
  
$Revision: 1.1.2.1 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)

See Also:

getWriters()
, 
getWriter(String)
, 
Serialized Form
' where name='weka.PrintablePanel';
UPDATE implementation set fulldescription='Bean that encapsulates displays bar graph summaries for attributes in a data set.
Version:
  
$Revision: 1.7.2.1 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.AttributeSummarizer';
UPDATE implementation set fulldescription='A filter that removes instances which are incorrectly classified. Useful for removing outliers. 
 Valid filter-specific options are: 
 -W classifier string 
 Full class name of classifier to use, followed by scheme options. (required)
 -C class index 
 Attribute on which misclassifications are based. If &lt; 0 will use any current set class or default to the last attribute. 
 -F number of folds 
 The number of folds to use for cross-validation cleansing. (&lt;2 = no cross-validation - default)
  -T threshold 
 Threshold for the max error when predicting numeric class. (Value should be &gt;= 0, default = 0.1)
 -I max iterations 
 The maximum number of cleansing iterations to perform. (&lt;1 = until fully cleansed - default)
 -V 
 Invert the match so that correctly classified instances are discarded.

Version:
  
$Revision: 1.2.2.1 $

Author:
  
Richard Kirkby (rkirkby@cs.waikato.ac.nz), Malcolm Ware (mfw4@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.RemoveMisclassified';
UPDATE implementation set fulldescription='GUI Customizer for the loader bean
Version:
  
$Revision: 1.7.2.2 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.LoaderCustomizer';
UPDATE implementation set fulldescription='An interface for objects capable of producing streams of instances.
Version:
  
$Revision: 1.4 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)
' where name='weka.InstanceProducer';
UPDATE implementation set fulldescription='This class offers get methods for the default Experimenter settings in  the props file 
weka.gui.experiment.Experimenter.props
.
Version:
  
$Revision: 1.1.2.3 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)

See Also:

PROPERTY_FILE
, 
Serialized Form
' where name='weka.ExperimenterDefaults';
UPDATE implementation set fulldescription='Reads a text file that is comma or tab delimited..
Version:
  
$Revision: 1.9.2.5 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Loader
, 
Serialized Form
' where name='weka.CSVLoader';
UPDATE implementation set fulldescription='Interface to something that understands options.
Version:
  
$Revision: 1.7 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz), Len Trigg (trigg@cs.waikato.ac.nz)
' where name='weka.OptionHandler';
UPDATE implementation set fulldescription='Interface to something that can accept DataSetEvents
Since:
  
1.0

Version:
  
$Revision: 1.2 $

Author:
  
Mark Hall

See Also:

EventListener
' where name='weka.DataSourceListener';
UPDATE implementation set fulldescription='A SplitEvaluator that produces results for a classification scheme on a nominal class attribute, including weighted misclassification costs.
Version:
  
$Revision: 1.10.2.2 $

Author:
  
Len Trigg (len@reeltwo.com)

See Also:

Serialized Form
' where name='weka.CostSensitiveClassifierSplitEvaluator';
UPDATE implementation set fulldescription='Implementation for the format of floating point numbers
Version:
  
$Revision: 1.2.2.1 $

Author:
  
Yong Wang

See Also:

Serialized Form
' where name='weka.FloatingPointFormat';
UPDATE implementation set fulldescription='Encapsulates an evaluatable nominal prediction: the predicted probability distribution plus the actual class value.
Version:
  
$Revision: 1.9.2.1 $

Author:
  
Len Trigg (len@reeltwo.com)

See Also:

Serialized Form
' where name='weka.NominalPrediction';
UPDATE implementation set fulldescription='Implements Grading. For more information, see
  Seewald A.K., Fuernkranz J. (2001): An Evaluation of Grading    Classifiers, in Hoffmann F.\ et al.\ (eds.), Advances in Intelligent    Data Analysis, 4th International Conference, IDA 2001, Proceedings,    Springer, Berlin/Heidelberg/New York/Tokyo, pp.115-124, 2001 Valid options are:
 -X num_folds 
 The number of folds for the cross-validation (default 10).
 -S seed 
 Random number seed (default 1).
 -B classifierstring 
 Classifierstring should contain the full class name of a base scheme followed by options to the classifier. (required, option should be used once for each classifier).
 -M classifierstring 
 Classifierstring for the meta classifier. Same format as for base classifiers. This classifier estimates confidence in prediction of base classifiers. (required) 

Version:
  
$Revision: 1.4.2.2 $

Author:
  
Alexander K. Seewald (alex@seewald.at), Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.Grading';
UPDATE implementation set fulldescription='Implementation for generating an user defined decision tree. For more info see 
 Ware M., Frank E., Holmes G., Hall M. and Witten I.H. (2000). 
interactive machine learning - letting users build classifiers
, Working Paper 00/4, Department of Computer Science,  University of Waikato; March. Also available online at <a href="http://www.cs.waikato.ac.nz/~ml/publications/2000/ 00MW-etal-Interactive-ML.ps"> http://www.cs.waikato.ac.nz/~ml/publications/2000/ 00MW-etal-Interactive-ML.ps
. 

Version:
  
$Revision: 1.18.2.2 $

Author:
  
Malcolm Ware (mfw4@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.UserClassifier';
UPDATE implementation set fulldescription='Abstract class for classification models that can be used  recursively to split the data.
Version:
  
$Revision: 1.8 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.ClassifierSplitModel';
UPDATE implementation set fulldescription='A class to store simple statistics
Version:
  
$Revision: 1.9 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.Stats';
UPDATE implementation set fulldescription='Interface to something that can process a BatchClassifierEvent
Since:
  
1.0

Version:
  
$Revision: 1.1 $

Author:
  
Mark Hall

See Also:

EventListener
' where name='weka.BatchClassifierListener';
UPDATE implementation set fulldescription='This instance filter takes a range of N numeric attributes and replaces them with N-1 numeric attributes, the values of which are the difference  between consecutive attribute values from the original instance. eg: 
 Original attribute values 
 
 0.1, 0.2, 0.3, 0.1, 0.3 
 
 New attribute values 
 
 0.1, 0.1, -0.2, 0.2 
 
 The range of attributes used is taken in numeric order. That is, a range spec of 7-11,3-5 will use the attribute ordering 3,4,5,7,8,9,10,11 for the differences, 
not
 7,8,9,10,11,3,4,5.
 Valid filter-specific options are:
 -R index1,index2-index4,...
 Specify list of columns to take the differences between.  First and last are valid indexes. (default none)

Version:
  
$Revision: 1.3.2.1 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.FirstOrder';
UPDATE implementation set fulldescription='Abstract class for TrainingSetProducers that contains default implementations of add/remove listener methods and default visual representation
Since:
  
1.0

Version:
  
$Revision: 1.2.2.1 $

Author:
  
Mark Hall

See Also:

TrainingSetProducer
, 
DataSetListener
, 
Serialized Form
' where name='weka.AbstractTrainingSetProducer';
UPDATE implementation set fulldescription='Bean info class for AbstractTrainAndTestSetProducers
Version:
  
$Revision: 1.1 $

Author:
  
Mark Hall
' where name='weka.AbstractTrainAndTestSetProducerBeanInfo';
UPDATE implementation set fulldescription='Implementation for matrix manipulation used for pace regression. 
 REFERENCES 
 Wang, Y. (2000). "A new approach to fitting linear models in high dimensional spaces." PhD Thesis. Department of Computer Science, University of Waikato, New Zealand. 
 Wang, Y. and Witten, I. H. (2002). "Modeling for optimal probability prediction." Proceedings of ICML\'2002. Sydney. 

Version:
  
$Revision: 1.1 $

Author:
  
Yong Wang (yongwang@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.PaceMatrix';
UPDATE implementation set fulldescription='Implementation to store information about an option. 
 Typical usage: 
 
Option myOption = new Option("Uses extended mode.", "E", 0, "-E")); 

Version:
  
$Revision: 1.5 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)
' where name='weka.Option';
UPDATE implementation set fulldescription='Swaps two values of a nominal attribute.
 Valid filter-specific options are: 
 -C col 
 Index of the attribute to be changed. (default last)
 -F index 
 Index of the first value (default first).
 -S index 
 Index of the second value (default last).

Version:
  
$Revision: 1.4 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.SwapValues';
UPDATE implementation set fulldescription='Implementation that can test whether a given string is a stop word. Lowercases all words before the test.
Version:
  
$Revision: 1.1.2.1 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz), Ashraf M. Kibriya (amk14@cs.waikato.ac.nz)
' where name='weka.Stopwords';
UPDATE implementation set fulldescription='Generates points illustrating prediction tradeoffs that can be obtained by varying the threshold value between classes. For example, the typical  threshold value of 0.5 means the predicted probability of "positive" must be higher than 0.5 for the instance to be predicted as "positive". The  resulting dataset can be used to visualize precision/recall tradeoff, or  for ROC curve analysis (true positive rate vs false positive rate). Weka just varies the threshold on the class probability estimates in each  case. The Mann Whitney statistic is used to calculate the AUC.
Version:
  
$Revision: 1.18.2.1 $

Author:
  
Len Trigg (len@reeltwo.com)
' where name='weka.ThresholdCurve';
UPDATE implementation set fulldescription='This filter removes attributes that do not vary at all or that vary too much. All constant attributes are deleted automatically, along with any that exceed the maximum percentage of variance parameter. The maximum variance test is only applied to nominal attributes.
 Valid filter-specific options are: 
 -M percentage 
 The maximum variance allowed before an attribute will be deleted (default 99).

Version:
  
$Revision: 1.5.2.1 $

Author:
  
Richard Kirkby (rkirkby@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.RemoveUseless';
UPDATE implementation set fulldescription='Bean that collects and displays pieces of text
Version:
  
$Revision: 4748 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.TextViewer';
UPDATE implementation set fulldescription='This class extends the component which is handed over in the constructor by a print dialog. The Print dialog is accessible via Ctrl-Shft-Left Mouse Click. 
 The individual JComponentWriter-descendants can be accessed by the 
getWriter(String)
 method, if the parameters need to be changed.
Version:
  
$Revision: 1.1.2.4 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)

See Also:

getWriters()
, 
getWriter(String)

' where name='weka.PrintableComponent';
UPDATE implementation set fulldescription='This panel is a VisualizePanel, with the added ablility to display the area under the ROC curve if an ROC curve is chosen.
Version:
  
$Revision: 1.1 $

Author:
  
Dale Fletcher (dale@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.ThresholdVisualizePanel';
UPDATE implementation set fulldescription='Implementation for storing an instance as a sparse vector. A sparse instance only requires storage for those attribute values that are non-zero. Since the objective is to reduce storage requirements for datasets with large numbers of default values, this also includes nominal attributes -- the first nominal value (i.e. that which has index 0) will not require explicit storage, so rearrange your nominal attribute value orderings if necessary. Missing values will be stored explicitly.
Version:
  
$Revision: 1.14.2.3 $

Author:
  
Eibe Frank

See Also:

Serialized Form
' where name='weka.SparseInstance';
UPDATE implementation set fulldescription='A custom hashtable class to support the caching system.
See Also:

Serialized Form
' where name='weka.KStarCache.CacheTable';
UPDATE implementation set fulldescription='Interface implemented by classes that can produce "shallow" copies of their objects. (As opposed to clone(), which is supposed to produce a "deep" copy.)
Version:
  
$Revision: 1.5 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)
' where name='weka.Copyable';
UPDATE implementation set fulldescription='Implementation for constructing random forests. For more information see: 
 Leo Breiman. Random Forests. Machine Learning 45 (1):5-32, October 2001. 
 Valid options are: 
 -I num 
 Set the number of trees in the forest (default 10) 
 -K num 
 Set the number of features to consider. If 
 -S seed 
 Random number seed (default 1). 

Version:
  
$Revision: 1.6 $

Author:
  
Richard Kirkby (rkirkby@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.RandomForest';
UPDATE implementation set fulldescription='Interface for allowing to score a classifier
Version:
  
$Revision: 1.1.2.1 $

Author:
  
Remco Bouckaert (rrb@xm.co.nz)
' where name='weka.Scoreable';
UPDATE implementation set fulldescription='Implementation for building and using a PRISM rule set for classifcation.   Can only deal with nominal attributes. Can\'t deal with missing values. Doesn\'t do any pruning. For more information, see 
 J. Cendrowska (1987). 
PRISM: An algorithm for inducing modular rules
. International Journal of Man-Machine Studies. Vol.27, No.4, pp.349-370.

Version:
  
$Revision: 1.17 $

Author:
  
Ian H. Witten (ihw@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.Prism';
UPDATE implementation set fulldescription='A class for storing stats on a paired comparison. This version is based on the corrected resampled t-test statistic, which uses the ratio of the number of test examples/the number of training examples.
 For more information see:
 Claude Nadeau and Yoshua Bengio, "Inference for the Generalization Error," Machine Learning, 2001.
Version:
  
$Revision: 1.2 $

Author:
  
Richard Kirkby (rkirkby@cs.waikato.ac.nz)
' where name='weka.PairedStatsCorrected';
UPDATE implementation set fulldescription='Implementation for handling a tree structure that can be pruned using a pruning set.
Version:
  
$Revision: 1.8 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.PruneableClassifierTree';
UPDATE implementation set fulldescription='This interface should be implemented by any class which needs to receive LayoutCompleteEvents from the LayoutEngine. Typically this would be implemented by the Visualization class.
Version:
  
$Revision: 1.3.2.1 $ - 24 Apr 2003 - Initial version (Ashraf M. Kibriya)

Author:
  
Ashraf M. Kibriya (amk14@cs.waikato.ac.nz)
' where name='weka.LayoutCompleteEventListener';
UPDATE implementation set fulldescription='Abstract unsupervised attribute evaluator.
Version:
  
$Revision: 1.6 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.UnsupervisedAttributeEvaluator';
UPDATE implementation set fulldescription='Event that encapsulates some textual information
Version:
  
$Revision: 1.2 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.TextEvent';
UPDATE implementation set fulldescription='Listener interface that customizer classes that are interested in data format changes can implement.
Version:
  
$Revision: 1.1.2.2 $

Author:
  
Mark Hall
' where name='weka.DataFormatListener';
UPDATE implementation set fulldescription='A custom class which provides the environment for computing the transformation probability of a specified test instance nominal attribute to a specified train instance nominal attribute.
Version:
  
$Revision 1.0 $

Author:
  
Len Trigg (len@reeltwo.com), Abdelaziz Mahoui (am14@cs.waikato.ac.nz)
' where name='weka.KStarNominalAttribute';
UPDATE implementation set fulldescription='Interface for probability estimators. Example code: 
   // Create a discrete estimator that takes values 0 to 9   DiscreteEstimator newEst = new DiscreteEstimator(10, true);   // Create 50 random integers first predicting the probability of the   // value, then adding the value to the estimator   Random r = new Random(seed);   for(int i = 0; i < 50; i++) {     current = Math.abs(r.nextInt() % 10);     System.out.println(newEst);     System.out.println("Prediction for " + current                         + " = " + newEst.getProbability(current));     newEst.addValue(current, 1);   }
Version:
  
$Revision: 1.5 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)
' where name='weka.Estimator';
UPDATE implementation set fulldescription='This is an interface used to create classes that can be used by the  neuralnode to perform all it\'s computations.
Version:
  
$Revision: 1.4 $

Author:
  
Malcolm Ware (mfw4@cs.waikato.ac.nz)
' where name='weka.NeuralMethod';
UPDATE implementation set fulldescription='Implementation for a Naive Bayes classifier using estimator classes. This is the updateable version of NaiveBayes. This classifier will use a default precision of 0.1 for numeric attributes when buildClassifier is called with zero training instances. For more information on Naive Bayes classifiers, see
 George H. John and Pat Langley (1995). 
Estimating Continuous Distributions in Bayesian Classifiers
. Proceedings of the Eleventh Conference on Uncertainty in Artificial Intelligence. pp. 338-345. Morgan Kaufmann, San Mateo.
 Valid options are:
 -K 
 Use kernel estimation for modelling numeric attributes rather than a single normal distribution.

Version:
  
$Revision: 1.4 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz), Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.NaiveBayesUpdateable';
UPDATE implementation set fulldescription='This class handles relationships between display names of properties  (or classes) and Methods that are associated with them. It differentiates  between read and write methods. It automatically stores public methods that  have the same signature as the 
readFromXML()
 and  
writeToXML()
 methods in the 
XMLSerialization
 class.
Version:
  
$Revision: 1.1.2.3 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)

See Also:

MethodHandler
, 
XMLSerialization

' where name='weka.XMLSerializationMethodHandler';
UPDATE implementation set fulldescription='Implementation that implements a normalized Gaussian radial basis function network.  It uses the k-means clustering algorithm to provide the basis functions and learns either a logistic regression (discrete class problems) or linear regression (numeric class problems) on top of that. Symmetric multivariate Gaussians are fit to the data from each cluster. If the class is nominal it uses the given number of clusters per class. It standardizes all numeric attributes to zero mean and unit variance. Valid options are:
 -B num 
 Set the number of clusters (basis functions) to use.
 -R ridge 
 Set the ridge parameter for the logistic regression or linear regression.
 -M num 
 Set the maximum number of iterations for logistic regression. (default -1, until convergence)
 -S seed 
 Set the random seed used by K-means when generating clusters.  (default 1). 
 -W num 
 Set the minimum standard deviation for the clusters. (default 0.1). 

Version:
  
$Revision: 1.4.2.1 $

Author:
  
Mark Hall, Eibe Frank

See Also:

Serialized Form
' where name='weka.RBFNetwork';
UPDATE implementation set fulldescription='Utility class. Adapted from the 
JAMA
 package.
Version:
  
$Revision: 1.1.2.2 $

Author:
  
The Mathworks and NIST, Fracpete (fracpete at waikato dot ac dot nz)
' where name='weka.Maths';
UPDATE implementation set fulldescription='Implementation implementing the prior estimattion of the predictive apriori algorithm  for mining association rules.  Reference: T. Scheffer (2001). 
Finding Association Rules That Trade Support  Optimally against Confidence
. Proc of the 5th European Conf. on Principles and Practice of Knowledge Discovery in Databases (PKDD\'01), pp. 424-435. Freiburg, Germany: Springer-Verlag. 

Version:
  
$Revision: 1.4 $

Author:
  
Stefan Mutter (mutter@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.PriorEstimation';
UPDATE implementation set fulldescription='Implementation implementing a binary C4.5-like split on an attribute.
Version:
  
$Revision: 1.9.2.1 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.BinC45Split';
UPDATE implementation set fulldescription='Abstract utility class for handling settings common to randomizable classifiers.
Version:
  
$Revision: 1.2 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.RandomizableClassifier';
UPDATE implementation set fulldescription='Transforms numeric attributes using a given transformation method.
 Valid filter-specific options are: 
 -R index1,index2-index4,...
 Specify list of columns to transform. First and last are valid indexes. (default none). Non-numeric columns are skipped.
 -V
 Invert matching sense.
 -C string 
 Name of the class containing the method used for transformation.  (default java.lang.Math) 
 -M string 
 Name of the method used for the transformation. (default abs) 

Version:
  
$Revision: 1.5 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.NumericTransform';
UPDATE implementation set fulldescription='The CISearchAlgorithm class supports Bayes net structure search algorithms that are based on conditional independence test (as opposed to for example score based of cross validation based search algorithms).
Version:
  
$Revision: 1.4 $

Author:
  
Remco Bouckaert (rrb@xm.co.nz)

See Also:

Serialized Form
' where name='weka.CISearchAlgorithm';
UPDATE implementation set fulldescription='Bean info class for the Filter bean
Version:
  
$Revision: 1.3 $

Author:
  
Mark Hall
' where name='weka.FilterBeanInfo';
UPDATE implementation set fulldescription='Interface to something that can process a ChartEvent
Version:
  
$Revision: 1.2 $

Author:
  
Mark Hall
' where name='weka.ChartListener';
UPDATE implementation set fulldescription='Abstract class for objects that can provide instances from some source
Since:
  
1.0

Version:
  
$Revision: 1.2 $

Author:
  
Mark Hall

See Also:

JPanel
, 
DataSource
, 
Serializable
, 
Serialized Form
' where name='weka.AbstractDataSource';
UPDATE implementation set fulldescription='Writes to a destination in csv format. Valid options: -i input arff file 
 The input filw in ARFF format. 
 -o the output file 
 The output file. The prefix of the output file is sufficient. If no output file is given, Saver tries to use standard out. 

Version:
  
$Revision: 1.2.2.2 $

Author:
  
Stefan Mutter (mutter@cs.waikato.ac.nz)

See Also:

Saver
, 
Serialized Form
' where name='weka.CSVSaver';
UPDATE implementation set fulldescription='This class serializes and deserializes a KnowledgeFlow setup to and fro XML.
Version:
  
$Revision: 1.1.2.6 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)
' where name='weka.XMLBeans';
UPDATE implementation set fulldescription='Encapsulates a single evaluatable prediction: the predicted value plus the  actual class value.
Version:
  
$Revision: 1.6 $

Author:
  
Len Trigg (len@reeltwo.com)
' where name='weka.Prediction';
UPDATE implementation set fulldescription='Reads C4.5 input files. Takes a filestem or filestem with .names or .data appended. Assumes that both 
.names and 
.data exist in the directory of the supplied filestem.
Version:
  
$Revision: 1.9.2.3 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Loader
, 
Serialized Form
' where name='weka.C45Loader';
UPDATE implementation set fulldescription='Implementation that encapsulates a result (and progress info) for part of a distributed boundary visualization. The result of a sub-task is the probabilities necessary to display one row of the final  visualization.
Since:
  
1.0

Version:
  
$Revision: 1.1 $

Author:
  
Mark Hall

See Also:

Serializable
, 
Serialized Form
' where name='weka.RemoteResult';
UPDATE implementation set fulldescription='Bean that can be used for displaying threshold curves (e.g. ROC curves) and scheme error plots
Version:
  
$Revision: 1.3.2.2 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.ModelPerformanceChart';
UPDATE implementation set fulldescription='Bean info class for the classifier performance evaluator
Version:
  
$Revision: 1.2.2.2 $

Author:
  
Mark Hall
' where name='weka.ClassifierPerformanceEvaluatorBeanInfo';
UPDATE implementation set fulldescription='Implementation to encapsulate an experiment as a task that can be executed on a remote host.
Version:
  
$Revision: 1.6.2.1 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.RemoteExperimentSubTask';
UPDATE implementation set fulldescription='An instance filter that discretizes a range of numeric attributes in  the dataset into nominal attributes. Discretization is by simple binning. Skips the class attribute if set.
 Valid filter-specific options are: 
 -B num 
 Specifies the (maximum) number of bins to divide numeric attributes into. Default = 10.
 -M num 
 Specifies the desired weight of instances per bin for equal-frequency binning. If this is set to a positive number then the -B option will be  ignored. Default = -1.
 -F 
 Use equal-frequency instead of equal-width discretization if  class-based discretisation is turned off.
 -O 
 Optimize the number of bins using a leave-one-out estimate of the  entropy (for equal-width binning). If this is set then the -B option will be ignored.
 -R col1,col2-col4,... 
 Specifies list of columns to Discretize. First and last are valid indexes. (default: first-last) 
 -V 
 Invert matching sense.
 -D 
 Make binary nominal attributes. 

Version:
  
$Revision: 1.6.2.1 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz), Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.Discretize';
UPDATE implementation set fulldescription='Abstract class for computing splitting criteria with respect to distributions of class values.
Version:
  
$Revision: 1.5 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.SplitCriterion';
UPDATE implementation set fulldescription='OutputZipper writes output to either gzipped files or to a multi entry zip file. If the destination file is a directory each output string will be written to an individually named gzip file. If the destination file is a file, then each output string is appended as a named entry to the zip file until finished() is called to close the file.
Version:
  
$Revision: 1.5 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)
' where name='weka.OutputZipper';
UPDATE implementation set fulldescription='Implementation for storing and manipulating a misclassification cost matrix. The element at position i,j in the matrix is the penalty for classifying an instance of class j as class i.
Version:
  
$Revision: 1.9.2.1 $

Author:
  
Richard Kirkby (rkirkby@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.CostMatrix';
UPDATE implementation set fulldescription='Abstract scheme for learning associations. All schemes for learning associations implemement this class
Version:
  
$Revision: 1.5 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.Associator';
UPDATE implementation set fulldescription='Main GUI class for the KnowledgeFlow
Since:
  
1.0

Version:
  
$Revision: 6210 $

Author:
  
Mark Hall

See Also:

JPanel
, 
PropertyChangeListener
, 
Serialized Form
' where name='weka.KnowledgeFlowApp';
UPDATE implementation set fulldescription='Implementation for evaluating a attribute ranking (given by a specified evaluator) using a specified subset evaluator. 
 Valid options are: 
 -A 
 
 Specify the attribute/subset evaluator to be used for generating the  ranking. If a subset evaluator is specified then a forward selection search is used to produce a ranked list of attributes.

Version:
  
$Revision: 1.11 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.RankSearch';
UPDATE implementation set fulldescription='Implementation for handling a naive bayes tree structure used for classification.
Version:
  
$Revision: 1.1 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.NBTreeClassifierTree';
UPDATE implementation set fulldescription='Abstract unit in a NeuralNetwork.
Version:
  
$Revision: 5405 $

Author:
  
Malcolm Ware (mfw4@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.NeuralConnection';
UPDATE implementation set fulldescription='Abstract utility class for handling settings common to randomizable meta classifiers that build an ensemble from multiple classifiers based on a given random number seed.
Version:
  
$Revision: 1.2 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.RandomizableMultipleClassifiersCombiner';
UPDATE implementation set fulldescription='Standardizes all numeric attributes in the given dataset to have zero mean and unit variance. intervals.
Version:
  
$Revision: 1.4.2.1 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.Standardize';
UPDATE implementation set fulldescription='This metaclassifier makes its base classifier cost-sensitive. Two methods can be used to introduce cost-sensitivity: reweighting training instances  according to the total cost assigned to each class; or predicting the class with minimum expected misclassification cost (rather than the most likely  class). 
 Valid options are:
 -M 
 Minimize expected misclassification cost.  (default is to reweight training instances according to costs per class)
 -W classname 
 Specify the full class name of a classifier (required).
 -C cost file 
 File name of a cost matrix to use. If this is not supplied, a cost matrix will be loaded on demand. The name of the on-demand file is the relation name of the training data plus ".cost", and the path to the on-demand file is specified with the -N option.
 -N directory 
 Name of a directory to search for cost files when loading costs on demand (default current directory). 
 -S seed 
 Random number seed used when reweighting by resampling (default 1).
 -cost-matrix matrix
 The cost matrix, specified in Matlab single line format.
 Options after -- are passed to the designated classifier.

Version:
  
$Revision: 1.18.2.4 $

Author:
  
Len Trigg (len@reeltwo.com)

See Also:

Serialized Form
' where name='weka.CostSensitiveClassifier';
UPDATE implementation set fulldescription='Implementation for performing a best first search. 
 Valid options are: 
 -P start set 
 Specify a starting set of attributes. Eg 1,4,7-9. 
 -D 0 = backward | 1 = forward | 2 = bidirectional 
 Direction of the search. (default = 1). 
 -N num 
 Number of non improving nodes to consider before terminating search. (default = 5). 
 -S num 
 Size of lookup cache for evaluated subsets. Expressed as a multiple of the number of attributes in the data set. (default = 1). 

Version:
  
$Revision: 1.24.2.3 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)         Martin Guetlein (cashing merit of expanded nodes)

See Also:

Serialized Form
' where name='weka.BestFirst';
UPDATE implementation set fulldescription='Abstract utility class for handling settings common to meta classifiers that build an ensemble from multiple classifiers.
Version:
  
$Revision: 5906 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.MultipleClassifiersCombiner';
UPDATE implementation set fulldescription='Classifier subset evaluator. Uses a classifier to estimate the "merit" of a set of attributes. Valid options are:
 -B 
 
 Class name of the classifier to use for accuracy estimation. Place any classifier options last on the command line following a "--". Eg  -B weka.classifiers.bayes.NaiveBayes ... -- -K 
 -T 
 Use the training data for accuracy estimation rather than a hold out/ test set. 
 -H 
 
 The file containing hold out/test instances to use for accuracy estimation
Version:
  
$Revision: 1.12.2.1 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.ClassifierSubsetEval';
UPDATE implementation set fulldescription='A bean that evaluates the performance of batch trained clusterers
Version:
  
$Revision: 1.1 $

Author:
  
Stefan Mutter

See Also:

Serialized Form
' where name='weka.ClustererPerformanceEvaluator';
UPDATE implementation set fulldescription='Base class for a Bayes Network classifier. Provides datastructures (network structure, conditional probability distributions, etc.) and facilities common to Bayes Network learning algorithms like K2 and B. Works with nominal variables and no missing values only. For further documentation, see  
Bayesian networks in Weka
 user documentation.
Version:
  
$Revision: 1.21.2.5 $

Author:
  
Remco Bouckaert (rrb@xm.co.nz)

See Also:

Serialized Form
' where name='weka.BayesNet';
UPDATE implementation set fulldescription='Reads a source that contains serialized Instances.
Version:
  
$Revision: 1.9.2.2 $

Author:
  
Len Trigg

See Also:

Loader
, 
Serialized Form
' where name='weka.SerializedInstancesLoader';
UPDATE implementation set fulldescription='Abstract class for TestSetProducers that contains default implementations of add/remove listener methods and defualt visual representation.
Since:
  
1.0

Version:
  
$Revision: 1.2.2.1 $

Author:
  
Mark Hall

See Also:

TestSetProducer
, 
Serialized Form
' where name='weka.AbstractTestSetProducer';
UPDATE implementation set fulldescription='
Version:
  
$Revision: 1.1.2.1 $

Author:
  
Yong Wang
' where name='weka.IntVector';
UPDATE implementation set fulldescription='Implementation for handling a partial tree structure pruned using C4.5\'s pruning heuristic.
Version:
  
$Revision: 1.6 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.C45PruneableDecList';
UPDATE implementation set fulldescription='Implementation implementing an Apriori-type algorithm. Iteratively reduces the minimum support until it finds the required number of rules with the given minimum  confidence. 
 Reference: R. Agrawal, R. Srikant (1994). 
Fast algorithms for mining association rules in large databases 
. Proc International Conference on Very Large Databases, pp. 478-499. Santiage, Chile: Morgan Kaufmann, Los Altos, CA. 
 Valid options are:
 -N required number of rules 
 The required number of rules (default: 10). 
 -T type of metric by which to sort rules 
 0 = confidence | 1 = lift | 2 = leverage | 3 = Conviction. 
 -C minimum confidence of a rule 
 The minimum confidence of a rule (default: 0.9). 
 -D delta for minimum support 
 The delta by which the minimum support is decreased in each iteration (default: 0.05). 
 -U upper bound for minimum support 
 The upper bound for minimum support. Don\'t explicitly look for  rules with more than this level of support. 
 -M lower bound for minimum support 
 The lower bound for the minimum support (default = 0.1). 
 -S significance level 
 If used, rules are tested for significance at the given level. Slower (default = no significance testing). 
 -R 
 If set then columns that contain all missing values are removed from the data. -I 
 If set the itemsets found are also output (default = no). 

Version:
  
$Revision: 7068 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz), Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.Apriori';
UPDATE implementation set fulldescription='Implementation for learning a simple linear regression model. Picks the attribute that results in the lowest squared error. Missing values are not allowed. Can only deal with numeric attributes.
Version:
  
$Revision: 1.5 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.SimpleLinearRegression';
UPDATE implementation set fulldescription='
Version:
  
$Revision: 1.2.2.1 $

Author:
  
Peter A. Flach, Nicolas Lachiche

See Also:

Serialized Form
' where name='weka.IndividualLiteral';
UPDATE implementation set fulldescription='An event containing the user selection from the tree display
Version:
  
$Revision: 1.3 $

Author:
  
Malcolm Ware (mfw4@cs.waikato.ac.nz)
' where name='weka.TreeDisplayEvent';
UPDATE implementation set fulldescription='Interface for classes that want to listen for updates on RemoteExperiment progress
Version:
  
$Revision: 1.4 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)
' where name='weka.RemoteExperimentListener';
UPDATE implementation set fulldescription='Constructs a node for use in an m5 tree or rule
Version:
  
$Revision: 1.8.2.1 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.RuleNode';
UPDATE implementation set fulldescription='' where name='weka.PrincipalComponents';
UPDATE implementation set fulldescription='Implementation for building a logistic regression model using LogitBoost. Incorporates attribute selection by fitting simple regression functions in LogitBoost. For more information, see master thesis "Logistic Model Trees" (Niels Landwehr, 2003)
 Valid options are: 
 -I iterations 
 Set fixed number of iterations for LogitBoost (instead of using cross-validation). 
 -S 
 Select the number of LogitBoost iterations that gives minimal error on the training set  (instead of using cross-validation). 
 -P 
 Minimize error on probabilities instead of misclassification error. 
 -M iterations 
 Set maximum number of iterations for LogitBoost. 
 -H iter 
 Set parameter for heuristic for early stopping of LogitBoost. If enabled, the minimum is selected greedily, stopping if the current minimum has not changed  for iter iterations. By default, heuristic is enabled with value 50. Set to zero to disable heuristic.
Version:
  
$Revision: 1.5.2.1 $

Author:
  
Niels Landwehr

See Also:

Serialized Form
' where name='weka.SimpleLogistic';
UPDATE implementation set fulldescription='Interface to something that can be drawn as a graph.
Version:
  
$Revision: 1.8 $

Author:
  
Ashraf M. Kibriya(amk14@cs.waikato.ac.nz), Eibe Frank(eibe@cs.waikato.ac.nz)
' where name='weka.Drawable';
UPDATE implementation set fulldescription='This interface is for all JComponent classes that provide the ability to  print itself to a file.
Version:
  
$Revision: 1.1.2.1 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)

See Also:

PrintableComponent
, 
PrintablePanel

' where name='weka.PrintableHandler';
UPDATE implementation set fulldescription='Implementation for manipulating normal mixture distributions. 
 REFERENCES 
 Wang, Y. (2000). "A new approach to fitting linear models in high dimensional spaces." PhD Thesis. Department of Computer Science, University of Waikato, New Zealand. 
 Wang, Y. and Witten, I. H. (2002). "Modeling for optimal probability prediction." Proceedings of ICML\'2002. Sydney. 

Version:
  
$Revision: 1.1 $

Author:
  
Yong Wang (yongwang@cs.waikato.ac.nz)
' where name='weka.NormalMixture';
UPDATE implementation set fulldescription='An instance filter that assumes instances form time-series data and replaces attribute values in the current instance with the difference between the current value and the equivalent attribute attribute value of some previous (or future) instance. For instances where the time-shifted value is unknown either the instance may be dropped, or missing values used.
 Valid filter-specific options are:
 -R index1,index2-index4,...
 Specify list of columns to calculate new values for. First and last are valid indexes. (default none)
 -V 
 Invert matching sense (i.e. calculate for all non-specified columns)
 -I num 
 The number of instances forward to take value differences between. A negative number indicates taking values from a past instance. (default -1) 
 -M 
 For instances at the beginning or end of the dataset where the translated values are not known, use missing values (default is to remove those instances).

Version:
  
$Revision: 1.2.2.2 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.TimeSeriesDelta';
UPDATE implementation set fulldescription='Implementation representing a set of literals, being either the body or the head of a rule.
Version:
  
$Revision: 1.4.2.1 $

Author:
  
Amelie Deltour

See Also:

Serialized Form
' where name='weka.LiteralSet';
UPDATE implementation set fulldescription='Bean info class for the graph viewer
Version:
  
$Revision: 1.1 $

Author:
  
Mark Hall
' where name='weka.GraphViewerBeanInfo';
UPDATE implementation set fulldescription='Implementation for selecting a classifier from among several using cross  validation on the training data or the performance on the training data. Performance is measured based on percent correct (classification) or mean-squared error (regression).
 Valid options from the command line are:
 -D 
 Turn on debugging output.
 -S seed 
 Random number seed (default 1).
 -B classifierstring 
 Classifierstring should contain the full class name of a scheme included for selection followed by options to the classifier (required, option should be used once for each classifier).
 -X num_folds 
 Use cross validation error as the basis for classifier selection. (default 0, is to use error on the training data instead)

Version:
  
$Revision: 1.18.2.1 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.MultiScheme';
UPDATE implementation set fulldescription='Holds all the necessary configuration information for a distributed experiment. This object is able to be serialized for storage on disk.
 This class is experimental at present. Has been tested using  CSVResultListener (sending results to standard out) and  DatabaseResultListener (InstantDB + RmiJdbc bridge). 
 Getting started:
 Start InstantDB (with the RMI bridge) on some machine. If using java2 then specify -Djava.security.policy=db.policy to the virtual machine. Where db.policy is as follows: 
 grant {   permission java.security.AllPermission; }; Start RemoteEngine servers on x machines as per the instructons in the README_Experiment_Gui file. There must be a  DatabaseUtils.props in either the HOME or current directory of each machine, listing all necessary jdbc drivers.
 The machine where a RemoteExperiment is started must also have a copy of DatabaseUtils.props listing the URL to the machine where the  database server is running (RmiJdbc + InstantDB). 
 Here is an example of starting a RemoteExperiment: 
 java -Djava.rmi.server.codebase=file:/path to weka classes/ \ weka.experiment.RemoteExperiment -L 1 -U 10 \ -T /home/ml/datasets/UCI/iris.arff \ -D "weka.experiment.DatabaseResultListener" \ -P "weka.experiment.RandomSplitResultProducer" \ -h rosebud.cs.waikato.ac.nz -h blackbird.cs.waikato.ac.nz -r -- \ -W weka.experiment.ClassifierSplitEvaluator -- \ -W weka.classifiers.bayes.NaiveBayes The "codebase" property tells rmi where to serve up weka classes from. This can either be a file url (as long as a shared file system is being used that is accessable by the remoteEngine servers), or http url (which of course supposes that a web server is running and you have put your weka classes somewhere that is web accessable). If using a file url the trailing "/" is *most* important unless the weka classes are in a jar file. 

Version:
  
$Revision: 1.13.2.2 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.RemoteExperiment';
UPDATE implementation set fulldescription='Implementation for Evaluating attributes individually by measuring symmetrical  uncertainty with respect to the class. Valid options are:
 -M 
 Treat missing values as a seperate value. 

Version:
  
$Revision: 1.15.2.1 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.SymmetricalUncertAttributeEval';
UPDATE implementation set fulldescription='The FromFile reads the structure of a Bayes net from a file in BIFF format.
Version:
  
$Revision: 1.4.2.1 $

Author:
  
Remco Bouckaert

See Also:

Serialized Form
' where name='weka.FromFile';
UPDATE implementation set fulldescription='Bean info class for the scatter plot matrix bean
Version:
  
$Revision: 1.1 $

Author:
  
Mark Hall
' where name='weka.ScatterPlotMatrixBeanInfo';
UPDATE implementation set fulldescription='This utility class is looking for all the classes implementing or  inheriting from a given interface or class.
 (RTSI = RunTime Subclass Identification) 
Notes
    
Source: JavaWorld 
Tip 113
: Identify subclasses at runtime
    
JWhich: JavaWorld 
Tip 105
: Mastering the classpath with JWhich
       Modifications by FracPete:
          
it returns Vectors with the classnames (for the sorting see 
StringCompare
)
          
doesn\'t create an instance of class anymore, but rather tests, whether the superclass/interface is              somewhere in the class hierarchy of the found class and whether it is abstract or not
          
checks all parts of the classpath for the package and does not take the first one only              (i.e. you can have a dir with the default classes and an additional dir with more classes              that are not part of the default ones, e.g. developer classes)

Version:
  
$Revision: 1.2.2.4 $

Author:
  
Daniel Le Berre
, FracPete (fracpete at waikato dot ac dot nz)

See Also:

RTSI.StringCompare

' where name='weka.RTSI';
UPDATE implementation set fulldescription='DECORATE is a meta-learner for building diverse ensembles of classifiers by using specially constructed artificial training examples. Comprehensive experiments have demonstrated that this technique is consistently more accurate than the base classifier,  Bagging and Random Forests. Decorate also obtains higher accuracy than Boosting on small training sets, and achieves comparable performance on larger training sets.  For more details see: 
 Prem Melville and Raymond J. Mooney. 
Constructing diverse classifier ensembles using artificial training examples.
 Proceedings of the Seventeeth International Joint Conference on Artificial Intelligence 2003.
 Prem Melville and Raymond J. Mooney. 
Creating diversity in ensembles using artificial data.
 Submitted.
 Valid options are:
 -D 
 Turn on debugging output.
 -W classname 
 Specify the full class name of a weak classifier as the basis for  Decorate (default weka.classifiers.trees.J48()).
 -E num 
 Specify the desired size of the committee (default 10). 
 -I iterations 
 Set the maximum number of Decorate iterations (default 10). 
 -S seed 
 Seed for random number generator. (default 0).
 -R factor 
 Factor that determines number of artificial examples to generate. 
 Options after -- are passed to the designated classifier.

Version:
  
$Revision: 1.3.2.1 $

Author:
  
Prem Melville (melville@cs.utexas.edu)

See Also:

Serialized Form
' where name='weka.Decorate';
UPDATE implementation set fulldescription='Support for drawing a property value in a component.
Version:
  
$Revision: 5844 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz), Richard Kirkby (rkirkby@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.PropertyPanel';
UPDATE implementation set fulldescription='Implementation for Evaluating attributes individually by using the SVM classifier. Attributes are ranked by the square of the weight assigned by the SVM. Attribute selection for multiclass problems is handled by ranking attributes for each class seperately using a one-vs-all method and then "dealing" from the top of  each pile to give a final ranking.
 For more information see: 
 Guyon, I., Weston, J., Barnhill, S., &amp; Vapnik, V. (2002).  Gene selection for cancer classification using support vector machines. Machine Learning, 46, 389-422 
 Valid options are: 
 -X constant rate of elimination 
 Specify constant rate at which attributes are eliminated per invocation of the support vector machine. Default = 1.
 -Y percent rate of elimination 
 Specify the percentage rate at which attributes are eliminated per invocation of the support vector machine. This setting trumps the constant rate setting.  Default = 0 (percentage rate ignored).
 -Z threshold for percent elimination 
 Specify the threshold below which the percentage elimination method reverts to the constant elimination method.
 -C complexity parameter 
 Specify the value of C - the complexity parameter to be passed on to the support vector machine. 
 -P episilon 
 Sets the epsilon for round-off error. (default 1.0e-25)
 -T tolerance 
 Sets the tolerance parameter. (default 1.0e-10)

Version:
  
$Revision: 1.17.2.1 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz), Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.SVMAttributeEval';
UPDATE implementation set fulldescription='Implementation for evaluating machine learning models. 
 ------------------------------------------------------------------- 
 General options when evaluating a learning scheme from the command-line: 
 -t filename 
 Name of the file with the training data. (required) 
 -T filename 
 Name of the file with the test data. If missing a cross-validation  is performed. 
 -c index 
 Index of the class attribute (1, 2, ...; default: last). 
 -x number 
 The number of folds for the cross-validation (default: 10). 
 -s seed 
 Random number seed for the cross-validation (default: 1). 
 -m filename 
 The name of a file containing a cost matrix. 
 -l filename 
 Loads classifier from the given file. 
 -d filename 
 Saves classifier built from the training data into the given file. 
 -v 
 Outputs no statistics for the training data. 
 -o 
 Outputs statistics only, not the classifier. 
 -i 
 Outputs information-retrieval statistics per class. 
 -k 
 Outputs information-theoretic statistics. 
 -p range 
 Outputs predictions for test instances, along with the attributes in  the specified range (and nothing else). Use \'-p 0\' if no attributes are desired. 
 -r 
 Outputs cumulative margin distribution (and nothing else). 
 -g 
  Only for classifiers that implement "Graphable." Outputs the graph representation of the classifier (and nothing else). 
 ------------------------------------------------------------------- 
 Example usage as the main of a classifier (called FunkyClassifier): public static void main(String [] args) {   try {     Classifier scheme = new FunkyClassifier();     System.out.println(Evaluation.evaluateModel(scheme, args));   } catch (Exception e) {     System.err.println(e.getMessage());   } } ------------------------------------------------------------------ 
 Example usage from within an application: Instances trainInstances = ... instances got from somewhere Instances testInstances = ... instances got from somewhere Classifier scheme = ... scheme got from somewhere Evaluation evaluation = new Evaluation(trainInstances); evaluation.evaluateModel(scheme, testInstances); System.out.println(evaluation.toSummaryString());
Version:
  
$Revision: 1.53.2.6 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz), Len Trigg (trigg@cs.waikato.ac.nz)
' where name='weka.Evaluation';
UPDATE implementation set fulldescription='Calculates T-Test statistics on data stored in a set of instances.
 Valid options from the command-line are:
 -D num,num2... 
 The column numbers that uniquely specify a dataset. (default last) 
 -R num 
 The column number containing the run number. (default last) 
 -F num 
 The column number containing the fold number. (default none) 
 -S num 
 The significance level for T-Tests. (default 0.05) 
 -G num,num2... 
 The column numbers that uniquely specify one result generator (eg: scheme name plus options). (default last) 
 -L 
 Produce comparison tables in Latex table format 
 -csv 
 Produce comparison tables in csv format 

Version:
  
$Revision: 1.22.2.2 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)
' where name='weka.PairedTTester';
UPDATE implementation set fulldescription='BeanInfo class for the class value picker bean
Version:
  
$Revision: 1.2 $

Author:
  
Mark Hall
' where name='weka.ClassValuePickerBeanInfo';
UPDATE implementation set fulldescription='A bean that joins two streams of instances into one.
Version:
  
$Revision: 1.3 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.InstanceJoiner';
UPDATE implementation set fulldescription='Event encapsulating classifier performance data based on varying a threshold over the classifier\'s predicted probabilities
Version:
  
$Revision: 1.1 $

Author:
  
Mark Hall

See Also:

EventObject
, 
Serialized Form
' where name='weka.ThresholdDataEvent';
UPDATE implementation set fulldescription='Implementation for doing classification using regression methods. For more information, see 
 E. Frank, Y. Wang, S. Inglis, G. Holmes, and I.H. Witten (1998) "Using model trees for classification", 
Machine Learning
, Vol.32, No.1, pp. 63-76.
 Valid options are:
 -W classname 
 Specify the full class name of a numeric predictor as the basis for  the classifier (required).

Version:
  
$Revision: 1.20 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz), Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.ClassificationViaRegression';
UPDATE implementation set fulldescription='BeanInfo class for the class assigner bean
Version:
  
$Revision: 1.1 $

Author:
  
Mark Hall
' where name='weka.ClassAssignerBeanInfo';
UPDATE implementation set fulldescription='Cluster data generator designed for the BIRCH System Dataset is generated with instances in K clusters. Instances are 2-d data points. Each cluster is characterized by the number of data points in it its radius and its center. The location of the cluster centers is determined by the pattern parameter. Three patterns are currently supported grid, sine and random. todo: (out of: BIRCH: An Efficient Data Clustering Method for Very Large Databases; T. Zhang, R. Ramkrishnan, M. Livny; 1996 ACM) Class to generate data randomly by producing a decision list. The decision list consists of rules. Instances are generated randomly one by one. If decision list fails to classify the current instance, a new rule according to this current instance is generated and added to the decision list.
 The option -V switches on voting, which means that at the end of the generation all instances are reclassified to the class value that is supported by the most rules.
 This data generator can generate \'boolean\' attributes (= nominal with the values {true, false}) and numeric attributes. The rules can be \'A\' or \'NOT A\' for boolean values and \'B < random_value\' or \'B >= random_value\' for numeric values.
  Valid options are:
 -G 
 The pattern for instance generation is grid.
 This flag cannot be used at the same time as flag I. The pattern is random, if neither flag G nor flag I is set.
 -I 
 The pattern for instance generation is sine.
 This flag cannot be used at the same time as flag G. The pattern is random, if neither flag G nor flag I is set.
 -N num .. num 
 The range of the number of instances in each cluster (default 1..50).
 Lower number must be between 0 and 2500, upper number must be between 50 and 2500.
 -R num .. num 
 The range of the radius of the clusters (default 0.1 .. SQRT(2)).
 Lower number must be between 0 and SQRT(2), upper number must be between
 SQRT(2) and SQRT(32).
 -M num 
 Distance multiplier, only used if pattern is grid (default 4). 
 -C num 
 Number of cycles, only used if pattern is sine (default 4). 
 -O 
 Flag for input order is ordered. If flag is not set then input order is randomized.
 -P num
 Noise rate in percent. Can be between 0% and 30% (default 0%).
 (Remark: The original algorithm only allows noise up to 10%.)
 -S seed 
 Random number seed for random function used (default 1). 

Version:
  
$Revision: 1.3 $

Author:
  
Gabi Schmidberger (gabi@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.BIRCHCluster';
UPDATE implementation set fulldescription='Abstract attribute transformer. Transforms the dataset.
Version:
  
$Revision: 1.6 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)
' where name='weka.AttributeTransformer';
UPDATE implementation set fulldescription='Interface for objects that display log and display information on running tasks.
Version:
  
$Revision: 1.4 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)
' where name='weka.TaskLogger';
UPDATE implementation set fulldescription='Abstract class for objects that can provide some kind of evaluation for classifier, clusterers etc.
Since:
  
1.0

Version:
  
$Revision: 1.2.2.1 $

Author:
  
Mark Hall

See Also:

JPanel
, 
Visible
, 
Serializable
, 
Serialized Form
' where name='weka.AbstractEvaluator';
UPDATE implementation set fulldescription='Hashtable collision list.
See Also:

Serialized Form
' where name='weka.KStarCache.TableEntry';
UPDATE implementation set fulldescription='
Version:
  
$Revision: 1.2.2.1 $

Author:
  
Yong Wang

See Also:

Serialized Form
' where name='weka.FlexibleDecimalFormat';
UPDATE implementation set fulldescription='Event for graphs
Version:
  
$Revision: 1.3 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.GraphEvent';
UPDATE implementation set fulldescription='BeanInfo class for the Classifier wrapper bean
Version:
  
$Revision: 1.1 $

Author:
  
Mark Hall
' where name='weka.ClassifierBeanInfo';
UPDATE implementation set fulldescription='CFS attribute subset evaluator. For more information see: 
 Hall, M. A. (1998). Correlation-based Feature Subset Selection for Machine  Learning. Thesis submitted in partial fulfilment of the requirements of the degree of Doctor of Philosophy at the University of Waikato. 
 Valid options are: -M 
 Treat missing values as a separate value. 
 -L 
 Don\'t include locally predictive attributes. 

Version:
  
$Revision: 6134 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.CfsSubsetEval';
UPDATE implementation set fulldescription='Abstract utility class for handling settings common to randomizable meta classifiers that build an ensemble from a single base learner.
Version:
  
$Revision: 1.2 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.RandomizableIteratedSingleClassifierEnhancer';
UPDATE implementation set fulldescription='Implementation for generating an unpruned or a pruned C4.5 decision tree. For more information, see
 Ross Quinlan (1993). 
C4.5: Programs for Machine Learning
,  Morgan Kaufmann Publishers, San Mateo, CA. 
 Valid options are: 
 -U 
 Use unpruned tree.
 -C confidence 
 Set confidence threshold for pruning. (Default: 0.25) 
 -M number 
 Set minimum number of instances per leaf. (Default: 2) 
 -R 
 Use reduced error pruning. No subtree raising is performed. 
 -N number 
 Set number of folds for reduced error pruning. One fold is used as the pruning set. (Default: 3) 
 -B 
 Use binary splits for nominal attributes. 
 -S 
 Don\'t perform subtree raising. 
 -L 
 Do not clean up after the tree has been built. 
 -A 
 If set, Laplace smoothing is used for predicted probabilites. 
 -Q 
 The seed for reduced-error pruning. 

Version:
  
$Revision: 1.2 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.J48';
UPDATE implementation set fulldescription='Exception that is raised by an object that is unable to process  data with missing values.
Version:
  
$Revision: 1.1 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.NoSupportForMissingValuesException';
UPDATE implementation set fulldescription='Interface to something that can accept VisualizableErrorEvents
Since:
  
1.0

Version:
  
$Revision: 1.1.2.2 $

Author:
  
Mark Hall

See Also:

EventListener
' where name='weka.VisualizableErrorListener';
UPDATE implementation set fulldescription='Bean that encapsulates weka.gui.visualize.VisualizePanel
Version:
  
$Revision: 1.9 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.DataVisualizer';
UPDATE implementation set fulldescription='A class for transforming options listed in XML to a regular WEKA command line string.

Version:
  
$Revision: 1.1.2.2 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)
' where name='weka.XMLOptions';
UPDATE implementation set fulldescription='Reads from a database. Can read a database in batch or incremental mode. In inremental mode MySQL and HSQLDB are supported. For all other DBMS set a pseudoincremental mode is used: In pseudo incremental mode the instances are read into main memory all at once and then incrementally provided to the user. For incremental loading the rows in the database table have to be ordered uniquely. The reason for this is that every time only a single row is fetched by extending the user" query by a LIMIT clause. If this extension is impossible instances will be loaded pseudoincrementally. To ensure that every row is fetched exaclty once, they have to ordered. Therefore a (primary) key is necessary.This approach is chosen, instead of using JDBC driver facilities, because the latter one differ betweeen different drivers. If you use the DatabaseSaver and save instances by generating automatically a primary key (its name is defined in DtabaseUtils), this primary key will be used for ordering but will not be part of the output. The user defined SQL query to extract the instances should not contain LIMIT and ORDER BY clauses (see -Q option).  In addition, for incremental loading,  you can define in the DatabaseUtils file how many distinct values a nominal attribute is allowed to have. If this number is exceeded, the column will become a string attribute.   In batch mode no string attributes will be created. Available options are:  -Q the query to specify which tuples to load
 The query must have the form: SELECT *|
 FROM 
 [WHERE} (default: SELECT * FROM Results0).
 -P comma separted list of columns that are a unqiue key 
 Only needed for incremental loading, if it cannot be detected automatically
 -I 
 Sets incremental loading
Version:
  
$Revision: 7047 $

Author:
  
Stefan Mutter (mutter@cs.waikato.ac.nz)

See Also:

Loader
, 
Serialized Form
' where name='weka.DatabaseLoader';
UPDATE implementation set fulldescription='This class is used to represent a node in the neuralnet.
Version:
  
$Revision: 5405 $

Author:
  
Malcolm Ware (mfw4@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.NeuralNode';
UPDATE implementation set fulldescription='This is an event which is fired by a LayoutEngine once a LayoutEngine finishes laying out the graph, so that the Visualizer can repaint the screen to show the changes.
Version:
  
$Revision: 1.2.2.1 $ - 24 Apr 2003 - Initial version (Ashraf M. Kibriya)

Author:
  
Ashraf M. Kibriya (amk14@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.LayoutCompleteEvent';
UPDATE implementation set fulldescription='Abstract attribute subset evaluator capable of evaluating subsets with respect to a data set that is distinct from that used to initialize/ train the subset evaluator.
Version:
  
$Revision: 1.3 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.HoldOutSubsetEvaluator';
UPDATE implementation set fulldescription='Implements John C. Platt\'s sequential minimal optimization algorithm for training a support vector classifier using polynomial or RBF kernels.  This implementation globally replaces all missing values and transforms nominal attributes into binary ones. It also normalizes all attributes by default. (Note that the coefficients in the output are based on the normalized/standardized data, not the original data.) Multi-class problems are solved using pairwise classification. To obtain proper probability estimates, use the option that fits logistic regression models to the outputs of the support vector machine. In the multi-class case the predicted probabilities will be coupled using Hastie and Tibshirani\'s pairwise coupling method. Note: for improved speed standardization should be turned off when operating on SparseInstances.
 For more information on the SMO algorithm, see
 J. Platt (1998). 
Fast Training of Support Vector Machines using Sequential Minimal Optimization
. Advances in Kernel Methods - Support Vector Learning, B. Schoelkopf, C. Burges, and A. Smola, eds., MIT Press. 
 S.S. Keerthi, S.K. Shevade, C. Bhattacharyya, K.R.K. Murthy,  
Improvements to Platt\'s SMO Algorithm for SVM Classifier Design
.  Neural Computation, 13(3), pp 637-649, 2001. 
 Valid options are:
 -C num 
 The complexity constant C. (default 1)
 -E num 
 The exponent for the polynomial kernel. (default 1)
 -G num 
 Gamma for the RBF kernel. (default 0.01)
 -N 
 
 Whether to 0=normalize/1=standardize/2=neither. (default 0=normalize)
 -F 
 Feature-space normalization (only for non-linear polynomial kernels). 
 -O 
 Use lower-order terms (only for non-linear polynomial kernels). 
 -R 
 Use the RBF kernel. (default poly)
 -A num 
 Sets the size of the kernel cache. Should be a prime number.  (default 250007, use 0 for full cache) 
 -L num 
 Sets the tolerance parameter. (default 1.0e-3)
 -P num 
 Sets the epsilon for round-off error. (default 1.0e-12)
 -M 
 Fit logistic models to SVM outputs.
 -V num 
 Number of folds for cross-validation used to generate data for logistic models. (default -1, use training data) -W num 
 Random number seed for cross-validation. (default 1)
Version:
  
$Revision: 1.53.2.3 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz), Shane Legg (shane@intelligenesis.net) (sparse vector code), Stuart Inglis (stuart@reeltwo.com) (sparse vector code)

See Also:

Serialized Form
' where name='weka.SMO';
UPDATE implementation set fulldescription='Interface to something that can generate new instances based on a set of input instances
Since:
  
1.0

Version:
  
$Revision: 1.3 $

Author:
  
Mark Hall
' where name='weka.DataGenerator';
UPDATE implementation set fulldescription='The NaiveBayes class generates a fixed Bayes network structure with arrows from the class variable to each of the attribute variables.
Version:
  
$Revision: 1.3 $

Author:
  
Remco Bouckaert

See Also:

Serialized Form
' where name='weka.NaiveBayes';
UPDATE implementation set fulldescription='Bean info class for AbstractDataSource. All beans that extend AbstractDataSource might want to extend this class
Since:
  
1.0

Version:
  
$Revision: 1.1.2.1 $

Author:
  
Mark Hall

See Also:

SimpleBeanInfo
' where name='weka.AbstractDataSourceBeanInfo';
UPDATE implementation set fulldescription='BeanInfo class for the Clusterer wrapper bean
Version:
  
$Revision: 1.1 $

Author:
  
Stefan Mutter
' where name='weka.ClustererBeanInfo';
UPDATE implementation set fulldescription='Implements StackingC (more efficient version of stacking). For more information, see
  Seewald A.K.: 
How to Make Stacking Better and Faster While Also Taking Care  of an Unknown Weakness
, in Sammut C., Hoffmann A. (eds.), Proceedings of the  Nineteenth International Conference on Machine Learning (ICML 2002), Morgan  Kaufmann Publishers, pp.554-561, 2002.
 Valid options are:
 -X num_folds 
 The number of folds for the cross-validation (default 10).
 -S seed 
 Random number seed (default 1).
 -B classifierstring 
 Classifierstring should contain the full class name of a base scheme followed by options to the classifier. (required, option should be used once for each classifier).
 -M classifierstring 
 Classifierstring for the meta classifier. Same format as for base classifiers. Has to be a numeric prediction scheme, defaults to Linear Regression as in the original paper.

Version:
  
$Revision: 1.8 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz), Alexander K. Seewald (alex@seewald.at)

See Also:

Serialized Form
' where name='weka.StackingC';
UPDATE implementation set fulldescription='LearningRateResultProducer takes the results from a ResultProducer and submits the average to the result listener. For non-numeric result fields, the first value is used.
Version:
  
$Revision: 5594 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.LearningRateResultProducer';
UPDATE implementation set fulldescription='This can be used by the  neuralnode to perform all it\'s computations (as a Linear unit).
Version:
  
$Revision: 1.4 $

Author:
  
Malcolm Ware (mfw4@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.LinearUnit';
UPDATE implementation set fulldescription='Implementation for building and using a simple Naive Bayes classifier. Numeric attributes are modelled by a normal distribution. For more information, see
 Richard Duda and Peter Hart (1973).
Pattern Classification and Scene Analysis
. Wiley, New York.
Version:
  
$Revision: 1.13.2.2 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.NaiveBayesSimple';
UPDATE implementation set fulldescription='Implementation for storing an (class) association rule. The premise and the consequence are stored each as separate item sets. For every rule their expected predictive accuracy and the time of generation is stored. These two measures allow to introduce a sort order for rules.
Version:
  
$Revision: 1.1.2.1 $

Author:
  
Stefan Mutter

See Also:

Serialized Form
' where name='weka.RuleItem';
UPDATE implementation set fulldescription='
Version:
  
$Revision: 1.2.2.1 $

Author:
  
Yong Wang
' where name='weka.DoubleVector';
UPDATE implementation set fulldescription='Implementation for handling a tree structure used for classification.
Version:
  
$Revision: 1.17.2.1 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.ClassifierTree';
UPDATE implementation set fulldescription='Holds all the necessary configuration information for a standard type experiment. This object is able to be serialized for storage on disk.
Version:
  
$Revision: 1.22.2.3 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.Experiment';
UPDATE implementation set fulldescription='Interface to something that can be matched with tree matching algorithms.
Version:
  
$Revision: 1.5 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)
' where name='weka.Matchable';
UPDATE implementation set fulldescription='Implementation for handling an instance. All values (numeric, date, nominal, or string) are internally stored as floating-point numbers. If an attribute is nominal (or a string), the stored value is the index of the corresponding nominal (or string) value in the attribute\'s definition. We have chosen this approach in favor of a more elegant object-oriented approach because it is much faster. 
 Typical usage (code from the main() method of this class): 
 ... 
 // Create empty instance with three attribute values 
 Instance inst = new Instance(3); 
 // Set instance\'s values for the attributes "length", "weight", and "position"
 inst.setValue(length, 5.3); 
 inst.setValue(weight, 300); 
 inst.setValue(position, "first"); 
 // Set instance\'s dataset to be the dataset "race" 
 inst.setDataset(race); 
 // Print the instance 
 System.out.println("The instance: " + inst); 
 ... 
 All methods that change an instance are safe, ie. a change of an instance does not affect any other instances. All methods that change an instance\'s attribute values clone the attribute value vector before it is changed. If your application heavily modifies instance values, it may be faster to create a new instance from scratch.
Version:
  
$Revision: 1.19.2.2 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.Instance';
UPDATE implementation set fulldescription='Implementation for bagging a classifier. For more information, see
 Leo Breiman (1996). 
Bagging predictors
. Machine Learning, 24(2):123-140. 
 Valid options are:
 -W classname 
 Specify the full class name of a weak classifier as the basis for  bagging (required).
 -I num 
 Set the number of bagging iterations (default 10). 
 -S seed 
 Random number seed for resampling (default 1). 
 -P num 
 Size of each bag, as a percentage of the training size (default 100). 
 -O 
 Compute out of bag error. 
 Options after -- are passed to the designated classifier.

Version:
  
$Revision: 6503 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz), Len Trigg (len@reeltwo.com), Richard Kirkby (rkirkby@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.Bagging';
UPDATE implementation set fulldescription='This Logger just sends messages to System.err.
Version:
  
$Revision: 1.3 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)
' where name='weka.SysErrLog';
UPDATE implementation set fulldescription='This class performs Bias-Variance decomposion on any classifier using the sub-sampled cross-validation procedure as specified in:
 Geoffrey I. Webb & Paul Conilione (2002), 
 Estimating bias and variance from data 
, School of Computer Science and Software Engineering, Monash University, Australia 
 The Kohavi and Wolpert definition of bias and variance is specified in:
 R. Kohavi & D. Wolpert (1996), 
Bias plus variance decomposition for zero-one loss functions
, in Proc. of the Thirteenth International Machine Learning Conference (ICML96) download postscript
.
 The Webb definition of bias and variance is specified in:
 Geoffrey I. Webb (2000), 
 MultiBoosting: A Technique for Combining Boosting and Wagging
, Machine Learning, 40(2), pages 159-196
 Valid options are:
 -c num 
 Specify the index of the class attribute (default last).
 -D 
 Turn on debugging output.
 -l num 
 Set the number times each instance is to be classified (default 10). 
 -p num 
 Set the proportion of instances that are the same between any two training sets. Training set size/(Dataset size - 1) < num < 1.0 (Default is Training set size/(Dataset size - 1) ) 
 -s num 
 Set the seed for the dataset randomisation (default 1). 
 -t filename 
 Set the arff file to use for the decomposition (required).
 -T num 
 Set the size of the training sets. Must be greater than 0 and less size of the dataset. (default half of dataset size) 
 -W classname 
 Specify the full class name of a learner to perform the decomposition on (required).
 Options after -- are passed to the designated sub-learner. 

Version:
  
$Revision: 1.2.2.1 $

Author:
  
Paul Conilione (paulc4321@yahoo.com.au)
' where name='weka.BVDecomposeSegCVSub';
UPDATE implementation set fulldescription='Abstract utility class for handling settings common to randomizable meta classifiers that build an ensemble from a single base learner.
Version:
  
$Revision: 1.2 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.RandomizableSingleClassifierEnhancer';
UPDATE implementation set fulldescription='Implementation for editing CostMatrix objects. Brings up a custom editing panel with which the user can edit the matrix interactively, as well as save load cost matrices from files.
Version:
  
$Revision: 1.7 $

Author:
  
Richard Kirkby (rkirkby@cs.waikato.ac.nz)
' where name='weka.CostMatrixEditor';
UPDATE implementation set fulldescription='Bean info class for the test set maker bean. Essentially just hides gui related properties
Version:
  
$Revision: 1.1 $

Author:
  
Mark Hall
' where name='weka.TestSetMakerBeanInfo';
UPDATE implementation set fulldescription='Creates a very simple command line for invoking the main method of classes. System.out and System.err are redirected to an output area. Features a simple command history -- use up and down arrows to move through previous commmands. This gui uses only AWT (i.e. no Swing).
Version:
  
$Revision: 1.6.2.2 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.SimpleCLI';
UPDATE implementation set fulldescription='A dialog for setting various output format parameters.
Version:
  
$Revision: 1.1.2.1 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)

See Also:

Serialized Form
' where name='weka.OutputFormatDialog';
UPDATE implementation set fulldescription='Extension of MouseAdapter that implements Serializable.
See Also:

Serialized Form
' where name='weka.ResultHistoryPanel.RMouseAdapter';
UPDATE implementation set fulldescription='An instance filter that copies a range of attributes in the dataset. This is used in conjunction with other filters that overwrite attribute during the course of their operation -- this filter allows the original attributes to be kept as well as the new attributes.
 Valid filter-specific options are:
 -R index1,index2-index4,...
 Specify list of columns to copy. First and last are valid indexes. Attribute copies are placed at the end of the dataset. (default none)
 -V
 Invert matching sense (i.e. copy all non-specified columns)

Version:
  
$Revision: 6995 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.Copy';
UPDATE implementation set fulldescription='Interface to something that is capable of being a source for data -  either batch or incremental data
Since:
  
1.0

Version:
  
$Revision: 1.1 $

Author:
  
Mark Hall
' where name='weka.DataSource';
UPDATE implementation set fulldescription='NNge classifier.  Nearest neighbor like algorithm using non-nested generalized exemplars. For more information, see 
 Brent Martin, (1995) "Instance-Based learning : Nearest Neighbor With Generalization", Master Thesis, University of Waikato, Hamilton, New Zealand Sylvain Roy (2002) "Nearest Neighbor With Generalization", Unpublished, University of Canterbury, Christchurch, New Zealand Valid options are:
 -I num 
 Set the number of folder to use in the computing of the mutual information (default 5) 
 -G num 
 Set the number of attempts of generalisation (default 5) 

Version:
  
$Revision: 1.2.2.1 $

Author:
  
Brent Martin (bim20@cosc.canterbury.ac.nz), Sylvain Roy (sro33@student.canterbury.ac.nz)

See Also:

Serialized Form
' where name='weka.NNge';
UPDATE implementation set fulldescription='BeanVisual encapsulates icons and label for a given bean. Has methods to load icons, set label text and toggle between static and animated versions of a bean\'s icon.
Since:
  
1.0

Version:
  
$Revision: 1.4.2.3 $

Author:
  
Mark Hall

See Also:

JPanel
, 
Serializable
, 
Serialized Form
' where name='weka.BeanVisual';
UPDATE implementation set fulldescription='M5Base. Implements base routines for generating M5 Model trees and rules. 
 The original algorithm M5 was invented by Quinlan: 
 Quinlan J. R. (1992). Learning with continuous classes. Proceedings of the Australian Joint Conference on Artificial Intelligence. 343--348. World Scientific, Singapore. 
 Yong Wang made improvements and created M5\': 
 Wang, Y and Witten, I. H. (1997). Induction of model trees for predicting continuous classes. Proceedings of the poster papers of the European Conference on Machine Learning. University of Economics, Faculty of Informatics and Statistics, Prague. 
 Valid options are:
 -U 
 Use unsmoothed predictions. 
 -R 
 Build regression tree/rule rather than model tree/rule
Version:
  
$Revision: 6239 $

See Also:

Serialized Form
' where name='weka.M5Base';
UPDATE implementation set fulldescription='Writes to a destination in the format used by the C4.5 slgorithm. The output are two files: *.names, *.data Valid options: -i input arff file 
 The input filw in ARFF format. 
 -o the output file 
 The output file. The prefix of the output file is sufficient.
 -c class index 
 The index of the class attribute. first and last are valid as well (default: last). 

Version:
  
$Revision: 1.2 $

Author:
  
Stefan Mutter (mutter@cs.waikato.ac.nz)

See Also:

Saver
, 
Serialized Form
' where name='weka.C45Saver';
UPDATE implementation set fulldescription='Bean info class for the data visualizer
Version:
  
$Revision: 1.1 $

Author:
  
Mark Hall
' where name='weka.DataVisualizerBeanInfo';
UPDATE implementation set fulldescription='This panel controls the running of an experiment.
Version:
  
$Revision: 1.18 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.RunPanel';
UPDATE implementation set fulldescription='A specialized JTable for the Arff-Viewer.
Version:
  
$Revision: 1.2.2.2 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)

See Also:

Serialized Form
' where name='weka.ArffTable';
UPDATE implementation set fulldescription='CSVResultListener outputs the received results in csv format to a Writer
Version:
  
$Revision: 1.10 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.CSVResultListener';
UPDATE implementation set fulldescription='
Version:
  
$Revision: 1.2.2.1 $

Author:
  
Peter A. Flach, Nicolas Lachiche

See Also:

Serialized Form
' where name='weka.SimpleLinkedList';
UPDATE implementation set fulldescription='This class serializes and deserializes an Experiment instance to and fro XML.
 It omits the 
options
 from the Experiment, since these are handled by the get/set-methods. For the 
Classifier
 class with all its  derivative classes it stores only 
debug
 and 
options
. For 
SplitEvaluator
 and 
ResultProducer
 only the options are retrieved. The 
PropertyNode
 is done manually since it has no get/set-methods for its public fields.
 Since there\'s no read-method for 
m_ClassFirst
 we always save it as 
false
.
Version:
  
$Revision: 1.1.2.4 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)

See Also:

Experiment.m_ClassFirst

' where name='weka.XMLExperiment';
UPDATE implementation set fulldescription='Generates points illustrating the prediction margin. The margin is defined as the difference between the probability predicted for the actual class and the highest probability predicted for the other classes. One hypothesis as to the good performance of boosting algorithms is that they increaes the margins on the training data and this gives better performance on test data.
Version:
  
$Revision: 1.9 $

Author:
  
Len Trigg (len@reeltwo.com)
' where name='weka.MarginCurve';
UPDATE implementation set fulldescription='Implementation implementing the rule generation procedure of the predictive apriori algorithm. Reference: T. Scheffer (2001). 
Finding Association Rules That Trade Support  Optimally against Confidence
. Proc of the 5th European Conf. on Principles and Practice of Knowledge Discovery in Databases (PKDD\'01), pp. 424-435. Freiburg, Germany: Springer-Verlag. 
 The implementation follows the paper expect for adding a rule to the output of the 
n
 best rules. A rule is added if: the expected predictive accuracy of this rule is among the 
n
 best and it is  not subsumed by a rule with at least the same expected predictive accuracy (out of an unpublished manuscript from T. Scheffer).
Version:
  
$Revision: 1.1 $

Author:
  
Stefan Mutter (mutter@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.RuleGeneration';
UPDATE implementation set fulldescription='Implementation implementing some simple random variates generator.
Version:
  
$Revision: 1.1 $

Author:
  
Xin Xu (xx5@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.RandomVariates';
UPDATE implementation set fulldescription='M5P. Implements routines for generating M5 model trees.
 The original algorithm M5 was invented by Quinlan: 
 Quinlan J. R. (1992). Learning with continuous classes. Proceedings of the Australian Joint Conference on Artificial Intelligence. 343--348. World Scientific, Singapore. 
 Yong Wang made improvements and created M5\': 
 Wang, Y and Witten, I. H. (1997). Induction of model trees for predicting continuous classes. Proceedings of the poster papers of the European Conference on Machine Learning. University of Economics, Faculty of Informatics and Statistics, Prague. 
 Valid options are:
 -U 
 Use unsmoothed predictions. 

Version:
  
$Revision: 1.1.2.1 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.M5P';
UPDATE implementation set fulldescription='Generates a single m5 tree or rule
Version:
  
$Revision: 6239 $

Author:
  
Mark Hall

See Also:

Serialized Form
' where name='weka.Rule';
UPDATE implementation set fulldescription='Implementation for selecting a NB tree split.
Version:
  
$Revision: 1.2 $

Author:
  
Mark Hall (mhall@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.NBTreeModelSelection';
UPDATE implementation set fulldescription='A filter that removes attributes of a given type.
 Valid filter-specific options are: 
 -T type 
 Attribute type to delete. Options are "nominal", "numeric", "string" and "date". (default "string")
 -V
 Invert matching sense (i.e. only keep specified columns)

Version:
  
$Revision: 1.4 $

Author:
  
Richard Kirkby (rkirkby@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.RemoveType';
UPDATE implementation set fulldescription='Interface implemented by classes that wish to recieve user selection events from a tree displayer.
Version:
  
$Revision: 1.4 $

Author:
  
Malcolm Ware (mfw4@cs.waikato.ac.nz)
' where name='weka.TreeDisplayListener';
UPDATE implementation set fulldescription='A helper class for some common tasks with Dialogs, Icons, etc.
Version:
  
$Revision: 1.1.2.1 $

Author:
  
FracPete (fracpete at waikato dot ac dot nz)
' where name='weka.ComponentHelper';
UPDATE implementation set fulldescription='Generates for each run, carries out an n-fold cross-validation, using the set SplitEvaluator to generate some results. If the class attribute is nominal, the dataset is stratified. Results for each fold are generated, so you may wish to use this in addition with an AveragingResultProducer to obtain averages for each run.
Version:
  
$Revision: 1.14 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.CrossValidationResultProducer';
UPDATE implementation set fulldescription='Implementation for handling discrete functions. 
 A discrete function here is one that takes non-zero values over a finite set of points. 

Version:
  
$Revision: 1.1 $

Author:
  
Yong Wang (yongwang@cs.waikato.ac.nz)
' where name='weka.DiscreteFunction';
UPDATE implementation set fulldescription='Convert the results of a database query into instances. The jdbc driver and database to be used default to "jdbc.idbDriver" and "jdbc:idb=experiments.prp". These may be changed by creating a java properties file called DatabaseUtils.props in user.home or the current directory. eg:
 jdbcDriver=jdbc.idbDriver jdbcURL=jdbc:idb=experiments.prp Command line use just outputs the instances to System.out.
Version:
  
$Revision: 7046 $

Author:
  
Len Trigg (trigg@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.InstanceQuery';
UPDATE implementation set fulldescription='Merges two values of a nominal attribute.
 Valid filter-specific options are: 
 -C col 
 The column containing the values to be merged. (default last)
 -F index 
 Index of the first value (default first).
 -S index 
 Index of the second value (default last).

Version:
  
$Revision: 5863 $

Author:
  
Eibe Frank (eibe@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.MergeTwoValues';
UPDATE implementation set fulldescription='Implementation representing a two-way split on a numeric attribute, of the form: either \'is 
= some_value\'.
Version:
  
$Revision: 1.2 $

Author:
  
Richard Kirkby (rkirkby@cs.waikato.ac.nz)

See Also:

Serialized Form
' where name='weka.TwoWayNumericSplit';
