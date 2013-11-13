package org.openml.features;

public class DataQuality {

	private final String name;
	private final String label;
	private final String value;
	
	public DataQuality( String name, String label, String value ) {
		this.name = name;
		this.label = label;
		this.value = value;
	}
	
	public DataQuality( String name, String value ) {
		this( name, null, value );
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"name\":\""+name+"\",");
		if(label != null) {
			sb.append("\"label\":\""+label+"\",");
		}
		sb.append("\"value\":"+value+"}");
		return sb.toString();
	}
}
