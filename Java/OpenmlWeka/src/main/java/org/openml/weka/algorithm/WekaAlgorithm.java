package org.openml.weka.algorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.algorithms.OptionParser;
import org.openml.apiconnector.algorithms.ParameterType;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.Flow;
import org.openml.apiconnector.xml.Run;
import org.openml.apiconnector.xml.Flow.Parameter;
import org.openml.apiconnector.xml.FlowExists;
import org.openml.apiconnector.xml.Run.Parameter_setting;
import org.openml.apiconnector.xml.SetupExists;
import org.openml.apiconnector.xml.UploadFlow;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

import weka.classifiers.Classifier;
import weka.classifiers.functions.supportVector.Kernel;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.RevisionHandler;
import weka.core.TechnicalInformationHandler;
import weka.core.Utils;
import weka.core.Version;
import weka.core.setupgenerator.AbstractParameter;
import weka.experiment.SplitEvaluator;

public class WekaAlgorithm {
	
	public static String getVersion(String algorithm) {
		String version = "undefined";
		try {
			RevisionHandler classifier = (RevisionHandler) Class.forName(algorithm).newInstance();
			if( StringUtils.isAlphanumeric( classifier.getRevision() )) {
				version = classifier.getRevision();
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return version;
	}
	
	public static Integer getSetupId(String classifierName, String option_str, OpenmlConnector apiconnector) throws Exception {
		
		// first find flow. if the flow doesn't exist, neither does the setup.
		Flow find = WekaAlgorithm.create(classifierName, option_str, null);
		int flow_id = -1;
		try {
			FlowExists result = apiconnector.flowExists(find.getName(), find.getExternal_version());
			if(result.exists()) { 
				flow_id = result.getId(); 
			} else {
				return null;
			}
		} catch( Exception e ) {
			return null;
		}
		Flow implementation = apiconnector.flowGet(flow_id);
		
		String[] params = Utils.splitOptions(option_str);
		List<Parameter_setting> list = WekaAlgorithm.getParameterSetting(params, implementation);
		
		// now create the setup object
		Run run = new Run(null, null, implementation.getId(), null, list.toArray(new Parameter_setting[list.size()]), null);
		File setup = Conversion.stringToTempFile(XstreamXmlMapping.getInstance().toXML(run), "setup", "xml");
		SetupExists se = apiconnector.setupExists(setup);
		
		if (se.exists()) {
			return se.getId();
		} else {
			return null;
		}
	}
	
	public static int getImplementationId(Flow implementation, Classifier classifier, OpenmlConnector apiconnector) throws Exception {
		try {
			// First ask OpenML whether this implementation already exists
			FlowExists result = apiconnector.flowExists(implementation.getName(), implementation.getExternal_version());
			if(result.exists()) return result.getId();
		} catch( Exception e ) { /* Suppress Exception since it is totally OK. */ }
		// It does not exist. Create it. 
		String xml = XstreamXmlMapping.getInstance().toXML(implementation);
		//System.err.println(xml);
		File implementationFile = Conversion.stringToTempFile(xml, implementation.getName(), "xml");
		File source = null;
		File binary = null;
		try { source = getFile(classifier, "src/", "java"); } catch(IOException e) {}
		try { binary = getFile(classifier, "bin/", "class"); } catch(IOException e) {}
		UploadFlow ui = apiconnector.flowUpload(implementationFile, binary, source);
		return ui.getId();
	}

	public static Flow create(String classifier_name, String option_str, String[] tags) throws Exception {
		Object classifier = Class.forName(classifier_name).newInstance();
		String[] currentOptions = Utils.splitOptions( option_str );
		String[] defaultOptions = ((OptionHandler) classifier).getClass().newInstance().getOptions();
		
		String classPath = classifier.getClass().getName();
		String classifierName = classPath.substring( classPath.lastIndexOf('.') + 1 );
		String name = "weka." + classifierName;
		String version = getVersion(classifier_name);
		String description = "Weka implementation of " + classifierName;
		String language = "English";
		String dependencies = "Weka_" + Version.VERSION;
		
		if( classifier instanceof TechnicalInformationHandler ) {
			description = ((TechnicalInformationHandler) classifier).getTechnicalInformation().toString();
		}
		
		Flow i = new Flow( name, classifier.getClass().getName(), dependencies + "_" + version, description, language, dependencies );
		if (tags != null) {
			for(String tag : tags) {
				i.addTag(tag);
			}
		}
		
		Enumeration<Option> parameters = ((OptionHandler) classifier).listOptions();
		while(parameters.hasMoreElements()) {
			Option parameter = parameters.nextElement();
			if(parameter.name().trim().equals("")) continue; // filter trash
			String defaultValue = "";
			String currentValue = "";
			if(parameter.numArguments() == 0) {
				defaultValue = Utils.getFlag(parameter.name(), defaultOptions) == true ? "true" : "";
				currentValue = Utils.getFlag(parameter.name(), currentOptions) == true ? "true" : "";
			} else {
				defaultValue = Utils.getOption(parameter.name(), defaultOptions);
				currentValue = Utils.getOption(parameter.name(), currentOptions);
			}
			
			String[] currentValueSplitted = currentValue.split(" ");
			
			try {
				Object parameterObject = Class.forName(currentValueSplitted[0]).newInstance();
				ParameterType type;
				Flow subimplementation;
				
				if(parameterObject instanceof Kernel) {
					// Kernels etc. All parameters of the kernel are on the same currentOptions entry
					subimplementation = create( 
						currentValueSplitted[0], 
						StringUtils.join(OptionParser.removeFirstElement(currentValueSplitted), " "), tags );
					type = ParameterType.KERNEL;
					
					i.addComponent(parameter.name(), subimplementation);
					i.addParameter(parameter.name(), type.getName(), currentValueSplitted[0], parameter.description());
				} else if (parameterObject instanceof Classifier) {
					// Meta algorithms and stuff. All parameters follow from the hyphen in currentOptions
					subimplementation = create( 
						currentValueSplitted[0], 
						StringUtils.join( Utils.partitionOptions(currentOptions), " "), tags);
					type = ParameterType.BASELEARNER;
					
					i.addComponent(parameter.name(), subimplementation);
					i.addParameter(parameter.name(), type.getName(), currentValue, parameter.description());
				} else if (parameterObject instanceof AbstractParameter) { 
					// TODO: The statement above will only be correctly executed if
					// the MultiSearch package is installed. We *should* surround it
					// with a try/catch(NoClassDefFoundError) block. Complicated
					// construction, and for now it will not cause problems. TODO for later
					type = ParameterType.ARRAY;
					i.addParameter(parameter.name(), type.getName(), null, parameter.description());
				} else {
					Exception current = new ClassNotFoundException("Parameter class found, but no known procedure to handle it found. Will be handled as plain: " + currentValueSplitted[0]);
					Conversion.log("Warning","FlowCreation",current.getMessage());
					throw current;
				}
			} catch(ClassNotFoundException e) {
				// if this parameter did contain a subimplementation, we already
				// added it. This is the other case, were we will have to decide
				// whether to add it. TODO: do something smart about it. 
				if(i.parameter_exists(parameter.name()) == false) {
					ParameterType type = (parameter.numArguments() == 0) ? ParameterType.FLAG : ParameterType.OPTION;
					i.addParameter(parameter.name(), type.getName(), defaultValue, parameter.description());
				}
			}
		}
		
		return i;
	}
	
	public static ArrayList<Parameter_setting> getParameterSetting(String[] parameters, Flow implementation) {
		ArrayList<Parameter_setting> settings = new ArrayList<Parameter_setting>();
		if (implementation.getParameter() != null) {
			for(Parameter p : implementation.getParameter()) {
				try {
					ParameterType type = ParameterType.fromString(p.getData_type());
					switch(type) {
					case KERNEL:
						String kernelvalue = Utils.getOption(p.getName(), parameters);
						try {
							String kernelname = kernelvalue.substring(0, kernelvalue.indexOf(' '));
							String[] kernelsettings = Utils.splitOptions(kernelvalue.substring(kernelvalue.indexOf(' ')+1));
							ArrayList<Parameter_setting> kernelresult = getParameterSetting(kernelsettings, implementation.getSubImplementation(p.getName()));
							settings.addAll(kernelresult);
							settings.add(new Parameter_setting(implementation.getId(), p.getName(), kernelname));
						} catch(ClassNotFoundException e) {}
						break;
					case BASELEARNER:
						String baselearnervalue = Utils.getOption(p.getName(), parameters);
						try {
							String[] baselearnersettings = Utils.partitionOptions(parameters);
							settings.addAll(getParameterSetting(baselearnersettings, implementation.getSubImplementation(p.getName())));
							settings.add(new Parameter_setting(implementation.getId(), p.getName(), baselearnervalue));
						} catch(ClassNotFoundException e) {}
						break;
					case OPTION:
						String optionvalue = Utils.getOption(p.getName(), parameters);
						if(optionvalue != "") {
							settings.add(new Parameter_setting(implementation.getId(), p.getName(), optionvalue));
						}
						break;
					case FLAG:
						boolean flagvalue = Utils.getFlag(p.getName(), parameters);
						if(flagvalue) {
							settings.add(new Parameter_setting(implementation.getId(), p.getName(), "true"));
						}
						break;
					case ARRAY:
						List<String> values = new ArrayList<String>();
						String currentvalue = Utils.getOption(p.getName(), parameters);
						while (!currentvalue.equals("")) {
							values.add(currentvalue);
							currentvalue = Utils.getOption(p.getName(), parameters);
						}
						
						if(values.size() > 0) {
							settings.add(new Parameter_setting(implementation.getId(), p.getName(), values.toString()));
						}
						break;
					}	
				} catch(Exception e) {/*Parameter not found. */}
			}
		}
		return settings;
	}
	
	public static File getFile(Classifier classifier, String prefix, String extension) throws IOException {
		Class<? extends Classifier> c = classifier.getClass();
		String sourcefile = c.getName().replace('.', '/');
		InputStream is = getFis( sourcefile + "." + extension, prefix );
		if(is == null) throw new IOException("Could not find resource " + sourcefile + "." + extension);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuilder totalSource = new StringBuilder();
		String line = br.readLine();
		while(line != null) {
			totalSource.append(line + "\n");
			line = br.readLine();
		}
		return Conversion.stringToTempFile(totalSource.toString(), c.getName(), extension);
	}

	private static InputStream getFis(String classname, String prefix) {
		WekaAlgorithm loader = new WekaAlgorithm();
		InputStream is = null;
		
		is = loader.getClass().getResourceAsStream('/'+classname);
		
		if(is == null) {
			try {
				File f = new File(prefix + classname);
				if(f.exists()) {
					is = new FileInputStream(f);
				}
			} catch( IOException e ) { e.printStackTrace(); }
		}
		return is;
	}
	
	public static File classifierSerializedToFile(Classifier cls, Integer task_id) throws IOException {
		File file = File.createTempFile("WekaSerialized_" + cls.getClass().getName(), ".model");
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
		oos.writeObject(cls);
		oos.flush();
		oos.close();
		file.deleteOnExit();
		return file;
	}
	
	public static Map<String, Object> splitEvaluatorToMap(SplitEvaluator se, Object[] results) {
		Map<String, Object> splitEvaluatorResults = new HashMap<String, Object>();
		String[] seResultNames = se.getResultNames();
		
		for(int i = 0; i < seResultNames.length; ++i) {
			splitEvaluatorResults.put(seResultNames[i], results[i]);
		}
		
		return splitEvaluatorResults;
	}
}
