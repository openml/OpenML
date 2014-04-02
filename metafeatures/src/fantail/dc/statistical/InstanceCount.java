package fantail.dc.statistical;

import fantail.dc.Characterizer;
import fantail.dc.DCValue;
import weka.core.Instances;

public class InstanceCount extends Characterizer {

    protected final String[] ids = new String[]{"InstanceCount"};

    public String[] getIDs() {
        return ids;
    }

    public DCValue[] characterize(Instances instances) {
        return new DCValue[]{
            new DCValue(ids[0], instances.numInstances())
        };
    }
}
