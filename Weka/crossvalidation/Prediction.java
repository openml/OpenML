package crossvalidation;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("oml:prediction")
public class Prediction{
	
	@XStreamAlias("oml:prediction-type")
	String predictionType;
	
	@XStreamImplicit(itemFieldName="oml:evaluation-method")
	List<Evaluator> evaluationMethods;
	
	public Prediction(String predType, List<Evaluator> evaluationMethods){
		super();
		this.predictionType=predType;
		this.evaluationMethods=evaluationMethods;
		System.out.println("Creating prediction");
	}
	
	public Prediction(){
		System.out.println("Creating prediction");
	}
	
	public String getPredictionType(){
		return this.predictionType;
	}
	
	public List<Evaluator> getEvaluatorList(){
		return this.evaluationMethods;
	}

}
