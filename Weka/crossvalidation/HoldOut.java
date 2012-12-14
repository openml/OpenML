package crossvalidation;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;

public class HoldOut extends Evaluator{
	
	ArrayList<Integer> train;
	ArrayList<Integer> test;
	ArrayList predictions;
	
	public HoldOut(ArrayList<Integer> train, ArrayList<Integer> test, ArrayList<Integer> predictons){
		this.train=train;
		this.test=test;
		this.predictions=predictions;
	}

}
