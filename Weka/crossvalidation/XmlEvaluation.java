package crossvalidation;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

public class XmlEvaluation {
	
	@XStreamImplicit(itemFieldName="oml:evaluation-method")
	private List<XmlEvaluation> evaluationMethods;
	
	public XmlEvaluation(List<XmlEvaluation> evaluationMethods){
		this.evaluationMethods=evaluationMethods;
	}


}
