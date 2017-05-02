/*
 *  Webapplication - Java library that runs on OpenML servers
 *  Copyright (C) 2014 
 *  @author Jan N. van Rijn (j.n.van.rijn@liacs.leidenuniv.nl)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  
 */
package org.openml.webapplication.generatefolds;

import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;

public class ArffMapping {
	
	ArrayList<Attribute> attributes;
	
	
	public ArffMapping( boolean use_samples ) {
		attributes = new ArrayList<Attribute>();
		
		ArrayList<String> att_type_values = new ArrayList<String>();
		att_type_values.add("TRAIN");
		att_type_values.add("TEST");
		
		Attribute type = new Attribute("type",att_type_values);
		Attribute rowid = new Attribute("rowid");
		Attribute fold = new Attribute("fold");
		Attribute repeat = new Attribute("repeat");
		
		attributes.add(type);
		attributes.add(rowid);
		attributes.add(repeat);
		attributes.add(fold);
		
		if(use_samples){
			Attribute sample = new Attribute("sample");
			attributes.add(sample);
		}
	}
	
	public ArrayList<Attribute> getArffHeader() {
		return attributes;
	}
	
	public Instance createInstance( boolean train, int rowid, int repeat, int fold ) {
		Instance instance = new DenseInstance( 4 );
		instance.setValue( attributes.get(0), train ? 0.0 : 1.0 );
		instance.setValue( attributes.get(1), rowid );
		instance.setValue( attributes.get(2), repeat );
		instance.setValue( attributes.get(3), fold );
		
		return instance;
	}
	
	public Instance createInstance( boolean train, int rowid, int repeat, int fold, int sample ) {
		Instance instance = new DenseInstance(5);
		instance.setValue( attributes.get(0), train ? 0.0 : 1.0 );
		instance.setValue( attributes.get(1), rowid );
		instance.setValue( attributes.get(2), repeat );
		instance.setValue( attributes.get(3), fold );
		instance.setValue( attributes.get(4), sample );
		
		return instance;
	}

}
