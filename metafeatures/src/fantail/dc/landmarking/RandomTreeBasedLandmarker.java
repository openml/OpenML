package fantail.dc.landmarking;

import fantail.dc.*;
import java.util.Arrays;
import weka.core.Instances;

public class RandomTreeBasedLandmarker extends Characterizer implements Randomizable {

    private int m_NumFolds = 2;
    private int m_K = 0;
    private int m_Seed = 1;

    @Override
    public void setSeed(int seed) {
        m_Seed = seed;
    }

    public void setK(int k) {
        m_K = k;
    }

    protected final String[] ids = new String[]{"RandomTreeDepth1AUC_K=" + m_K,
        "RandomTreeDepth2AUC_K=" + m_K,
        "RandomTreeDepth3AUC_K=" + m_K};

    public String[] getIDs() {
        return ids;
    }

    public DCValue[] characterize(Instances data) {

        int numFolds = m_NumFolds;

        double score1 = 0.5;
        double score2 = 0.5;
        double score3 = 0.5;

        weka.classifiers.trees.RandomTree cls = new weka.classifiers.trees.RandomTree();
        cls.setSeed(m_Seed);
        cls.setMaxDepth(1);

        try {
            //ds.buildClassifier(data);
            weka.classifiers.Evaluation eval = new weka.classifiers.Evaluation(data);
            eval.crossValidateModel(cls, data, numFolds, new java.util.Random(1));

            score1 = eval.weightedAreaUnderROC();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //
        cls = new weka.classifiers.trees.RandomTree();
        cls.setSeed(m_Seed);
        cls.setMaxDepth(2);

        try {

            weka.classifiers.Evaluation eval = new weka.classifiers.Evaluation(data);
            eval.crossValidateModel(cls, data, numFolds, new java.util.Random(1));

            score2 = eval.weightedAreaUnderROC();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //
        cls = new weka.classifiers.trees.RandomTree();
        cls.setSeed(m_Seed);
        cls.setMaxDepth(3);

        try {

            weka.classifiers.Evaluation eval = new weka.classifiers.Evaluation(data);
            eval.crossValidateModel(cls, data, numFolds, new java.util.Random(1));

            score3 = eval.weightedAreaUnderROC();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new DCValue[]{
            new DCValue(ids[0], score1),
            new DCValue(ids[1], score2),
            new DCValue(ids[2], score3)};
    }
}
