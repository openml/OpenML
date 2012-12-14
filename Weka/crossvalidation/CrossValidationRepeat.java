package crossvalidation;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("oml:repeat")
public class CrossValidationRepeat {
	
	@XStreamAlias("oml:repeat-id")
	int repeatId;
	@XStreamImplicit(itemFieldName="oml:fold")
	private List<CrossValidationFold> folds;
	
	public void Repeat(int repeatId,List<CrossValidationFold> folds){
		this.repeatId=repeatId;
		this.folds=folds;
	}

}
