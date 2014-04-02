package fantail.dc.landmarking;

import fantail.dc.*;
import java.util.Arrays;
import weka.core.Instances;

public class REPTreeBasedLandmarker extends Characterizer implements NFoldCrossValidationBased {

    private int m_NumFolds = 2;

    @Override
    public void setNumFolds(int n) {
        m_NumFolds = n;
    }

    protected final String[] ids = new String[]{"REPTreeDepth1ErrRate", "REPTreeDepth1AUC",
        "REPTreeDepth2ErrRate", "REPTreeDepth2AUC",
        "REPTreeDepth3ErrRate", "REPTreeDepth3AUC", "REPTreeDepth1Kappa", "REPTreeDepth2Kappa", "REPTreeDepth3Kappa"};

    public String[] getIDs() {
        return ids;
    }

    public DCValue[] characterize(Instances data) {

        int numFolds = m_NumFolds;

        double score1 = 0.5;
        double score2 = 0.5;
        //double score3 = 0.5;

        double score3 = 0.5;
        double score4 = 0.5;
        //double score3 = 0.5;

        double score5 = 0.5;
        double score6 = 0.5;

        double score7 = 0.5;
        double score8 = 0.5;
        double score9 = 0.5;

        weka.classifiers.trees.REPTree cls = new weka.classifiers.trees.REPTree();
        cls.setMaxDepth(1);

        try {

            weka.classifiers.Evaluation eval = new weka.classifiers.Evaluation(data);
            eval.crossValidateModel(cls, data, numFolds, new java.util.Random(1));

            score1 = eval.pctIncorrect();
            score2 = eval.weightedAreaUnderROC();

            score7 = eval.kappa();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //
        cls = new weka.classifiers.trees.REPTree();
        cls.setMaxDepth(2);

        try {

            weka.classifiers.Evaluation eval = new weka.classifiers.Evaluation(data);
            eval.crossValidateModel(cls, data, numFolds, new java.util.Random(1));

            score3 = eval.pctIncorrect();
            score4 = eval.weightedAreaUnderROC();

            score8 = eval.kappa();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //
        cls = new weka.classifiers.trees.REPTree();
        cls.setMaxDepth(3);

        try {

            weka.classifiers.Evaluation eval = new weka.classifiers.Evaluation(data);
            eval.crossValidateModel(cls, data, numFolds, new java.util.Random(1));

            score5 = eval.pctIncorrect();
            score6 = eval.weightedAreaUnderROC();

            score9 = eval.kappa();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new DCValue[]{
            new DCValue(ids[0], score1),
            new DCValue(ids[1], score2),
            new DCValue(ids[2], score3),
            new DCValue(ids[3], score4),
            new DCValue(ids[4], score5),
            new DCValue(ids[5], score6),
            new DCValue(ids[6], score7),
            new DCValue(ids[7], score8),
            new DCValue(ids[8], score9)};
    }
}
