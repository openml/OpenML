package fantail.dc.landmarking;

import fantail.dc.*;
import java.util.Arrays;
import weka.core.Instances;

public class SimpleLandmarkers extends Characterizer implements NFoldCrossValidationBased {

    private int m_NumFolds = 2;

    @Override
    public void setNumFolds(int n) {
        m_NumFolds = n;
    }

    protected final String[] ids = new String[]{"DecisionStumpErrRate", "DecisionStumpAUC", "NBErrRate", "NBAUC",
        "DecisionStumpKappa", "NBKappa"};

    public String[] getIDs() {
        return ids;
    }

    public DCValue[] characterize(Instances data) {

        int numFolds = m_NumFolds;

        double score1 = 0.5;
        double score2 = 0.5;

        double score5 = 0.5;
        double score6 = 0.5;

        double score3 = 0.5;
        double score4 = 0.5;

        weka.classifiers.trees.DecisionStump ds = new weka.classifiers.trees.DecisionStump();
        try {

            weka.classifiers.Evaluation eval = new weka.classifiers.Evaluation(data);
            eval.crossValidateModel(ds, data, numFolds, new java.util.Random(1));

            score1 = eval.pctIncorrect();
            score2 = eval.weightedAreaUnderROC();
            score3 = eval.kappa();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            weka.classifiers.bayes.NaiveBayes nb = new weka.classifiers.bayes.NaiveBayes();

            weka.classifiers.Evaluation eval = new weka.classifiers.Evaluation(data);
            eval.crossValidateModel(nb, data, numFolds, new java.util.Random(1));

            score5 = eval.pctIncorrect();
            score6 = eval.weightedAreaUnderROC();
            score4 = eval.kappa();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new DCValue[]{
            new DCValue(ids[0], score1),
            new DCValue(ids[1], score2),
            new DCValue(ids[2], score5),
            new DCValue(ids[3], score6),
            new DCValue(ids[3], score3),
            new DCValue(ids[3], score4)};
    }
}
