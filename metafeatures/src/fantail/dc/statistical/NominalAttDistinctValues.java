package fantail.dc.statistical;

import fantail.dc.Characterizer;
import fantail.dc.DCValue;
import weka.core.Instances;
import java.util.*;

public class NominalAttDistinctValues extends Characterizer {

    protected final String[] ids = new String[]{"MaxNominalAttDistinctValues",
        "MinNominalAttDistinctValues",
        "MeanNominalAttDistinctValues",
        "StdvNominalAttDistinctValues"};

    @Override
    public String[] getIDs() {
        return ids;
    }

    @Override
    public DCValue[] characterize(Instances data) {
        int attrib_count = data.numAttributes() - 1,
                nominal_count = 0,
                numeric_count = 0;

        for (int i = 0; i < attrib_count; i++) {
            if (data.attribute(i).isNominal()) {
                nominal_count++;
            } else {
                numeric_count++;
            }
        }

        if (nominal_count == 0) {
            return new DCValue[]{
                new DCValue(ids[0], -1),
                new DCValue(ids[1], -1),
                new DCValue(ids[2], -1),
                new DCValue(ids[3], -1)
            };
        }

        ArrayList<Double> distinctValuesCounts = new ArrayList<Double>();

        for (int i = 0; i < attrib_count; i++) {
            if (data.attribute(i).isNominal()) {
                distinctValuesCounts.add(1.0 * data.numDistinctValues(i));

            }
        }

        double[] values = new double[distinctValuesCounts.size()];
        for (int i = 0; i < distinctValuesCounts.size(); i++) {
            values[i] = distinctValuesCounts.get(i);
        }

        double min = values[weka.core.Utils.minIndex(values)];
        double max = values[weka.core.Utils.maxIndex(values)];
        double mean = weka.core.Utils.mean(values);
        double variance = weka.core.Utils.variance(values);
        double stdv = Math.sqrt(variance);

        return new DCValue[]{
            new DCValue(ids[0], max),
            new DCValue(ids[1], min),
            new DCValue(ids[2], mean),
            new DCValue(ids[3], stdv)
        };

    }
}
