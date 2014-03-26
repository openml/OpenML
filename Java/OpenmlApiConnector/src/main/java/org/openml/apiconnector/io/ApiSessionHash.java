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
package org.openml.apiconnector.io;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import org.openml.apiconnector.algorithms.DateParser;
import org.openml.apiconnector.settings.Constants;
import org.openml.apiconnector.xml.Authenticate;

public class ApiSessionHash implements Serializable {
	
	private static final long serialVersionUID = 7831245113631L;
	private String username;
	private String password;
	private String sessionHash;
	private long validUntil;
	
	public ApiSessionHash() {
		sessionHash = null;
		username = null;
	}
	
	public boolean isValid() {
		Date utilDate = new Date();
		return validUntil > utilDate.getTime() + Constants.DEFAULT_TIME_MARGIN;
	}
	
	public boolean set( String username, String password ) throws ParseException {
		this.username = username;
		this.password = password;
		
		return update();
	}
	
	public boolean update() {
		try {
			Authenticate auth = ApiConnector.openmlAuthenticate(username, password);
			this.validUntil = DateParser.mysqlDateToTimeStamp(auth.getValidUntil());
			this.sessionHash = auth.getSessionHash();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public String getUsername() {
		return username;
	}

	public String getSessionHash() {
		if( isValid() == false )
			update();
		return sessionHash;
	}
}
