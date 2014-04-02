package fantail.dc.statistical;

import fantail.dc.Characterizer;
import fantail.dc.DCValue;
import weka.core.Attribute;
import weka.core.Instances;

public class DefaultAccuracy extends Characterizer {

    protected final String[] ids = new String[]{"DefaultAccuracy"};

    @Override
    public String[] getIDs() {
        return ids;
    }

    @Override
    public DCValue[] characterize(Instances instances) {
        
        Attribute class_attrib = instances.classAttribute();
        final double mode = instances.meanOrMode(class_attrib);
        final int count = instances.numInstances();
        int nonerrors = 0;

        for (int i = 0; i < count; i++) {
            if (mode == instances.instance(i).value(class_attrib)) {
                nonerrors++;
            }
        }

        return new DCValue[]{
            new DCValue(ids[0],
            ((double) nonerrors / count))
        };
    }
}
