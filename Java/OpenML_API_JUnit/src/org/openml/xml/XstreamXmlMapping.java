package org.openml.xml;

import org.openml.xml.Authenticate;
import org.openml.xml.DataSetDescription;
import org.openml.xml.ApiError;
import org.openml.xml.Implementation;
import org.openml.xml.Run;
import org.openml.xml.UploadDataSet;
import org.openml.xml.UploadImplementation;
import org.openml.xml.UploadRun;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class XstreamXmlMapping {

	public static XStream getInstance() {
		XStream xstream = new XStream(new DomDriver("UFT-8", new NoNameCoder()));
		
		// data set description
		xstream.alias("oml:data_set_description", DataSetDescription.class);
		xstream.aliasAttribute(DataSetDescription.class, "oml", "xmlns:oml");
		
		xstream.addImplicitCollection(DataSetDescription.class, "creator", "oml:creator", String.class);
		xstream.addImplicitCollection(DataSetDescription.class, "contributor", "oml:contributor", String.class);
		
		xstream.aliasField("oml:id", DataSetDescription.class, "id");
		xstream.aliasField("oml:name", DataSetDescription.class, "name");
		xstream.aliasField("oml:version", DataSetDescription.class, "version");
		xstream.aliasField("oml:description", DataSetDescription.class, "description");
		xstream.aliasField("oml:format", DataSetDescription.class, "format");
		xstream.aliasField("oml:collection_date", DataSetDescription.class, "collection_date");
		xstream.aliasField("oml:language", DataSetDescription.class, "language");
		xstream.aliasField("oml:upload_date", DataSetDescription.class, "upload_date");
		xstream.aliasField("oml:licence", DataSetDescription.class, "licence");
		xstream.aliasField("oml:url", DataSetDescription.class, "url");
		xstream.aliasField("oml:row_id_attribute", DataSetDescription.class, "row_id_attribute");
		xstream.aliasField("oml:md5_checksum", DataSetDescription.class, "md5_checksum");
		
		// upload data set
		xstream.alias("oml:upload_data_set", UploadDataSet.class);
		xstream.aliasField("oml:id", UploadDataSet.class, "id");
		
		// implementation 
		xstream.alias("oml:implementation", Implementation.class);
		xstream.aliasAttribute(Implementation.class, "oml", "xmlns:oml");
		
		xstream.addImplicitCollection(Implementation.class, "creator", "oml:creator", String.class);
		xstream.addImplicitCollection(Implementation.class, "contributor", "oml:contributor", String.class);
		xstream.addImplicitCollection(Implementation.class, "bibliographical_reference", "oml:bibliographical_reference", Implementation.Bibliographical_reference.class);
		xstream.addImplicitCollection(Implementation.class, "parameter", "oml:parameter", Implementation.Parameter.class);

		xstream.aliasField("oml:id", Implementation.class, "id");
		xstream.aliasField("oml:fullName", Implementation.class, "fullName");
		xstream.aliasField("oml:name", Implementation.class, "name");
		xstream.aliasField("oml:version", Implementation.class, "version");
		xstream.aliasField("oml:uploader", Implementation.class, "uploader");
		xstream.aliasField("oml:upload_date", Implementation.class, "upload_date");
		xstream.aliasField("oml:description", Implementation.class, "description");
		xstream.aliasField("oml:licence", Implementation.class, "licence");
		xstream.aliasField("oml:language", Implementation.class, "language");
		xstream.aliasField("oml:full_description", Implementation.class, "full_description");
		xstream.aliasField("oml:installation_notes", Implementation.class, "installation_notes");
		xstream.aliasField("oml:dependencies", Implementation.class, "dependencies");
		xstream.aliasField("oml:components", Implementation.class, "components");
		
		xstream.aliasField("oml:source_url", Implementation.class, "source_url");
		xstream.aliasField("oml:source_format", Implementation.class, "source_format");
		xstream.aliasField("oml:source_md5", Implementation.class, "source_md5");
		
		xstream.aliasField("oml:binary_url", Implementation.class, "binary_url");
		xstream.aliasField("oml:binary_format", Implementation.class, "binary_format");
		xstream.aliasField("oml:binary_md5", Implementation.class, "binary_md5");
		
		// implementation component
		xstream.alias("oml:implementation_component", Implementation.Component.class);
		xstream.addImplicitCollection(Implementation.Component.class, "implementation", "oml:implementation", Implementation.class);
		
		// bibliographical reference
		xstream.alias("oml:bibliographical_reference", Implementation.Bibliographical_reference.class);
		xstream.aliasField("oml:citation", Implementation.Bibliographical_reference.class, "citation");
		xstream.aliasField("oml:url", Implementation.Bibliographical_reference.class, "url");
		
		// parameter
		xstream.alias("oml:parameter", Implementation.Parameter.class);
		xstream.aliasField("oml:name", Implementation.Parameter.class, "name");
		xstream.aliasField("oml:data_type", Implementation.Parameter.class, "data_type");
		xstream.aliasField("oml:default_value", Implementation.Parameter.class, "default_value");
		xstream.aliasField("oml:description", Implementation.Parameter.class, "description");
		
		// upload implementation
		xstream.alias("oml:upload_implementation", UploadImplementation.class);
		xstream.aliasField("oml:id", UploadImplementation.class, "id");
		
		// owned implementation
		xstream.addImplicitCollection(ImplementationOwned.class, "id", "oml:id", Integer.class);
		xstream.alias("oml:implementation_owned", ImplementationOwned.class);
		xstream.aliasField("oml:id", ImplementationOwned.class, "id");
		
		// delete implementation
		xstream.alias("oml:implementation_delete", ImplementationDelete.class);
		xstream.aliasField("oml:id", ImplementationDelete.class, "id");
		
		// generic error
		xstream.alias("oml:error", ApiError.class);
		xstream.aliasAttribute(ApiError.class, "oml", "xmlns:oml");
		xstream.aliasField("oml:code", ApiError.class, "code");
		xstream.aliasField("oml:message", ApiError.class, "message");
		xstream.aliasField("oml:additional_information", ApiError.class, "additional_information");
		
		// authenticate
		xstream.alias("oml:authenticate", Authenticate.class);
		xstream.aliasField("oml:session_hash", Authenticate.class, "sessionHash");
		xstream.aliasField("oml:valid_until", Authenticate.class, "validUntil");
		
		// run
		xstream.alias("oml:run", Run.class);
		xstream.aliasAttribute(Run.class, "oml", "xmlns:oml");
		xstream.addImplicitCollection(Run.class, "parameter_settings", "oml:parameter_setting", Run.Parameter_setting.class);
		
		xstream.aliasField("oml:task_id", Run.class, "task_id");
		xstream.aliasField("oml:implementation_id", Run.class, "implementation_id");
		xstream.aliasField("oml:error_message", Run.class, "error_message");
		xstream.aliasField("oml:name", Run.Parameter_setting.class, "name");
		xstream.aliasField("oml:component", Run.Parameter_setting.class, "component");
		xstream.aliasField("oml:value", Run.Parameter_setting.class, "value");
		
		// upload run
		xstream.alias("oml:upload_run", UploadRun.class);
		xstream.aliasField("oml:run_id", UploadRun.class, "run_id");
		
		return xstream;
	}
}
