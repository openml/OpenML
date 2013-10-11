package org.openml.generatefolds;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;

public class ArffMapping {
	
	FastVector attributes;
	
	
	public ArffMapping() {
		attributes = new FastVector();
		
		FastVector att_type_values = new FastVector(2);
		att_type_values.addElement("TRAIN");
		att_type_values.addElement("TEST");
		
		Attribute type = new Attribute("type",att_type_values);
		Attribute rowid = new Attribute("rowid");
		Attribute fold = new Attribute("fold");
		Attribute repeat = new Attribute("fold");
		
		attributes.addElement(type);
		attributes.addElement(rowid);
		attributes.addElement(repeat);
		attributes.addElement(fold);
	}
	
	public FastVector getArffHeader() {
		return attributes;
	}
	
	public Instance createInstance( boolean train, double rowid, int repeat, int fold ) {
		Instance instance = new Instance(4);
		instance.setValue((Attribute)attributes.elementAt(0), train ? 1.0 : 0.0 );
		instance.setValue((Attribute)attributes.elementAt(1), rowid );
		instance.setValue((Attribute)attributes.elementAt(1), repeat );
		instance.setValue((Attribute)attributes.elementAt(1), fold );
		
		return instance;
	}

}
