package openml.io;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import openml.algorithms.DateParser;
import openml.settings.Constants;
import openml.xml.Authenticate;

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
