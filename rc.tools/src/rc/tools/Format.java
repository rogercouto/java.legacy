package rc.tools;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Format {

	private static final DateFormat DF = new SimpleDateFormat("dd/MM/yyyy");
	private static final DateFormat DBF = new SimpleDateFormat("yyyy-MM-dd");
	private static final NumberFormat CF = new DecimalFormat("R$ #,##0.00");
	private static final NumberFormat NF = new DecimalFormat("#,##0.00");
	
	public static String dateToStr(Date date){
		return DF.format(date);
	}
	
	public static String dateToDB(Date date){
		return DBF.format(date);
	}
	
	public static String numberToCurrency(Number number){
		return CF.format(number);
	}
	
	public static String numberToDecimal(Number number){
		return NF.format(number);
	}
	
	public static String periodo(String dataIni, Integer meses){
		try {
			Date dataInicial = DBF.parse(dataIni);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dataInicial);
			cal.add(Calendar.MONTH, meses);
			StringBuilder builder = new StringBuilder();
			SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy");
			builder.append(sdf.format(dataInicial));
			builder.append(" até ");
			builder.append(sdf.format(cal.getTime()));
			return builder.toString();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getMes(int numero){
		int index = numero-1;
		String[] meses = {"Janeiro","Fevereiro","Março","Abril","Maio","Junho","Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"};
		return meses[index];
	}
	
	public static String dbDateToStr(String dbDateStr){
		StringBuilder builder = new StringBuilder();
		builder.append(dbDateStr.substring(8, 10));
		builder.append("/");
		builder.append(dbDateStr.substring(5, 7));
		builder.append("/");
		builder.append(dbDateStr.substring(0, 4));
		return builder.toString();
	}
	
	public static String dbDateToFullStr(String dbDateStr){
		StringBuilder builder = new StringBuilder();
		builder.append(dbDateStr.substring(8, 10));
		builder.append(" de ");
		int mes = Integer.valueOf(dbDateStr.substring(5, 7));
		builder.append(getMes(mes));
		builder.append(" de ");
		builder.append(dbDateStr.substring(0, 4));
		return builder.toString();
	}
	
	public static String dbDateTimeToStr(String dbDateStr){
		StringBuilder builder = new StringBuilder();
		builder.append(dbDateStr.substring(8, 10));
		builder.append("/");
		builder.append(dbDateStr.substring(5, 7));
		builder.append("/");
		builder.append(dbDateStr.substring(0, 4));
		builder.append(" ");
		builder.append(dbDateStr.substring(11, 13));
		builder.append(":");
		builder.append(dbDateStr.substring(14, 16));
		return builder.toString();
	}
	
	public static String dbDateTimeToFullStr(String dbDateStr){
		StringBuilder builder = new StringBuilder();
		builder.append(dbDateStr.substring(8, 10));
		builder.append(" de ");
		int mes = Integer.valueOf(dbDateStr.substring(5, 7));
		builder.append(getMes(mes));
		builder.append(" de ");
		builder.append(dbDateStr.substring(0, 4));
		builder.append(",  ");
		builder.append(dbDateStr.substring(11, 13));
		builder.append(":");
		builder.append(dbDateStr.substring(14, 16));
		return builder.toString();
	}
	
}
