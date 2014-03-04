package openml.algorithms;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class DateParser {

	public static long mysqlDateToTimeStamp(String mysqlTime) throws ParseException {
		Calendar cal = Calendar.getInstance();
		//Date utilDate = new Date();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
	    cal.setTime(sdf.parse(mysqlTime));
	    //System.out.println("Valid until: " + sdf.format(cal.getTime()));
	    //System.out.println("Now: " + sdf.format(utilDate.getTime()));
	    return cal.getTime().getTime();
	}
}
