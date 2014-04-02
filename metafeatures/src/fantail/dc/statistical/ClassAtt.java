package fantail.dc.statistical;

import fantail.dc.Characterizer;
import fantail.dc.DCValue;
import weka.core.*;

public class ClassAtt extends Characterizer {

    protected final String[] ids = new String[]{"ClassCount", "PositivePercentage", "NegativePercentage"};

    @Override
    public String[] getIDs() {
        return ids;
    }

    @Override
    public DCValue[] characterize(Instances instances) {

        int pCount = 0;
        int nCount = 0;

        int[] counts = new int[instances.numClasses()];
        for (int i = 0; i < instances.numInstances(); i++) {
            Instance instance = instances.instance(i);
            counts[(int) instance.classValue()]++;
        }

        pCount = counts[weka.core.Utils.minIndex(counts)];
        nCount = counts[weka.core.Utils.maxIndex(counts)];

        return new DCValue[]{
            new DCValue(ids[0], instances.numClasses()),
            new DCValue(ids[1], 1.0 * pCount / instances.numInstances()),
            new DCValue(ids[2], 1.0 * nCount / instances.numInstances())
        };
    }
}
