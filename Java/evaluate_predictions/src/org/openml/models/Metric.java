package org.openml.models;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Metric {

	public String name;
	public String implementation;
	public String label;
	
	public Metric( String name, String implementation, String label ) {
		this.name = name;
		this.implementation = implementation;
		this.label = label;
	}
	
	@Override
	public boolean equals( Object o ) {
		if( o instanceof Metric ) {

			Metric other = (Metric) o;
			
			if(other.label == null ) {
				return this.label == null && other.name.equals(this.name) && other.implementation.equals(this.implementation);
			} else {
				return other.name.equals(this.name) && other.label.equals(this.label) && other.implementation.equals(implementation);
			}
			
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
        return new HashCodeBuilder(17, 31).append(name).append(label).append(implementation).toHashCode();
    }
	
	@Override
	public String toString() {
		if(label == null ) 
			return implementation;
		else
			return implementation + "(label:" + label + ")";
	}
}
