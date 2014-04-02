package fantail.dc;

import fantail.dc.landmarking.*;
import fantail.dc.statistical.*;
import weka.core.*;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.converters.ConverterUtils.*;
import java.io.*;

public class DCTest1 {

    public static void datasetCharacteristics(Instances data) {

        Characterizer dc = new Statistical();
        DCValue[] dcValues = dc.characterize(data);
        for (DCValue dcValue : dcValues) {
            System.out.println(dcValue.toString());
        }

        dc = new AttributeCount();
        dcValues = dc.characterize(data);
        for (DCValue dcValue : dcValues) {
            System.out.println(dcValue.toString());
        }

        dc = new AttributeType();
        dcValues = dc.characterize(data);
        for (DCValue dcValue : dcValues) {
            System.out.println(dcValue.toString());
        }

        dc = new ClassAtt();
        dcValues = dc.characterize(data);
        for (DCValue dcValue : dcValues) {
            System.out.println(dcValue.toString());
        }

        dc = new DefaultAccuracy();
        dcValues = dc.characterize(data);
        for (DCValue dcValue : dcValues) {
            System.out.println(dcValue.toString());
        }

        dc = new IncompleteInstanceCount();
        dcValues = dc.characterize(data);
        for (DCValue dcValue : dcValues) {
            System.out.println(dcValue.toString());
        }

        dc = new InstanceCount();
        dcValues = dc.characterize(data);
        for (DCValue dcValue : dcValues) {
            System.out.println(dcValue.toString());
        }

        dc = new MissingValues();
        dcValues = dc.characterize(data);
        for (DCValue dcValue : dcValues) {
            System.out.println(dcValue.toString());
        }

        dc = new NominalAttDistinctValues();
        dcValues = dc.characterize(data);
        for (DCValue dcValue : dcValues) {
            System.out.println(dcValue.toString());
        }

        dc = new AttributeEntropy();
        dcValues = dc.characterize(data);
        for (DCValue dcValue : dcValues) {
            System.out.println(dcValue.toString());
        }

        dc = new SimpleLandmarkers();
        dcValues = dc.characterize(data);
        for (DCValue dcValue : dcValues) {
            System.out.println(dcValue.toString());
        }

        dc = new J48BasedLandmarker();
        dcValues = dc.characterize(data);
        for (DCValue dcValue : dcValues) {
            System.out.println(dcValue.toString());
        }

        dc = new REPTreeBasedLandmarker();
        dcValues = dc.characterize(data);
        for (DCValue dcValue : dcValues) {
            System.out.println(dcValue.toString());
        }

        dc = new RandomTreeBasedLandmarker();
        dcValues = dc.characterize(data);
        for (DCValue dcValue : dcValues) {
            System.out.println(dcValue.toString());
        }
    }

    public static void main(String args[]) throws Exception {

        String datasetPath = "C:\\Users\\Quan\\Desktop\\wine.arff";

        DataSource source = new DataSource(datasetPath);
        Instances data = source.getDataSet();
        if (data.classIndex() == -1) {
            data.setClassIndex(data.numAttributes() - 1);
        }
        System.err.println(datasetPath);
        datasetCharacteristics(data);
    }
}
