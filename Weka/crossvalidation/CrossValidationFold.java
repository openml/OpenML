package crossvalidation;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("fold")
public class CrossValidationFold {
	
	@XStreamAlias("oml:fold-id")
	private int foldId;
	@XStreamImplicit(itemFieldName="oml:fold-train")
	private List<Integer> foldTrain;
	@XStreamImplicit(itemFieldName="oml:fold-test")
	private List<Integer> foldTest;
	
	public CrossValidationFold(int foldId, List foldTrain, List foldTest){
		this.foldId=foldId;
		this.foldTrain=foldTrain;
		this.foldTest=foldTest;
	}

}
