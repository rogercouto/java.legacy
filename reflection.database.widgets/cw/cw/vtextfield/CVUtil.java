package cw.vtextfield;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CVUtil {

	 /**
     * Round a double value to a specified number of decimal
     * places.
     * @param val the value to be rounded.
     * @param digits the number of decimal places to round to.
     * @return val rounded to places decimal places.
     */
	public static double round(double val, int digits) {
        long factor = (long)Math.pow(10,digits);
        val = val * factor;
        long tmp = Math.round(val);
        return (double)tmp / factor;
    }
	
	public static Double getDouble(String number){
		DecimalFormat df = new DecimalFormat("0.00");
		try {
			return (Double) df.parse(number);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static Double getDouble(String number, int digits){
		DecimalFormat df = new DecimalFormat("0.00");
		try {
			return round((Double) df.parse(number), digits);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static String numberToString(Number n, int digits){
		String pattern = "0";
		if (digits > 0)
			pattern += ".";
		for (int i = 0; i < digits; i++) {
			pattern += "0";
		}
		DecimalFormat df = new DecimalFormat(pattern);
		return df.format(n);
	}
	
	public static Date txtToDate(String txtDate){
		if (!CVerify.validDate(txtDate))
			return null;
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		try {
			return df.parse(txtDate);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static Date txtToTime(String txtTime){
		if (!CVerify.validTime(txtTime))
			return null;
		DateFormat df = new SimpleDateFormat("HH:mm");
		try {
			return df.parse(txtTime);
		} catch (ParseException e) {
			return null;
		}
	}
	
}
