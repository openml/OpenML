package fantail.dc.statistical;

import fantail.dc.Characterizer;
import fantail.dc.DCValue;
import weka.core.Instances;
import weka.core.Instance;

public class Statistical extends Characterizer {

    protected final String[] ids = new String[]{"MeanMeansOfNumericAtts",
        "MeanStdDevOfNumericAtts",
        "MeanKurtosisOfNumericAtts",
        "MeanSkewnessOfNumericAtts"};

    @Override
    public String[] getIDs() {
        return ids;
    }

    @Override
    public DCValue[] characterize(Instances instances) {
        int attrib_count = instances.numAttributes() - 1,
                numeric_count = 0;

        double mean_sum = 0.0,
                stddev_sum = 0.0,
                kurtosis_sum = 0.0,
                skewness_sum = 0.0;

        for (int i = 0; i < attrib_count; i++) {
            if (instances.attribute(i).isNumeric()) {
                numeric_count++;
                final double mean = instances.meanOrMode(i);
                final double stddev = Math.sqrt(instances.variance(i));
                final double kurtosis = findKurtosis(instances, mean, stddev, i);
                final double skewness = findSkewness(instances, mean, stddev, i);
                mean_sum += mean;
                stddev_sum += stddev;
                kurtosis_sum += kurtosis;
                skewness_sum += skewness;
            }
        }

        if (0 == numeric_count) {
            return new DCValue[]{
                new DCValue(ids[0], 0.0),
                new DCValue(ids[1], 0.0),
                new DCValue(ids[2], 0.0),
                new DCValue(ids[3], 0.0)
            };
        } else {
            return new DCValue[]{
                new DCValue(ids[0], mean_sum / numeric_count),
                new DCValue(ids[1], stddev_sum / numeric_count),
                new DCValue(ids[2], kurtosis_sum / numeric_count),
                new DCValue(ids[3], skewness_sum / numeric_count)
            };
        }
    }

    private static double findKurtosis(Instances instances, double mean, double stddev, int attrib) {
        final double S4 = Math.pow(stddev, 4),
                YBAR = mean;
        double sum = 0.0;
        final int COUNT = instances.numInstances();
        int n = 0;

        if (S4 == 0) {
            return 0;
        }

        for (int i = 0; i < COUNT; i++) {
            Instance instance = instances.instance(i);
            if (!instance.isMissing(attrib)) {
                n++;
                sum += Math.pow(instance.value(attrib) - YBAR, 4);
            }
        }

        return (sum / ((n - 1) * S4)) - 3;
    }

    private static double findSkewness(Instances instances, double mean, double stddev, int attrib) {
        final double S3 = Math.pow(stddev, 3),
                YBAR = mean;
        double sum = 0.0;
        final int COUNT = instances.numInstances();
        int n = 0;

        if (S3 == 0) {
            return 0;
        }

        for (int i = 0; i < COUNT; i++) {
            Instance instance = instances.instance(i);
            if (!instance.isMissing(attrib)) {
                n++;
                sum += Math.pow(instance.value(attrib) - YBAR, 3);
            }
        }

        return (sum / ((n - 1) * S3));
    }
}
