package weka.filters.unsupervised.attribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.filters.SimpleBatchFilter;

public class RemoveUnusedClassValues extends SimpleBatchFilter implements OptionHandler {

	private static final long serialVersionUID = 5724291284990109383L;
	
	private int oldClassIndex;
	private int threshold = 1;

	public String globalInfo() {
		return "A simple batch filter that replaces a nominal class with a nominal class that only contains the used values.";
	}

	public Capabilities getCapabilities() {
		Capabilities result = super.getCapabilities();
		result.enableAllAttributes();
		result.enable(Capability.NOMINAL_CLASS); 
		return result;
	}

	protected Instances determineOutputFormat(Instances inputFormat) {
		inputFormat = getInputFormat(); // FIX, we didn't want just the header!
		
		Instances result = new Instances(inputFormat, 0);
		
		int[] usedClassValues = inputFormat.attributeStats( inputFormat.classIndex() ).nominalCounts;
		oldClassIndex = inputFormat.classIndex();
		
		List<String> newClassValues = new ArrayList<String>();
		for( int i = 0; i < usedClassValues.length; ++i ) {
			if( usedClassValues[i] > threshold ) newClassValues.add( inputFormat.classAttribute().value( i ) );
		}
		result.setClassIndex( -1 );
		result.deleteAttributeAt( oldClassIndex );
		Attribute newClassAttribute = new Attribute( "class", newClassValues );
		
		result.insertAttributeAt( newClassAttribute, oldClassIndex );
		result.setClassIndex( oldClassIndex );
		
		return result;
	}

	protected Instances process(Instances inst) {
		Instances result = new Instances(determineOutputFormat(inst), inst.numInstances() );
		for (int i = 0; i < inst.numInstances(); i++) {
			double[] values = new double[result.numAttributes()];
			for (int n = 0; n < inst.numAttributes(); n++) {
				if( n == oldClassIndex ) {
					String oldValue = inst.classAttribute().value( (int) inst.instance(i).classValue() );
					values[n] = result.classAttribute().indexOfValue( oldValue );
				} else {
					values[n] = inst.instance(i).value(n);
				}
			}
			
			if( values[oldClassIndex] < 0.0 ) {
				System.out.println("Discarded (0-based) instance: " + i);
			} else {
				Instance newInstance = new DenseInstance(1, values);
				result.add( newInstance );
			}
		}
		
		System.out.println( Arrays.toString( result.attributeStats( result.classIndex() ).nominalCounts ) );
		
		return result;
	}

	@Override
	public void setOptions(String[] options) throws Exception {
		threshold = Integer.parseInt( Utils.getOption('T', options) );
		
		if (getInputFormat() != null)
			 setInputFormat(getInputFormat());
	}
	
	// TODO: For some reason, Weka's GUI doesn't pick up these options... 
	@Override
	public Enumeration<Option> listOptions() {
		Vector<Option> newVector = new Vector<Option>(1);

	    newVector.addElement(new Option(
	    		"\tSpecifies threshold of occurences. Every value" + 
	    		" occuring less than T will be removed from class.\n" + 
	    		"\t(default none)", "T", 1, "-T <int1>"));
		return newVector.elements();
	}

	@Override
	public String[] getOptions() {
		String [] options = new String [2];
		int current = 0;
	    options[current++] = "-T"; 
	    options[current++] = threshold + "";
	    return options;
	}
	
	
}