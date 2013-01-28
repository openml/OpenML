
package com.rapidminer.openml.operator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Level;

import com.rapidminer.Process;
import com.rapidminer.RapidMiner;
import com.rapidminer.example.Attribute;
import com.rapidminer.example.AttributeRole;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.set.SplittedExampleSet;
import com.rapidminer.example.table.MemoryExampleTable;
import com.rapidminer.openml.gui.openMLTab;
import com.rapidminer.openml.task.OpenMLTaskManager;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.OperatorCapability;
import com.rapidminer.operator.OperatorChain;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.ValueDouble;
import com.rapidminer.operator.learner.CapabilityCheck;
import com.rapidminer.operator.learner.CapabilityProvider;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.operator.ports.PortPairExtender;
import com.rapidminer.operator.ports.metadata.CapabilityPrecondition;
import com.rapidminer.operator.ports.metadata.ExampleSetMetaData;
import com.rapidminer.operator.ports.metadata.ExampleSetPassThroughRule;
import com.rapidminer.operator.ports.metadata.PassThroughRule;
import com.rapidminer.operator.ports.metadata.SetRelation;
import com.rapidminer.operator.ports.metadata.SubprocessTransformRule;
import com.rapidminer.parameter.UndefinedParameterError;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.container.Pair;

public class XVConfigPrediction extends OperatorChain implements CapabilityProvider {

	private final InputPort exampleSetInput = getInputPorts().createPort("example set", ExampleSet.class);
	private final InputPort trainingConfigPort = getInputPorts().createPort("training config", ExampleSet.class);
	private final InputPort testConfigPort = getInputPorts().createPort("test config", ExampleSet.class);

	private final OutputPort trainingProcessExampleSource = getSubprocess(0).getInnerSources().createPort("training");
	private final InputPort trainingProcessModelSink = getSubprocess(0).getInnerSinks().createPort("model");

	// training -> testing
	private final PortPairExtender throughExtender = new PortPairExtender("through", getSubprocess(0).getInnerSinks(), getSubprocess(1).getInnerSources());

	// testing
	private final OutputPort applyProcessModelSource = getSubprocess(1).getInnerSources().createPort("model");
	private final OutputPort applyProcessExampleSource = getSubprocess(1).getInnerSources().createPort("unlabelled data");
	private final InputPort applyProcessExampleInnerSink = getSubprocess(1).getInnerSinks().createPort("labelled data");

	// output
	private final OutputPort exampleSetOutput = getOutputPorts().createPort("labelled data");

	private int currentRepeat;
	private int currentFold;

	public XVConfigPrediction(OperatorDescription description) {
		super(description, "Training", "Model Application");

		exampleSetInput.addPrecondition(new CapabilityPrecondition(this, exampleSetInput));

		throughExtender.start();

		getTransformer().addRule(new ExampleSetPassThroughRule(exampleSetInput, trainingProcessExampleSource, SetRelation.EQUAL) {

			@Override
			public ExampleSetMetaData modifyExampleSet(ExampleSetMetaData metaData) throws UndefinedParameterError {
			try {
					metaData.setNumberOfExamples(trainingProcessExampleSource.getData(ExampleSet.class).size());
				} catch (UserError e) {
					metaData.setNumberOfExamples(metaData.getNumberOfExamples());
				}
				return super.modifyExampleSet(metaData);
			}
		});
		getTransformer().addRule(new ExampleSetPassThroughRule(exampleSetInput, applyProcessExampleSource, SetRelation.EQUAL) {

			@Override
			public ExampleSetMetaData modifyExampleSet(ExampleSetMetaData metaData) throws UndefinedParameterError {
				try {
					metaData.setNumberOfExamples(applyProcessExampleSource.getData(ExampleSet.class).size());
				} catch (UserError e) {
					metaData.setNumberOfExamples(metaData.getNumberOfExamples());
				}
				return super.modifyExampleSet(metaData);
			}
		});
		getTransformer().addRule(new SubprocessTransformRule(getSubprocess(0)));
		getTransformer().addRule(new PassThroughRule(trainingProcessModelSink, applyProcessModelSource, false));
		getTransformer().addRule(throughExtender.makePassThroughRule());
		getTransformer().addRule(new SubprocessTransformRule(getSubprocess(1)));
		getTransformer().addPassThroughRule(applyProcessExampleInnerSink, exampleSetOutput);

		addValue(new ValueDouble("current_repeat", "The number of the current repeat being processed.") {

			@Override
			public double getDoubleValue() {
				return currentRepeat;
			}
		});
		
		addValue(new ValueDouble("current_fold", "The number of the current fold being processed.") {

			@Override
			public double getDoubleValue() {
				return currentFold;
			}
		});
	}

	@Override
	public void doWork() throws OperatorException {
		ExampleSet inputSet = exampleSetInput.getData(ExampleSet.class);
		ExampleSet trainConfig = trainingConfigPort.getData(ExampleSet.class);
		ExampleSet testConfig = testConfigPort.getData(ExampleSet.class);

		//check capabilities and produce errors if they are not fulfilled
		CapabilityCheck check = new CapabilityCheck(this, false);
		check.checkLearnerCapabilities(this, inputSet);


		Attribute fold = trainConfig.getAttributes().get("fold");
		Attribute repeat = trainConfig.getAttributes().get("repeat");

		if (fold == null || repeat == null || !(testConfig.getAttributes().contains(repeat)) || !(testConfig.getAttributes().contains(fold))) {
			throw new UserError(this, "fold_config_attributes_missing");
		}

	
		HashMap<Integer, TreeSet<Integer>> trainingFoldConfiguration = getFoldConfiguration(trainConfig, fold, repeat, "training");
		HashMap<Integer, TreeSet<Integer>> testFoldConfiguration = getFoldConfiguration(testConfig, fold, repeat, "test");


		if (!testFoldConfiguration.equals(trainingFoldConfiguration)) {
			throw new UserError(this, "unequal_no_of_folds_or_repeats_in_test_and_train");
		}
		
		int numberOfTrainRepeats = trainingFoldConfiguration.keySet().size();
		int numberOfTrainfolds = testFoldConfiguration.get(0).size();

		MemoryExampleTable predictionsExampleTable = null;
		Map<Attribute, String> specialAttributesMap = new HashMap<Attribute, String>();

		for (int i = 0; i < numberOfTrainRepeats; i++) {
			for (int j = 0; j < numberOfTrainfolds; j++) {
				currentRepeat = i;
				currentFold = j;
				ExampleSet trainingSet = filterRepeatsandFolds(i, j, trainConfig, inputSet);
				trainingProcessExampleSource.deliver(trainingSet);
				getSubprocess(0).execute();

				ExampleSet testSet = filterRepeatsandFolds(i, j, testConfig, inputSet);
				applyProcessExampleSource.deliver((IOObject) testSet);
				throughExtender.passDataThrough();
				applyProcessModelSource.deliver(trainingProcessModelSink.getData(IOObject.class));
				getSubprocess(1).execute();

				ExampleSet predictedSet = applyProcessExampleInnerSink.getData(ExampleSet.class);

				if (predictionsExampleTable == null) {
					predictionsExampleTable = new MemoryExampleTable(predictedSet.getExampleTable().getAttributes());
					Iterator<AttributeRole> attRoles = predictedSet.getAttributes().allAttributeRoles();
					while (attRoles.hasNext()) {
						AttributeRole role = attRoles.next();
						Attribute attribute = role.getAttribute();
						if (role.isSpecial()) {
							specialAttributesMap.put(attribute, role.getSpecialName());
						}

					}
				}
				for (int k = 0; k < predictedSet.size(); k++) {
					predictionsExampleTable.addDataRow(predictedSet.getExampleTable().getDataRow(k));
				}

				inApplyLoop();

			}
		}

		exampleSetOutput.deliver(predictionsExampleTable.createExampleSet(specialAttributesMap));

	}

	private HashMap<Integer,TreeSet<Integer>>  getFoldConfiguration(ExampleSet config, Attribute fold, Attribute repeat, String configType) throws UserError {
		
		HashMap<Integer,TreeSet<Integer>> foldConfig = new HashMap<Integer, TreeSet<Integer>>();
		
		for (int i = 0; i < config.size(); i++) {
			
			int repeatVal = (int) config.getExample(i).getValue(repeat);
			int foldVal = (int) config.getExample(i).getValue(fold);

			
			if (!foldConfig.containsKey(repeatVal)){
				foldConfig.put(repeatVal, new TreeSet<Integer>());
			}else{
				foldConfig.get(repeatVal).add(foldVal);
			}
		}
		
		int prevFoldSize = foldConfig.get(0).size();
		for (Integer key : foldConfig.keySet()) {
			if (prevFoldSize!=foldConfig.get(key).size()){
				throw new UserError(this, "no_of_folds_in_repeats_are_not_equal");
			}
		}
		
	
		return foldConfig;
		
	}

	private ExampleSet filterRepeatsandFolds(int repeat, int fold, ExampleSet config, ExampleSet inputSet) {

		ExampleSet dataExampleSet = null;

		try {
			String filterRepeatsandFoldsProcess = OpenMLTaskManager.readXMLFromResource("/com/rapidminer/resources/util/rmprocess/FilterRepeatsAndFolds.xml");

			IOContainer ioInput = new IOContainer(new IOObject[] { config, inputSet });

			RapidMiner.setExecutionMode(com.rapidminer.RapidMiner.ExecutionMode.COMMAND_LINE);
			Process importProcess = new Process(filterRepeatsandFoldsProcess);
			importProcess.getContext().addMacro(new Pair<String, String>("fold", String.valueOf(fold)));
			importProcess.getContext().addMacro(new Pair<String, String>("repeat", String.valueOf(repeat)));
			IOContainer ioResult = importProcess.run(ioInput);

			if (ioResult.getElementAt(0) instanceof ExampleSet) {
				dataExampleSet = (ExampleSet) ioResult.getElementAt(0);
			}

		} catch (Exception e) {
			LogService.getRoot().log(Level.SEVERE, openMLTab.readFromBundle("openml.fetch_meta_data_fail"));
		}
		return dataExampleSet;
	}


	@Override
    public boolean supportsCapability(OperatorCapability capability) {
        switch (capability) {
        case NO_LABEL:
            return false;
         default:
            return true;
        }
    }

}
