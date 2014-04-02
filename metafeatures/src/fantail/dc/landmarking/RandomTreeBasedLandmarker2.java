package fantail.dc.landmarking;

import fantail.dc.Characterizer;
import fantail.dc.DCValue;
import fantail.dc.Randomizable;
import java.util.*;
import weka.core.Instances;

public class RandomTreeBasedLandmarker2 extends Characterizer implements Randomizable {

    private int m_Seed = 1;
    private int m_NumFolds = 2;
    private int m_K = 1;

    protected final String[] ids = new String[]{"RandomTreeDepth1ErrRate_K=" + m_K, "RandomTreeDepth1Kappa_K=" + m_K,
        "RandomTreeDepth2ErrRate_K=" + m_K, "RandomTreeDepth2Kappa_K=" + m_K,
        "RandomTreeDepth3ErrRate_K=" + m_K, "RandomTreeDepth3Kappa_K=" + m_K};

//    protected final String[] ids = new String[]{"RandomTreeDepth1ErrRate", "RandomTreeDepth1Kappa",
//        "RandomTreeDepth2ErrRate", "RandomTreeDepth2Kappa",
//        "RandomTreeDepth3ErrRate", "RandomTreeDepth3Kappa", 
//        "RandomTreeDepth4ErrRate", "RandomTreeDepth4Kappa",
//        "RandomTreeDepth5ErrRate", "RandomTreeDepth5Kappa"};
    public String[] getIDs() {
        return ids;
    }

    @Override
    public void setSeed(int seed) {
        m_Seed = seed;
    }

    public void setK(int k) {
        m_K = k;
    }

    public void setNumFolds(int n) {
        m_NumFolds = n;
    }

    public DCValue[] characterize(Instances data) {

        int seed = m_Seed;
        Random r = new Random(seed);

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
        double score10 = 0.5;

        weka.classifiers.trees.RandomTree cls = new weka.classifiers.trees.RandomTree();
        cls.setSeed(r.nextInt());
        cls.setKValue(m_K);
        //cls.setMaxDepth(1);

        try {
            //ds.buildClassifier(data);
            weka.classifiers.Evaluation eval = new weka.classifiers.Evaluation(data);
            eval.crossValidateModel(cls, data, numFolds, new java.util.Random(1));

            score1 = eval.pctIncorrect();
            score2 = eval.kappa();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //
        cls = new weka.classifiers.trees.RandomTree();
        cls.setSeed(r.nextInt());
        cls.setKValue(m_K);
        //cls.setMaxDepth(2);

        try {

            weka.classifiers.Evaluation eval = new weka.classifiers.Evaluation(data);
            eval.crossValidateModel(cls, data, numFolds, new java.util.Random(1));

            score3 = eval.pctIncorrect();
            score4 = eval.kappa();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //
        cls = new weka.classifiers.trees.RandomTree();
        cls.setSeed(r.nextInt());
        cls.setKValue(m_K);
        //cls.setMaxDepth(3);

        try {

            weka.classifiers.Evaluation eval = new weka.classifiers.Evaluation(data);
            eval.crossValidateModel(cls, data, numFolds, new java.util.Random(1));

            score5 = eval.pctIncorrect();
            score6 = eval.kappa();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //
        cls = new weka.classifiers.trees.RandomTree();
        cls.setSeed(r.nextInt());
        cls.setKValue(m_K);
        //cls.setMaxDepth(4);

        try {

            weka.classifiers.Evaluation eval = new weka.classifiers.Evaluation(data);
            eval.crossValidateModel(cls, data, numFolds, new java.util.Random(1));

            score7 = eval.pctIncorrect();
            score8 = eval.kappa();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //
        cls = new weka.classifiers.trees.RandomTree();
        cls.setSeed(r.nextInt());
        cls.setKValue(m_K);
        //cls.setMaxDepth(5);

        try {
            weka.classifiers.Evaluation eval = new weka.classifiers.Evaluation(data);
            eval.crossValidateModel(cls, data, numFolds, new java.util.Random(1));

            score9 = eval.pctIncorrect();
            score10 = eval.kappa();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new DCValue[]{
            new DCValue(ids[0], score1),
            new DCValue(ids[1], score2),
            new DCValue(ids[2], score3),
            new DCValue(ids[3], score4),
            new DCValue(ids[4], score5),
            new DCValue(ids[5], score6)
        };
    }
}
