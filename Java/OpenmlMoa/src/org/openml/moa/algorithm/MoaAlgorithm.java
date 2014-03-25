package org.openml.moa.algorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.ApiConnector;
import org.openml.apiconnector.xml.Implementation;
import org.openml.apiconnector.xml.ImplementationExists;
import org.openml.apiconnector.xml.Run;
import org.openml.apiconnector.xml.Run.Parameter_setting;
import org.openml.apiconnector.xml.UploadImplementation;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

import moa.classifiers.Classifier;
import moa.options.ClassOption;
import moa.options.FlagOption;
import moa.options.Option;
import moa.options.WEKAClassOption;

public class MoaAlgorithm {
	
	public static int getImplementationId( Implementation implementation, Classifier classifier, String hash ) throws Exception {
		try {
			// First ask OpenML whether this implementation already exists
			ImplementationExists result = ApiConnector.openmlImplementationExists( implementation.getName(), implementation.getExternal_version() );
			if(result.exists()) return result.getId();
		} catch( Exception e ) { /* Suppress Exception since it is totally OK.*/ }
		// It does not exist. Create it. 
		String xml = XstreamXmlMapping.getInstance().toXML( implementation );
		//System.err.println(xml);
		File implementationFile = Conversion.stringToTempFile( xml, implementation.getName(), "xml");
		File source = null;
		File binary = null;
		try { source = getFile( classifier, "src/", "java" ); } catch(IOException e) {}
		try { binary = getFile( classifier, "bin/", "class" ); } catch(IOException e) {}
		UploadImplementation ui = ApiConnector.openmlImplementationUpload(implementationFile, binary, source, hash);
		return ui.getId();
	}
	
	public static ArrayList<Run.Parameter_setting> getOptions( Implementation i, Option[] options ) {
		ArrayList<Run.Parameter_setting> result = new ArrayList<Run.Parameter_setting>();
		for( Option option : options ) {
			if( option instanceof FlagOption ) {
				FlagOption o = (FlagOption) option;
				result.add( new Parameter_setting(i.getId(), o.getCLIChar() + "", o.isSet() ? "true" : "false") );
			} else if( option instanceof ClassOption ) {
				ClassOption o = (ClassOption) option;
				if( o.getRequiredType().isAssignableFrom( Classifier.class ) ) {
					try {
						Classifier subclassifier = (Classifier) ClassOption.cliStringToObject( o.getValueAsCLIString(), o.getRequiredType(), null );
						String classPath = subclassifier.getClass().getName();
						String name = "moa." + classPath.substring( classPath.lastIndexOf('.') + 1 );
						// retrieve sub settings:
						Implementation subimplementation = i.getComponentByName( name );
						result.addAll( getOptions( subimplementation, subclassifier.getOptions().getOptionArray() ) );
					} catch (Exception e) {
						result.add( new Parameter_setting(i.getId(), option.getCLIChar() + "", option.getValueAsCLIString() ) );
						e.printStackTrace(); 
					}
				} else {
					result.add( new Parameter_setting(i.getId(), option.getCLIChar() + "", option.getValueAsCLIString() ) );
				}
			} else {
				result.add( new Parameter_setting(i.getId(), option.getCLIChar() + "", option.getValueAsCLIString() ) );
			}
		}
		
		return result;
	}
	
	public static Implementation create( Classifier classifier ) {
		String classPath = classifier.getClass().getName();
		String classifierName = classPath.substring( classPath.lastIndexOf('.') + 1 );
		String name = "moa." + classifierName;
		String version = "1.0"; //TODO: MOA does not support retrieval of version?
		String description = "Moa implementation of " + classifierName;
		String language = "English";
		String dependencies = "Moa_2014.03"; // TODO: No version information?
		
		Implementation i = new Implementation( name, dependencies + "_" + version, description, language, dependencies );
		for( Option option : classifier.getOptions().getOptionArray() ) {
			if( option instanceof FlagOption ) {
				FlagOption fo = (FlagOption) option;
				i.addParameter( fo.getCLIChar() + "", "flag", "false", fo.getName() + ": " + fo.getPurpose() );
			} else if( option instanceof ClassOption ) {
				ClassOption co = (ClassOption) option;
				i.addParameter(co.getCLIChar() + "", "baselearner", co.getDefaultCLIString(), co.getName() + ": " + co.getPurpose() );
				
				if( co.getRequiredType().isAssignableFrom( Classifier.class ) ) {
					try {
						Implementation subimplementation = create( (Classifier) ClassOption.cliStringToObject( co.getValueAsCLIString(), co.getRequiredType(), null ) );
						i.addComponent(co.getCLIChar() + "", subimplementation );
					} catch (Exception e) {	e.printStackTrace(); }
				}
			} else if( option instanceof WEKAClassOption ) {
				WEKAClassOption wco = (WEKAClassOption) option;
				i.addParameter(wco.getCLIChar() + "", "baselearner", wco.getDefaultCLIString(), wco.getName() + ": " + wco.getPurpose() );
				
				if( wco.getRequiredType().isAssignableFrom( weka.classifiers.Classifier.class ) ) {
					String weka_identifier = wco.getValueAsCLIString();
					String weka_classifier = weka_identifier.substring(0, weka_identifier.indexOf(' '));
					String weka_parameters = weka_identifier.substring(weka_identifier.indexOf(' ')+1);
					
					Implementation subimplementation;
					try {
						subimplementation = WekaAlgorithm.create( weka_classifier, weka_parameters );
						i.addComponent(wco.getCLIChar() + "", subimplementation );
					} catch (Exception e) { e.printStackTrace(); }
					
					
				}
			} else {
				i.addParameter( option.getCLIChar() + "", "option", option.getDefaultCLIString(), option.getName() + ": " + option.getPurpose() );
			}
		}
		
		return i;
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