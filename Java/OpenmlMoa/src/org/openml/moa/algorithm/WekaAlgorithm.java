package org.openml.moa.algorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;

import org.apache.commons.lang3.StringUtils;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.algorithms.OptionParser;
import org.openml.apiconnector.algorithms.ParameterType;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.Implementation;
import org.openml.apiconnector.xml.ImplementationExists;
import org.openml.apiconnector.xml.UploadImplementation;
import org.openml.apiconnector.xml.Implementation.Parameter;
import org.openml.apiconnector.xml.Run.Parameter_setting;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

import weka.classifiers.Classifier;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.RevisionHandler;
import weka.core.TechnicalInformationHandler;
import weka.core.Utils;
import weka.core.Version;

public class WekaAlgorithm {

	public static String getVersion(String algorithm) {
		String version = "undefined";
		try {
			RevisionHandler classifier = (RevisionHandler) Class.forName(algorithm).newInstance();
			version = classifier.getRevision();
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
	
	public static int getImplementationId( Implementation implementation, Classifier classifier, OpenmlConnector apiconnector ) throws Exception {
		try {
			// First ask OpenML whether this implementation already exists
			ImplementationExists result = apiconnector.implementationExists( implementation.getName(), implementation.getExternal_version() );
			if(result.exists()) return result.getId();
		} catch( Exception e ) { /* Suppress Exception since it is totally OK. */ }
		// It does not exist. Create it. 
		String xml = XstreamXmlMapping.getInstance().toXML( implementation );
		//System.err.println(xml);
		File implementationFile = Conversion.stringToTempFile( xml, implementation.getName(), "xml");
		File source = null;
		File binary = null;
		try { source = getFile( classifier, "src/", "java" ); } catch(IOException e) {}
		try { binary = getFile( classifier, "bin/", "class" ); } catch(IOException e) {}
		UploadImplementation ui = apiconnector.implementationUpload(implementationFile, binary, source);
		return ui.getId();
	}
	
	public static Implementation create( String classifier_name, String option_str ) throws Exception {
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
		
		Implementation i = new Implementation( name, dependencies + "_" + version, description, language, dependencies );
		
		@SuppressWarnings("unchecked")
		Enumeration<Option> parameters = ((OptionHandler) classifier).listOptions();
		while( parameters.hasMoreElements() ) {
			Option parameter = parameters.nextElement();
			if( parameter.name().trim().equals("") ) continue; // filter trash
			String defaultValue = "";
			String currentValue = "";
			if( parameter.numArguments() == 0 ) {
				defaultValue = Utils.getFlag(parameter.name(), defaultOptions) == true ? "true" : "";
				currentValue = Utils.getFlag(parameter.name(), currentOptions) == true ? "true" : "";
			} else {
				defaultValue = Utils.getOption(parameter.name(), defaultOptions);
				currentValue = Utils.getOption(parameter.name(), currentOptions);
			}
			
			String[] currentValueSplitted = currentValue.split(" ");
			boolean isSubimplementation = existingClass(currentValueSplitted[0]);
			if(isSubimplementation) {
				ParameterType type;
				Implementation subimplementation;
				if( currentValueSplitted.length > 1 ) {
					// Kernels etc. All parameters of the kernel are on the same currentOptions entry
					subimplementation = create( 
						currentValueSplitted[0], 
						StringUtils.join( OptionParser.removeFirstElement(currentValueSplitted), " " ) );
					type = ParameterType.KERNEL;
					
					i.addComponent( parameter.name(), subimplementation );
					i.addParameter( parameter.name(), type.getName(), currentValueSplitted[0], parameter.description() );
				} else {
					// Meta algorithms and stuff. All parameters follow from the hyphen in currentOptions
					subimplementation = create( 
						currentValueSplitted[0], 
						StringUtils.join( Utils.partitionOptions(currentOptions), " ") );
					type = ParameterType.BASELEARNER;
					
					i.addComponent( parameter.name(), subimplementation );
					i.addParameter( parameter.name(), type.getName(), currentValue, parameter.description() );
				}
			}
			
			if( !isSubimplementation ) {
				// if this parameter did contain a subimplementation, we already
				// added it. This is the other case, were we will have to decide
				// whether to add it. TODO: do something smart about it. 
				if( i.parameter_exists(parameter.name()) == false ) {
					ParameterType type = (parameter.numArguments() == 0 ) ? ParameterType.FLAG : ParameterType.OPTION;
					i.addParameter( parameter.name(), type.getName(), defaultValue, parameter.description() );
				}
			}
		}
		
		return i;
	}
	
	public static ArrayList<Parameter_setting> getParameterSetting( String[] parameters, Implementation implementation ) {
		ArrayList<Parameter_setting> settings = new ArrayList<Parameter_setting>();
		for( Parameter p : implementation.getParameter() ) {
			try {
				ParameterType type = ParameterType.fromString(p.getData_type());
				switch( type ) {
				case KERNEL:
					String kernelvalue = Utils.getOption(p.getName(), parameters);
					String[] kernelvalueSplitted = kernelvalue.split(" ");
					if( WekaAlgorithm.existingClass( kernelvalueSplitted[0] ) ) {
						String kernelname = kernelvalue.substring( 0, kernelvalue.indexOf(' ') );
						String[] kernelsettings = Utils.splitOptions(kernelvalue.substring(kernelvalue.indexOf(' ')+1));
						ArrayList<Parameter_setting> kernelresult = getParameterSetting( kernelsettings, implementation.getSubImplementation( p.getName() ) );
						settings.addAll( kernelresult );
						settings.add( new Parameter_setting( implementation.getId(), p.getName(), kernelname ) );
					} 
					break;
				case BASELEARNER:
					String baselearnervalue = Utils.getOption(p.getName(), parameters);
					if( WekaAlgorithm.existingClass( baselearnervalue ) ) {
						String[] baselearnersettings = Utils.partitionOptions( parameters );
						settings.addAll( getParameterSetting( baselearnersettings, implementation.getSubImplementation( p.getName() ) ) );
						settings.add( new Parameter_setting( implementation.getId(), p.getName(), baselearnervalue ) );
					}
					break;
				case OPTION:
					String optionvalue = Utils.getOption(p.getName(), parameters);
					if( optionvalue != "") {
						settings.add( new Parameter_setting( implementation.getId(), p.getName(), optionvalue ) );
					}
					break;
				case FLAG:
					boolean flagvalue = Utils.getFlag(p.getName(), parameters);
					if( flagvalue ) {
						settings.add( new Parameter_setting( implementation.getId(), p.getName(), "true" ) );
					}
					break;
				}
			} catch(Exception e) { /*Parameter not found. */ }
		}
		return settings;
	}
	
	public static boolean existingClass( String classpath ) {
		String classname = classpath.replace('.', '/');
		if(classpath.trim() == "") return false;
		return getFis( classname + ".class", "bin/" ) != null;
	}
	
	public static File getFile( Classifier classifier, String prefix, String extension ) throws IOException {
		Class<? extends Classifier> c = classifier.getClass();
		String sourcefile = c.getName().replace( '.', '/' );
		InputStream is = getFis( sourcefile + "." + extension, prefix );
		if( is == null ) throw new IOException( "Could not find resource " + sourcefile + "." + extension );
		BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
		StringBuilder totalSource = new StringBuilder();
		String line = br.readLine();
		while( line != null ) {
			totalSource.append( line + "\n" );
			line = br.readLine();
		}
		return Conversion.stringToTempFile(totalSource.toString(), c.getName(), extension);
	}
	
	private static InputStream getFis( String classname, String prefix ) {
		WekaAlgorithm loader = new WekaAlgorithm();
		InputStream is = null;
		
		is = loader.getClass().getResourceAsStream('/'+classname);
		
		if( is == null ) {
			try {
				File f = new File( prefix + classname );
				if( f.exists() ) {
					is = new FileInputStream( f );
				}
			} catch( IOException e ) { e.printStackTrace(); }
		}
		return is;
	}
}