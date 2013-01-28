package com.rapidminer.openml.operator;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.openml.util.MissingValueException;
import org.xml.sax.SAXException;

import com.rapidminer.MacroHandler;
import com.rapidminer.example.Attribute;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.openml.task.OpenMLTaskManager;
import com.rapidminer.openml.task.SuperVisedClassificationTask;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.operator.ports.metadata.ExampleSetMetaData;
import com.rapidminer.operator.ports.metadata.GenerateNewExampleSetMDRule;
import com.rapidminer.operator.ports.metadata.MetaData;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.tools.Observable;
import com.rapidminer.tools.Observer;


public class ReadOpenMLTask extends Operator {
	
	private OutputPort dataPort = getOutputPorts().createPort("data");
	private OutputPort trainingPort = getOutputPorts().createPort("training config");
	private OutputPort testPort = getOutputPorts().createPort("test config");
	
	public static final String PARAMETER_NUMBER_OF_REPEATS = "num_repeats";
	public static final String PARAMETER_NUMBER_OF_FOLDS = "num_folds";
	public static final String PARAMETER_TASK_ID = "open_ml_taskid";
	
	private SuperVisedClassificationTask classificationTask;
	private Integer taskId;
	private boolean parameterModified = true;

	public ReadOpenMLTask(OperatorDescription description) {
		super(description);
		
		loadTask();
		
		getTransformer().addRule(new GenerateNewExampleSetMDRule(dataPort){
			@Override
			public MetaData modifyMetaData(ExampleSetMetaData unmodifiedMetaData) {
				loadTask();
				if(classificationTask!=null){
					try {
						ExampleSet data = classificationTask.getData().getData();
						Attribute targetFeatureAttribute = data.getAttributes().get(classificationTask.getTargetFeature());
						data.getAttributes().setLabel(targetFeatureAttribute);
						ExampleSetMetaData exampleSetMetaData = new ExampleSetMetaData(data);
						return exampleSetMetaData;
					} catch (MissingValueException e) {
						return unmodifiedMetaData;
					}
				
				}
				return unmodifiedMetaData;
			}
		});
		getTransformer().addRule(new GenerateNewExampleSetMDRule(trainingPort){
			@Override
			public MetaData modifyMetaData(ExampleSetMetaData unmodifiedMetaData) {
				if(classificationTask!=null){
					return new ExampleSetMetaData(classificationTask.getTrainConfig());
				}
				return unmodifiedMetaData;
			}
		});
		getTransformer().addRule(new GenerateNewExampleSetMDRule(testPort){
			@Override
			public MetaData modifyMetaData(ExampleSetMetaData unmodifiedMetaData) {
				if(classificationTask!=null){
					return new ExampleSetMetaData(classificationTask.getTestConfig());
				}
				return unmodifiedMetaData;
			}
		});
		
		observeParameters();
	}

	private void loadTask() {
		try {
			taskId = getParameterAsInt(PARAMETER_TASK_ID);
			if (parameterModified) {
				classificationTask = OpenMLTaskManager.fetchClassificationTask(taskId.toString());
			}
		} catch (Exception e) {
			classificationTask = null;
		}
	}
	
	private void observeParameters() {
		// we add this as the first observer. otherwise, this change is not seen
		// by the resulting meta data transformation
		getParameters().addObserverAsFirst(new Observer<String>() {
			@Override
			public void update(Observable<String> observable, String arg) {
				parameterModified = true;
			}			
		}, false);
	}

	@Override
	public void doWork() throws OperatorException {
		
		
		try {
			
			classificationTask = OpenMLTaskManager.fetchClassificationTask(taskId.toString());
			
			Integer numberOfRepeats = classificationTask.getNumberofRepeats();
			Integer numberOfFolds = classificationTask.getNumberOfFolds();
//			String targetFeature = classificationTask.getTargetFeature();
			
			String numRepeatsMacro = getParameterAsString(PARAMETER_NUMBER_OF_REPEATS);
	        String numFoldsMacro = getParameterAsString(PARAMETER_NUMBER_OF_FOLDS);
	       
			MacroHandler macroHandler = getProcess().getMacroHandler();
	        macroHandler.addMacro(numRepeatsMacro, numberOfRepeats.toString());
	        macroHandler.addMacro(numFoldsMacro, numberOfFolds.toString());
	        
	        ExampleSet dataExampleSet = classificationTask.getData().getData();
	        Attribute targetFeatureAttribute = dataExampleSet.getAttributes().get(classificationTask.getTargetFeature());
	        dataExampleSet.getAttributes().setLabel(targetFeatureAttribute);

			/*String SetTargetFeatureprocess = OpenMLTaskManager.readXMLFromResource("/com/rapidminer/resources/util/rmprocess/setRole.xml");

			ExampleSet dataExampleSet = null;
			try {
				IOContainer ioInput = new IOContainer(new IOObject[] {classificationTask.getData().getData()});
				
				RapidMiner.setExecutionMode(com.rapidminer.RapidMiner.ExecutionMode.COMMAND_LINE);
				Process importProcess = new Process(SetTargetFeatureprocess);
				importProcess.getContext().addMacro(new Pair<String, String>("TARGET_FEATURE", targetFeature));
				IOContainer ioResult = importProcess.run(ioInput);
				

				if (ioResult.getElementAt(0) instanceof ExampleSet) {
					dataExampleSet = (ExampleSet) ioResult.getElementAt(0);
				}
				
			} catch (Exception e) {
				LogService.getRoot().log(Level.SEVERE, openMLTab.readFromBundle("openml.fetch_meta_data_fail"));
			}*/

		
	        ExampleSet trainConfigExampleSet = classificationTask.getTrainConfig();
	        ExampleSet testConfigExampleSet = classificationTask.getTestConfig();
	        
	        dataPort.deliver(dataExampleSet);
	        trainingPort.deliver(trainConfigExampleSet);
	        testPort.deliver(testConfigExampleSet);
	        
	        
		}
		catch (IOException e) {
			throw new UserError(this, "open_ml_xml_read_error");
		} catch (ParserConfigurationException e) {
			throw new UserError(this, "open_ml_xml_parse_error");
		} catch (SAXException e) {
			throw new UserError(this, "open_ml_xml_parse_error");
		} catch (MissingValueException e) {
			throw new UserError(this, "open_ml_invalid_xml_error");
		}
		
	}

	@Override
	public List<ParameterType> getParameterTypes() {
		 List<ParameterType> types = super.getParameterTypes();
		types.add(new ParameterTypeInt(PARAMETER_TASK_ID, "OpenML Task Id", Integer.MIN_VALUE, Integer.MAX_VALUE));
		types.add(new ParameterTypeString(PARAMETER_NUMBER_OF_REPEATS, "Specifies the name of the macro, which delievers the current the number of repeats defined in the OpenML task. Use %{macro_name} to use the macro.", "num_repeats", false));
		types.add(new ParameterTypeString(PARAMETER_NUMBER_OF_FOLDS, "Specifies the name of the macro, which delievers the current the number of folds defined in the OpenML task. Use %{macro_name} to use the macro.", "num_folds", false));
		return types;
	}


}
