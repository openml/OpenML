
package fantail.dc;

import weka.core.Instances;

public abstract class Characterizer {

    public abstract String[] getIDs();

    public abstract DCValue[] characterize(Instances instances);
    
    public int getNumMetaFeatures() {
        return getIDs().length;
    }
}
