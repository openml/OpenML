package crossvalidation;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("oml:data-set")
public class DataSet {
	
	@XStreamAlias("oml:data-set-id")
	int datasetId;
	@XStreamAlias("oml:target-feature")
	int targetFeature;
	
	public DataSet(int id, int feature){
		this.datasetId=id;
		this.targetFeature=feature;
		System.out.println(this.datasetId);
	}

}
