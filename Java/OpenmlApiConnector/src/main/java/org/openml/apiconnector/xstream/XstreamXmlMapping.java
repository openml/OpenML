package org.openml.apiconnector.xstream;

import org.openml.apiconnector.xml.ImplementationDelete;
import org.openml.apiconnector.xml.ImplementationExists;
import org.openml.apiconnector.xml.ImplementationOwned;
import org.openml.apiconnector.xml.Authenticate;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.ApiError;
import org.openml.apiconnector.xml.Implementation;
import org.openml.apiconnector.xml.Job;
import org.openml.apiconnector.xml.Run;
import org.openml.apiconnector.xml.Task;
import org.openml.apiconnector.xml.UploadDataSet;
import org.openml.apiconnector.xml.UploadImplementation;
import org.openml.apiconnector.xml.UploadRun;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;
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
		xstream.addImplicitCollection(Implementation.class, "component", "oml:component", Implementation.Component.class);
		
		xstream.aliasField("oml:id", Implementation.class, "id");
		xstream.aliasField("oml:fullName", Implementation.class, "fullName");
		xstream.aliasField("oml:name", Implementation.class, "name");
		xstream.aliasField("oml:version", Implementation.class, "version");
		xstream.aliasField("oml:external_version", Implementation.class, "external_version");
		xstream.aliasField("oml:uploader", Implementation.class, "uploader");
		xstream.aliasField("oml:upload_date", Implementation.class, "upload_date");
		xstream.aliasField("oml:description", Implementation.class, "description");
		xstream.aliasField("oml:licence", Implementation.class, "licence");
		xstream.aliasField("oml:language", Implementation.class, "language");
		xstream.aliasField("oml:full_description", Implementation.class, "full_description");
		xstream.aliasField("oml:installation_notes", Implementation.class, "installation_notes");
		xstream.aliasField("oml:dependencies", Implementation.class, "dependencies");
		
		xstream.aliasField("oml:source_url", Implementation.class, "source_url");
		xstream.aliasField("oml:source_format", Implementation.class, "source_format");
		xstream.aliasField("oml:source_md5", Implementation.class, "source_md5");
		
		xstream.aliasField("oml:binary_url", Implementation.class, "binary_url");
		xstream.aliasField("oml:binary_format", Implementation.class, "binary_format");
		xstream.aliasField("oml:binary_md5", Implementation.class, "binary_md5");
		
		// implementation component
		xstream.alias("oml:implementation_component", Implementation.Component.class);
		xstream.aliasField("oml:identifier", Implementation.Component.class, "identifier");
		xstream.aliasField("oml:implementation", Implementation.Component.class, "implementation");
		
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
		
		// implementation exists
		xstream.alias("oml:implementation_exists", ImplementationExists.class);
		xstream.aliasField("oml:exists", ImplementationExists.class, "exists");
		xstream.aliasField("oml:id", ImplementationExists.class, "id");
		
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
		
		// tasks
		xstream.alias("oml:task", Task.class);
		xstream.alias("oml:data_set", Task.Input.Data_set.class);
		xstream.alias("oml:estimation_procedure", Task.Input.Estimation_procedure.class);
		xstream.alias("oml:feature", Task.Output.Predictions.Feature.class);
		xstream.aliasAttribute(Task.class, "oml", "xmlns:oml");
		
		xstream.addImplicitCollection(Task.class, "inputs", "oml:input", Task.Input.class);
		xstream.addImplicitCollection(Task.class, "outputs", "oml:output", Task.Output.class);
		xstream.addImplicitCollection(Task.Input.Estimation_procedure.class, "parameters", "oml:parameter", Task.Input.Estimation_procedure.Parameter.class);
		xstream.addImplicitCollection(Task.Input.Evaluation_measures.class, "evaluation_measure", "oml:evaluation_measure", String.class);
		xstream.addImplicitCollection(Task.Output.Predictions.class, "features", "oml:feature", Task.Output.Predictions.Feature.class);
		
		xstream.aliasField("oml:task_id", Task.class, "task_id");
		xstream.aliasField("oml:task_type", Task.class, "task_type");
		xstream.aliasField("oml:data_set_id", Task.Input.Data_set.class, "data_set_id");
		xstream.aliasField("oml:target_feature", Task.Input.Data_set.class, "target_feature");
		xstream.aliasField("oml:data_set", Task.Input.class, "data_set");
		xstream.aliasField("oml:type", Task.Input.Estimation_procedure.class, "type");
		xstream.aliasField("oml:data_splits_url", Task.Input.Estimation_procedure.class, "data_splits_url");
		xstream.aliasField("oml:estimation_procedure", Task.Input.class, "estimation_procedure");
		xstream.aliasField("oml:evaluation_measure", Task.Input.Evaluation_measures.class, "evaluation_measure");
		xstream.aliasField("oml:evaluation_measures", Task.Input.class, "evaluation_measures");
		xstream.aliasField("oml:predictions", Task.Output.class, "predictions");
		xstream.aliasField("oml:format", Task.Output.Predictions.class, "format");
		
		xstream.useAttributeFor(Task.Input.class, "name");
		xstream.useAttributeFor(Task.Output.class, "name");
		xstream.useAttributeFor(Task.Input.Estimation_procedure.Parameter.class, "name");
		xstream.useAttributeFor(Task.Output.Predictions.Feature.class, "name");
		xstream.useAttributeFor(Task.Output.Predictions.Feature.class, "type");
		
		xstream.registerConverter(new ToAttributedValueConverter(Task.Input.Estimation_procedure.Parameter.class, xstream.getMapper(), xstream.getReflectionProvider(), xstream.getConverterLookup(), "value"));
		
		// run
		xstream.alias("oml:run", Run.class);
		xstream.aliasAttribute(Run.class, "oml", "xmlns:oml");
		xstream.addImplicitCollection(Run.class, "parameter_settings", "oml:parameter_setting", Run.Parameter_setting.class);
		
		xstream.aliasField("oml:task_id", Run.class, "task_id");
		xstream.aliasField("oml:implementation_id", Run.class, "implementation_id");
		xstream.aliasField("oml:error_message", Run.class, "error_message");
		xstream.aliasField("oml:setup_string", Run.class, "setup_string");
		xstream.aliasField("oml:input_data", Run.class, "input_data");
		xstream.aliasField("oml:output_data", Run.class, "output_data");
		
		xstream.aliasField("oml:name", Run.Parameter_setting.class, "name");
		xstream.aliasField("oml:component", Run.Parameter_setting.class, "component");
		xstream.aliasField("oml:value", Run.Parameter_setting.class, "value");
		
		xstream.addImplicitCollection( Run.Data.class, "dataset", "oml:dataset", Run.Data.Dataset.class);
		xstream.addImplicitCollection( Run.Data.class, "evaluation", "oml:evaluation", Run.Data.Evaluation.class);

		xstream.aliasField("oml:did", Run.Data.Dataset.class, "did");
		xstream.aliasField("oml:name", Run.Data.Dataset.class, "name");
		xstream.aliasField("oml:url", Run.Data.Dataset.class, "url");

		xstream.aliasField("oml:did", Run.Data.Evaluation.class, "did");
		xstream.aliasField("oml:name", Run.Data.Evaluation.class, "name");
		xstream.aliasField("oml:implementation", Run.Data.Evaluation.class, "implementation");
		xstream.aliasField("oml:value", Run.Data.Evaluation.class, "value");
		xstream.aliasField("oml:array_data", Run.Data.Evaluation.class, "array_data");
		
		xstream.useAttributeFor(Run.Data.Evaluation.class, "repeat");
		xstream.useAttributeFor(Run.Data.Evaluation.class, "fold");
		xstream.useAttributeFor(Run.Data.Evaluation.class, "sample");
		
		// upload run
		xstream.alias("oml:upload_run", UploadRun.class);
		xstream.aliasField("oml:run_id", UploadRun.class, "run_id");
		
		// run getjob
		xstream.alias("oml:job", Job.class);
		xstream.aliasField("oml:task_id", Job.class, "task_id");
		xstream.aliasField("oml:learner", Job.class, "learner");
		
		return xstream;
	}
}
