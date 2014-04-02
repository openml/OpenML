package fantail.dc.statistical;

import fantail.dc.Characterizer;
import fantail.dc.DCValue;
import weka.core.Instances;
import weka.core.Instance;

public class MissingValues extends Characterizer {

    protected final String[] ids = new String[]{"NumMissingValues", "PercentageOfMissingValues"};

    @Override
    public String[] getIDs() {
        return ids;
    }

    @Override
    public DCValue[] characterize(Instances instances) {
        final int instance_count = instances.numInstances(),
                attrib_count = instances.numAttributes();
        int count = 0;

        for (int i = 0; i < instance_count; i++) {
            Instance instance = instances.instance(i);

            for (int j = 0; j < attrib_count; j++) {
                if (instance.isMissing(j)) {
                    count++;
                }
            }
        }
        return new DCValue[]{
            new DCValue(ids[0], count),
            new DCValue(ids[1], 1.0 * count / (instances.numAttributes() * instances.numInstances()))
        };
    }
}
