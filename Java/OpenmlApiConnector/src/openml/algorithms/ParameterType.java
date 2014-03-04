package openml.algorithms;

public enum ParameterType {
	  FLAG("flag"),
	  KERNEL("kernel"),
	  BASELEARNER("baselearner"),
	  OPTION("option");

	  private String text;

	  ParameterType(String text) {
	    this.text = text;
	  }

	  public String getName() {
	    return this.text;
	  }

	  public static ParameterType fromString(String text) {
	    if (text != null) {
	      for (ParameterType b : ParameterType.values()) {
	        if (text.equalsIgnoreCase(b.text)) {
	          return b;
	        }
	      }
	    }
	    return null;
	  }
	}