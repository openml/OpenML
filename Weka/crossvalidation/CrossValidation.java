package crossvalidation;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("oml:cross-validation")
public class CrossValidation extends Evaluator {
	
	@XStreamAlias("oml:number-of-folds")
	int nrFolds;
	@XStreamAlias("oml:number-of-repeats")
	int nrRepeats;
	@XStreamOmitField
	ArrayList predictions;
	
	@XStreamImplicit(itemFieldName="oml:repeat")
	private List<CrossValidationRepeat> repeats;
	
	public CrossValidation(int nrRepeats, int nbFolds, ArrayList preds, List<CrossValidationRepeat> repeats){
		this.nrRepeats=nrRepeats;
		this.nrFolds=nbFolds;	
		this.predictions = preds;
		this.repeats=repeats;
	}
	
	public int getNrFolds(){
		return this.nrFolds;
	}

	public int getNrRepeats(){
		return this.nrRepeats;
	}
	
	public List<CrossValidationRepeat> getCrossValidationRepeats(){
		return this.repeats;
	}
}
