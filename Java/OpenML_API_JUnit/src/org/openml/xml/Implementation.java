package org.openml.xml;

import org.apache.commons.lang3.ArrayUtils;
import org.openml.constants.Constants;

public class Implementation {
	private final String oml = Constants.OPENML_XMLNS;
	
	private Integer id;
	private String fullName;
	private Integer uploader;
	private String name;
	private String version;
	private String description;
	private String[] creator;
	private String[] contributor;
	private String upload_date;
	private String licence;
	private String language;
	private String full_description;
	private String installation_notes;
	private String dependencies;
	private Bibliographical_reference[] bibliographical_reference;
	private String implement;
	private Parameter[] parameter;
	private Component components;
	private String source_url;
	private String binary_url;
	private String source_format;
	private String binary_format;
	private String source_md5;
	private String binary_md5;

	public Implementation(String name, String version, String description) {
		this.name = name;
		this.version = version;
		this.description = description;
	}

	public Implementation(String name, String version, String description,
			String[] creator, String[] contributor, String licence,
			String language, String full_description,
			String installation_notes, String dependencies) {
		super();
		this.name = name;
		this.version = version;
		this.description = description;
		this.creator = creator;
		this.contributor = contributor;
		this.licence = licence;
		this.language = language;
		this.full_description = full_description;
		this.installation_notes = installation_notes;
		this.dependencies = dependencies;
	}
	
	public Integer getId() {
		return id;
	}
	
	public String getFullName() {
		return fullName;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}
	
	public Integer getUploader() {
		return uploader;
	}
	
	public String getUploadDate() {
		return upload_date;
	}

	public String getDescription() {
		return description;
	}

	public String[] getCreator() {
		return creator;
	}

	public String[] getContributor() {
		return contributor;
	}

	public String getLicence() {
		return licence;
	}

	public String getLanguage() {
		return language;
	}

	public String getFull_description() {
		return full_description;
	}

	public String getInstallation_notes() {
		return installation_notes;
	}

	public String getDependencies() {
		return dependencies;
	}

	public Bibliographical_reference[] getBibliographical_reference() {
		return bibliographical_reference;
	}

	public Parameter[] getParameter() {
		return parameter;
	}

	public String getSource_format() {
		return source_format;
	}

	public String getBinary_format() {
		return binary_format;
	}

	public String getSource_md5() {
		return source_md5;
	}

	public String getBinary_md5() {
		return binary_md5;
	}

	public String getOml() {
		return oml;
	}
	
	public void addParameter(String name, String data_type, String default_value, String description) {
		Parameter p = new Parameter(name, data_type, default_value, description);
		this.parameter = ArrayUtils.addAll(this.parameter,p );
	}

	public static class Bibliographical_reference {
		private String citation;
		private String url;

		public Bibliographical_reference(String citation, String url) {
			this.citation = citation;
			this.url = url;
		}

		public String getCitation() {
			return citation;
		}

		public String getUrl() {
			return url;
		}
	}

	public static class Parameter {
		private String name;
		private String data_type;
		private String default_value;
		private String description;

		public Parameter(String name, String data_type, String default_value,
				String description) {
			this.name = name;
			this.data_type = data_type;
			this.default_value = default_value;
			this.description = description;
		}

		public String getName() {
			return name;
		}

		public String getData_type() {
			return data_type;
		}

		public String getDefault_value() {
			return default_value;
		}

		public String getDescription() {
			return description;
		}
	}
	
	public class Component {
		Implementation[] implementation;
	}
}