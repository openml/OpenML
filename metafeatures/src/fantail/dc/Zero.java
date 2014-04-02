package fantail.dc;

import weka.core.Instances;

public class Zero extends Characterizer {

    protected final String[] ids = new String[]{"Zero", "One"};

    public String[] getIDs() {
        return ids;
    }

    public DCValue[] characterize(Instances instances) {
        return new DCValue[]{
            new DCValue(ids[0], 0.0),
            new DCValue(ids[1], 1.0)
        };
    }
}
