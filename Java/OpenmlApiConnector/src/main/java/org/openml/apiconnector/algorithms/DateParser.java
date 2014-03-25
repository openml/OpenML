package org.openml.apiconnector.algorithms;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class DateParser {

	public static final DateFormat humanReadable = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	public static final DateFormat defaultOrder  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
