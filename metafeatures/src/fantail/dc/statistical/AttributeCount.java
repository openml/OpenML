package fantail.dc.statistical;

import fantail.dc.Characterizer;
import fantail.dc.DCValue;
import weka.core.Instances;

public class AttributeCount extends Characterizer {

    protected final String[] ids = new String[]{"NumAttributes", "Dimensionality"};

    @Override
    public String[] getIDs() {
        return ids;
    }

    @Override
    public DCValue[] characterize(Instances instances) {

        return new DCValue[]{
            new DCValue(ids[0], instances.numAttributes()),
            new DCValue(ids[1], 1.0 * instances.numAttributes() / instances.numInstances())
        };
    }
}
