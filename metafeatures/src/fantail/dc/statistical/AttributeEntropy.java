package fantail.dc.statistical;

import fantail.dc.Characterizer;
import fantail.dc.DCUntils;
import fantail.dc.DCValue;
import weka.core.Instances;

public class AttributeEntropy extends Characterizer {

    protected final String[] ids = new String[]{"ClassEntropy",
        "MeanAttributeEntropy",
        "MeanMutualInformation",
        "EquivalentNumberOfAtts",
        "NoiseToSignalRatio"};

    @Override
    public String[] getIDs() {
        return ids;
    }

    @Override
    public DCValue[] characterize(Instances data) {

        double classEntropy = DCUntils.computeClassEntropy(data);
        double meanAttEntropy = DCUntils.computeMeanAttributeEntropy(data);
        double meanMI = DCUntils.computeMutualInformation(data);
        double noiseSignalRatio = (meanAttEntropy - meanMI) / meanMI;
        double ena = 0;

        if (meanMI <= 0 || Double.isNaN(meanMI)) {
            ena = -1;
            noiseSignalRatio = -1;
        } else {
            ena = classEntropy / meanMI;
        }

        if (Double.isNaN(classEntropy)) {
            classEntropy = -1;
        }
        if (Double.isNaN(meanAttEntropy)) {
            meanAttEntropy = -1;
        }
        if (Double.isNaN(meanMI)) {
            meanMI = -1;
        }
        if (Double.isNaN(ena)) {
            ena = -1;
        }
        if (Double.isNaN(noiseSignalRatio)) {
            noiseSignalRatio = -1;
        }

        return new DCValue[]{
            new DCValue(ids[0], classEntropy),
            new DCValue(ids[1], meanAttEntropy),
            new DCValue(ids[2], meanMI),
            new DCValue(ids[3], ena),
            new DCValue(ids[4], noiseSignalRatio)
        };
    }
}
