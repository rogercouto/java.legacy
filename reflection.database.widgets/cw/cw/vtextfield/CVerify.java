package cw.vtextfield;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Calendar;

public class CVerify {

	/**
	 * Verifica se o texto é do tipo inteiro
	 * @param text
	 * @return
	 */
	protected static boolean integer(String text){
		char[] ca = text.toCharArray();
		for (char c : ca) {
			if (!Character.isDigit(c))
				return false;
		}
		return true;
	}
	
	protected static boolean haveComma(String text){
		char[] ca = text.toCharArray();
		for (char c : ca) {
			if (c == ',')
				return true;
		}
		return false;
	}
	
	protected static boolean decimal(String text){
		char[] ca = text.toCharArray();
		int countComma = 0;
		for (char c : ca) {
			if (c == ',')
				countComma++;
			if (!Character.isDigit(c) && c != ',')
				return false;
		}
		if (countComma > 1)
			return false;
		DecimalFormat df = new DecimalFormat("0.00");
		try {
			df.parse(text);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}
	
	protected static boolean date(String date){
		if (date.length() > 10)
			return false;
		char[] ca = date.toCharArray();
		for (int i = 0; i < ca.length; i++) {
			if (i == 2 || i == 5){
				if (ca[i] != '/')
					return false;
			}else{
				if (!Character.isDigit(ca[i]))
					return false;
			}
		}
		return true;
	}
	
	protected static boolean time(String date){
		if (date.length() > 5)
			return false;
		char[] ca = date.toCharArray();
		for (int i = 0; i < ca.length; i++) {
			if (i == 2){
				if (ca[i] != ':')
					return false;
			}else{
				if (!Character.isDigit(ca[i]))
					return false;
			}
		}
		return true;
	}
	
	protected static boolean validDate(String date){
		if (date.length() != 10)
			return false;
		char[] ca = date.toCharArray();
		for (int i = 0; i < ca.length; i++) {
			if (i == 2 || i == 5){
				if (ca[i] != '/')
					return false;
			}else{
				if (!Character.isDigit(ca[i]))
					return false;
			}
		}
		int dia = Integer.valueOf(date.substring(0, 2));
		int mes = Integer.valueOf(date.substring(3, 5));
		int ano = Integer.valueOf(date.substring(6, 10));
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, (mes-1));
		calendar.set(Calendar.YEAR, ano);
		int min = calendar.getActualMinimum(Calendar.YEAR);
		int max = calendar.getActualMaximum(Calendar.YEAR);
		if (ano < min || ano > max)
			return false;
		min = calendar.getActualMinimum(Calendar.MONTH)+1;
		max = calendar.getActualMaximum(Calendar.MONTH)+1;
		if (mes < min || mes > max)
			return false;
		min = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
		max = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		if (dia < min || dia > max)
			return false;
		return true;
	}
	
	protected static boolean validTime(String time){
		if (time.length() != 5)
			return false;
		char[] ca = time.toCharArray();
		for (int i = 0; i < ca.length; i++) {
			if (i == 2){
				if (ca[i] != ':')
					return false;
			}else{
				if (!Character.isDigit(ca[i]))
					return false;
			}
		}
		int hora = Integer.valueOf(time.substring(0, 2));
		int min = Integer.valueOf(time.substring(3, 5));
		if (hora < 0 || hora > 23)
			return false;
		if (min < 0 || min > 59)
			return false;
		return true;
	}
	
	public static void main(String[] args) {
		System.out.println(date("09/01/1984"));
	}
	
}
