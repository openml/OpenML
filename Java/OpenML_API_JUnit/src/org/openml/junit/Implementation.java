package org.openml.junit;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.junit.Test;
import org.openml.algorithms.Conversion;
import org.openml.constants.Constants;
import org.openml.io.ApiConnector;
import org.openml.xml.Authenticate;
import org.openml.xml.ImplementationOwned;
import org.openml.xml.UploadImplementation;
import org.openml.xml.XstreamXmlMapping;

import com.thoughtworks.xstream.XStream;

public class Implementation {
	
	private String[] files = {
		"implementation.xml",
		"implementation_bit_complex.xml",
		"implementation_bit_complex2.xml",
		"implementation_mediocre.xml",
		"implementation_very_complex.xml",
		"implementation_very_complex2.xml"
	};
	
	@Test
	public void testA_ImplementationUpload() throws Exception {
		ArrayList<Integer> implementation_ids = new ArrayList<>();
		
		Authenticate authenticationToken = ApiConnector.openmlAuthenticate(Constants.USERNAME, Constants.PASSWORD );
		
		for( String file : files ) {
			try {
			File f = new File("data/implementation/"+file);
			UploadImplementation ul = ApiConnector.openmlImplementationUpload(f, f, f, authenticationToken.getSessionHash());
			implementation_ids.add(Integer.parseInt(ul.getId()));
			} catch(Exception e){}
		}
		
		assertTrue( implementation_ids.size() == files.length );
	}
	
	@Test 
	public void testB_ImplementationDownload() throws Exception {
		Authenticate authenticationToken = ApiConnector.openmlAuthenticate(Constants.USERNAME, Constants.PASSWORD );
		ImplementationOwned impOwned = ApiConnector.openmlImplementationOwned(authenticationToken.getSessionHash());
		XStream xstream = XstreamXmlMapping.getInstance();
		
		for( Integer implementation_id : impOwned.getIds() ) {
			org.openml.xml.Implementation implementation = ApiConnector.openmlImplementationGet(implementation_id+"");
			
			assertTrue( validateXSD( new File("data/xsd/implementation.xsd"), Conversion.stringToTempFile( xstream.toXML(implementation), "temp-implementation-"+implementation_id) ) );
			
		}
		
	}
	
	@Test
	public void testC_ImplementationDelete() throws Exception {
		
		Authenticate authenticationToken = ApiConnector.openmlAuthenticate(Constants.USERNAME, Constants.PASSWORD );
		ImplementationOwned impOwned = ApiConnector.openmlImplementationOwned(authenticationToken.getSessionHash());
		
		int deleted_total = -1;
		int previous_round = 1;
		
		while(previous_round > 0) {
			deleted_total += previous_round;
			previous_round = 0;
			for( Integer id : impOwned.getIds() ) {
				try {
					ApiConnector.openmlImplementationDelete(id, authenticationToken.getSessionHash());
					previous_round++;
				} catch( Exception e ) {};
			}
		}
		assertTrue(deleted_total == impOwned.getIds().length);
	}
	
	private boolean validateXSD(File xsd, File xml) {
		try
	    {
	        SchemaFactory factory = 
	            SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	        Schema schema = factory.newSchema(new StreamSource(xsd));
	        Validator validator = schema.newValidator();
	        validator.validate(new StreamSource(xml));
	        return true;
	    }
	    catch(Exception ex)
	    {
	        return false;
	    }
	}
}
