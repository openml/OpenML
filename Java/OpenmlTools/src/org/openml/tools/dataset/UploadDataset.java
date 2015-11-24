package org.openml.tools.dataset;

import java.io.File;

import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.UploadDataSet;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

import weka.core.Instances;
import weka.core.converters.ArffLoader;

import com.thoughtworks.xstream.XStream;

public class UploadDataset {

	private static final XStream xstream = XstreamXmlMapping.getInstance();
	private static final ArffLoader loader = new ArffLoader();
	private static final String directory = "/Users/jan/Desktop/test/";
	
	private final OpenmlConnector apiconnector;
	
	public static void main( String[] args ) throws Exception {
		
		new UploadDataset( directory );
	}
	
	public UploadDataset( String directory ) throws Exception {
		Config config = new Config();
		if( config.getServer() != null ) {
			apiconnector = new OpenmlConnector( config.getServer(), config.getApiKey() );
		} else { 
			apiconnector = new OpenmlConnector( config.getApiKey() );
		}
		
		File dir = new File( directory );
		
		if( dir.isDirectory() == false ) {
			upload( dir ); // dir is actually not a dir, in this case
		} else {
			for( File f : dir.listFiles() ) {
				try {
					System.out.println( "Dataset: " + f.getAbsolutePath() );
					
					upload(	f );
				} catch( Exception e ) {
					e.printStackTrace();
					System.out.println( "Failed. " );
				}
			}
		}
	}
	
	private void upload( File datasetFile ) throws Exception {
		loader.setFile( datasetFile );
		Instances dataset = new Instances( loader.getStructure() );
		
		String name = datasetFile.getName();
		String description = "Automated file upload of " + datasetFile.getName();
		String format = datasetFile.getName().substring( datasetFile.getName().lastIndexOf('.') + 1 );
		String target = dataset.attribute( dataset.numAttributes() - 1 ).name();
		
		DataSetDescription dsd = new DataSetDescription( name, description, format, target );
		String description_xml = xstream.toXML( dsd );
		System.out.println( description_xml );
		
		File desc = Conversion.stringToTempFile( description_xml, "description", "xml");
		
		UploadDataSet ud = apiconnector.dataUpload(desc, datasetFile );
		System.out.println( "Uploaded " + name + " with id " + xstream.toXML( ud.getId() ) );
	}
}