package org.openml.apiconnector.xml;

import java.io.Serializable;

import org.openml.apiconnector.settings.Constants;

public class DataSetDescription implements Serializable {
	private static final long serialVersionUID = 987612341129L;
	private final String oml = Constants.OPENML_XMLNS;
	
	private Integer id;
	private String name;
	private String version;
	private String description;
	private String format;
	private String[] creator;
	private String[] contributor;
	private String collection_date;
	private String upload_date;
	private String language;
	private String licence;
	private String url;
	private String row_id_attribute;
	private String md5_checksum;
	
	/*
	 *	Constructor used from the Register Dataset Dialog. Set "null" for unspecified values that are optional.
	 */
	public DataSetDescription(
			String name, 
			String version, 
			String description, 
			String[] creator, 
			String[] contributor, 
			String format, 
			String collection_date, 
			String language, 
			String licence, 
			String row_id_attribute, 
			String md5_checksum) {
		this.id = null;
		this.name = name;
		this.version = version;
		this.description = description;
		this.creator = creator;
		this.contributor = contributor;
		this.format = format;
		this.collection_date = collection_date;
		this.language = language;
		this.upload_date = null;
		this.licence = licence;
		this.url = null;
		this.row_id_attribute = row_id_attribute;
		this.md5_checksum = md5_checksum;
	}

	public String getOml() {
		return oml;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String getDescription() {
		return description;
	}

	public String getFormat() {
		return format;
	}

	public String[] getCreator() {
		return creator;
	}

	public String[] getContributor() {
		return contributor;
	}

	public String getCollection_date() {
		return collection_date;
	}

	public String getUpload_date() {
		return upload_date;
	}

	public String getLanguage() {
		return language;
	}

	public String getLicence() {
		return licence;
	}

	public String getUrl() {
		return url;
	}
	
	public void unsetUrl() {
		this.url = null;
	}

	public String getRow_id_attribute() {
		return row_id_attribute;
	}

	public String getMd5_checksum() {
		return md5_checksum;
	}
	
	public String getCacheFileName() {
		return "dataset_" + getId() + "_" + getName() + ".arff";
	}
}
