package fantail.dc;

import java.io.Serializable;

public class APValue implements Serializable {

    public APValue(String id, double accuracy, double time) {
        this.id = id;
        this.accuracy = accuracy;

        if (time > 0) {
            this.time = time;
        } else {
            this.time = 1; // Prevent division by zero
        }
    }

    public double error() {
        return 1 - accuracy;
    }
    
    public final String id;
    
    public final double accuracy, time;

    public String toString() {
        return new StringBuffer().append(id).append("\tAccuracy = ").append(accuracy).append("\tTime = ").append(time).toString();
    }
}
