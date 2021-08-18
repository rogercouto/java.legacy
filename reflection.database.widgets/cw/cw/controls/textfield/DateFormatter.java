package cw.controls.textfield;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateFormatter extends MaskFormatter {

	private char divider = ' ';
	private String datePattern;
	
	public DateFormatter(String pattern) {
		super(pattern.replaceAll("[d;M;y;H;m;s]", "#"));
		this.datePattern = pattern;
	}
	public DateFormatter(String pattern, char blankChar) {
		super(pattern.replaceAll("[d;M;y;H;m;s]", "#"), blankChar);
		this.datePattern = pattern;
	}

	@Override
	public String getPattern() {
		return datePattern;
	}
	@Override
	public void setPattern(String pattern){
		if (text == null)
			this.datePattern = pattern;
	}
	public char getDivider() {
		return divider;
	}
	public void setDivider(char divider) {
		if (text == null)
			this.divider = divider;
	}

	@Override
	public String getText(){
		return text.getText();
	}
	
	public Date getDate(){
		if (!validate(getText(), datePattern))
			return null;
		DateFormat df = new SimpleDateFormat(datePattern);
		try {
			return df.parse(getText());
		} catch (ParseException e) {
		}
		return null;
	}
	
	public Validator getValidator(){
		return new Validator(){
			@Override
			public boolean verifyText(String string) {
				if (isEmpty())
					return true;
				if (!checkDate())
					return false;
				return validate(string, datePattern);
			}
		};
	}
	
	@Override
	public boolean isEmpty(){
		return (super.getText().trim().length() == 0);
	}
	
	private boolean checkDate(){
		String string = super.getText();
		if (string.length() != getVarLength())
			return false;
		return true;
	}
	
	public boolean validate(String dateTime,String pattern){
		if (getVarLength() != unmaskString().length())
			return false;
		String[] parts = getParts(dateTime,pattern);
		DateFormat df = new SimpleDateFormat(pattern);
		Calendar cal = new GregorianCalendar();  
		try {
			cal.setTime(df.parse(dateTime));
			if (parts[0] != null && (new Integer(parts[0])).intValue() != (new  
					Integer(cal.get(Calendar.DAY_OF_MONTH))).intValue() ) {  
				return(false);  
			} else if (parts[1] != null && (new Integer(parts[1])).intValue() != (new  
					Integer(cal.get(Calendar.MONTH)+1)).intValue() ) {  
				return(false);  
			} else if (parts[2] != null && (new Integer(parts[2])).intValue() != (new  
					Integer(cal.get(Calendar.YEAR))).intValue() ) {  
				return(false);  
			} else if (parts[3] != null && (new Integer(parts[3])).intValue() != (new  
					Integer(cal.get(Calendar.HOUR_OF_DAY))).intValue() ) {  
				return(false);  
			} else if (parts[4] != null && (new Integer(parts[4])).intValue() != (new  
					Integer(cal.get(Calendar.MINUTE))).intValue() ) {  
				return(false);  
			} else if (parts[5] != null && (new Integer(parts[5])).intValue() != (new  
					Integer(cal.get(Calendar.SECOND))).intValue() ) {
				return(false);  
			} 
			return true;
		} catch (ParseException e) {
			return false;
		} 
	}
	
	private String[] getParts(String dateTime,String pattern){
		char[] chars = {'d','M','y','H','m','s'};  
		String[] parts = new String[chars.length];
		char[] dt = dateTime.toCharArray();
		char[] pc = pattern.toCharArray();
		for (int i = 0; i < pc.length; i++) {
			for (int j = 0; j < chars.length; j++) {
				if (pc[i] == chars[j]){
					if (i > dt.length-1)
						break;
					if (parts[j] == null)
						parts[j] = EMPTY_STR;
					parts[j] += dt[i];
				}
			}
		}
		if (parts[2] != null && (parts[2].length() == 2)){
			int year = Integer.parseInt(parts[2]);
			if (year >= 29)
				parts[2] = "19"+parts[2];
			else
				parts[2] = "20"+parts[2];
		}
		return parts;
	}
	
}
