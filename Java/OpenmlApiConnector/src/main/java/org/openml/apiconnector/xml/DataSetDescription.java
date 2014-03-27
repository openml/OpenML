/*
 *  OpenmlApiConnector - Java integration of the OpenML Web API
 *  Copyright (C) 2014 
 *  @author Jan N. van Rijn (j.n.van.rijn@liacs.leidenuniv.nl)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  
 */
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
	private String default_target_attribute;
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
			String default_target_attribute,
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
		this.default_target_attribute = default_target_attribute;
		this.md5_checksum = md5_checksum;
	}
	
	public DataSetDescription(
			String name, 
			String description, 
			String format, 
			String default_target_attribute ) {
		this.id = null;
		this.name = name;
		this.version = null;
		this.description = description;
		this.creator = null;
		this.contributor = null;
		this.format = format;
		this.collection_date = null;
		this.language = null;
		this.upload_date = null;
		this.licence = null;
		this.url = null;
		this.row_id_attribute = null;
		this.default_target_attribute = default_target_attribute;
		this.md5_checksum = null;
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

	public String getDefault_target_attribute() {
		return default_target_attribute;
	}

	public String getMd5_checksum() {
		return md5_checksum;
	}
	
	public String getCacheFileName() {
		return "dataset_" + getId() + "_" + getName() + ".arff";
	}
}