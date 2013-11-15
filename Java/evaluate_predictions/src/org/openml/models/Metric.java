package org.openml.models;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Metric {

	public String name;
	public String label;
	
	public Metric( String name, String label ) {
		this.name = name;
		this.label = label;
	}
	
	@Override
	public boolean equals( Object o ) {
		if( o instanceof Metric ) {

			Metric other = (Metric) o;
			
			if(other.label == null ) {
				return this.label == null && other.name.equals(this.name);
			} else {
				return other.name.equals(this.name) && other.label.equals(this.label);
			}
			
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
        return new HashCodeBuilder(17, 31).append(name).append(label).toHashCode();
    }
	
	@Override
	public String toString() {
		if(label == null ) 
			return name;
		else
			return name + "(label:" + label + ")";
	}
}
