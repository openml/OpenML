package moa.classifiers.openml;

import weka.core.Instance;
import moa.classifiers.AbstractClassifier;
import moa.core.Measurement;


public class DummyClassifier extends AbstractClassifier {
	
	private static final long serialVersionUID = 3347729860311742557L;
	
	public DummyClassifier() {
		System.out.println("Initialized VAN RIJN DummyClassifier");
	}

	@Override
	public double[] getVotesForInstance(Instance arg0) {
		// TODO Auto-generated method stub
		return new double[0];
	}

	@Override
	public boolean isRandomizable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void getModelDescription(StringBuilder arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Measurement[] getModelMeasurementsImpl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resetLearningImpl() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trainOnInstanceImpl(Instance arg0) {
		// TODO Auto-generated method stub
		
	}
}
