package fantail.dc.statistical;

import fantail.dc.Characterizer;
import fantail.dc.DCValue;
import weka.core.Instances;

public class AttributeType extends Characterizer {

    protected final String[] ids = new String[]{"NumNominalAtts",
        "NumNumericAtts",
        "PercentageOfNominalAtts",
        "PercentageOfNumericAtts",
        "NumBinaryAtts",
        "PercentageOfBinaryAtts"};

    @Override
    public String[] getIDs() {
        return ids;
    }

    @Override
    public DCValue[] characterize(Instances instances) {
        int attrib_count = instances.numAttributes() - 1,
                nominal_count = 0,
                numeric_count = 0,
                bin_count = 0;

        for (int i = 0; i < attrib_count; i++) {
            if (instances.attribute(i).isNominal()) {
                nominal_count++;
                if (instances.numDistinctValues(i) == 2) {
                    bin_count++;
                }
            } else {
                numeric_count++;
            }
        }

        return new DCValue[]{
            new DCValue(ids[0], nominal_count),
            new DCValue(ids[1], numeric_count),
            new DCValue(ids[2], 1.0 * nominal_count / instances.numAttributes()),
            new DCValue(ids[3], 1.0 * numeric_count / instances.numAttributes()),
            new DCValue(ids[4], bin_count),
            new DCValue(ids[5], 1.0 * bin_count / instances.numAttributes()),};
    }
}
