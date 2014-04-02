package fantail.dc.landmarking;

import fantail.dc.*;
import weka.core.Instances;

public class J48BasedLandmarker extends Characterizer implements NFoldCrossValidationBased {

    private int m_NumFolds = 2;

    @Override
    public void setNumFolds(int n) {
        m_NumFolds = n;
    }

    protected final String[] ids = new String[]{"J48.00001.ErrRate", "J48.00001.AUC",
        "J48.0001.ErrRate", "J48.0001.AUC",
        "J48.001.ErrRate", "J48.001.AUC",
        "J48.00001.kappa", "J48.0001.kappa", "J48.001.kappa"};

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

        weka.classifiers.trees.J48 cls = new weka.classifiers.trees.J48();
        cls.setConfidenceFactor(0.00001f);

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
        cls = new weka.classifiers.trees.J48();
        cls.setConfidenceFactor(0.0001f);

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
        cls = new weka.classifiers.trees.J48();
        cls.setConfidenceFactor(0.001f);

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
