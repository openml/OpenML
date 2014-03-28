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
package org.openml.apiconnector.algorithms;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class DateParser {

	public static final DateFormat humanReadable = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	public static final DateFormat defaultOrder  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * Parses MySQL date format to Unix Time Stamp.
	 * 
	 * @param mysqlTime - A string representing the date field.
	 * @return Unix Time Stamp of mysqlTime
	 * @throws ParseException
	 */
	public static long mysqlDateToTimeStamp(String mysqlTime) throws ParseException {
		DateFormat current = (DateFormat) defaultOrder.clone();
		Calendar cal = Calendar.getInstance();
	    current.setTimeZone(TimeZone.getTimeZone("GMT"));
	    cal.setTime(current.parse(mysqlTime));
	    //System.out.println("Valid until: " + sdf.format(cal.getTime()));
	    //System.out.println("Now: " + sdf.format(utilDate.getTime()));
	    return cal.getTime().getTime();
	}
}
