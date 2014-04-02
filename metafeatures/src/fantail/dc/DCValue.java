package fantail.dc;

import java.io.Serializable;

public class DCValue implements Serializable {

    public DCValue(String id, double value) {
        this.id = id;
        this.value = value;
    }
    
    public final String id;
    
    public final double value;

    public String toString() {
        return new StringBuffer().append(id).append("\tValue = ").append(value).toString();
    }
}