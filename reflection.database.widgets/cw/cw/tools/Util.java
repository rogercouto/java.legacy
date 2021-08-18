package cw.tools;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;


public class Util {

	public static final char SIGNAL = '-';
	public static final char COMMA = ',';
	public static final char POINT = '.';
	/**
	 * Conta o mumero de vezes que um caractere aparece em um vetor
	 * @param c
	 * @param seq
	 * @return resultdo da contagem
	 */
	public static int charCount(char c,char[] seq){
		int k = 0;
		for (char sc : seq) {
			if (sc == c)
				k++;
		}
		return k;
	}
	/**
	 * Conta o mumero de vezes que um caractere aparece em uma string
	 * @param c
	 * @param string
	 * @return resultdo da contagem
	 */
	public static int charCount(char c,String string){
		return charCount(c, string.toCharArray());
	}
	
	/**
	 * Retorna a primeira posição que o caractere aparece em um vetor
	 * @param c
	 * @param seq
	 * @return posição do caractere ou -1 quando não aparece
	 */
	public static int charPos(char c,char[] seq){
		for (int i = 0; i < seq.length; i++) {
			if (c == seq[i])
				return i;
		}
		return -1;
	}
	
	/**
	 * Retorna a primeira posição que o caractere aparece em uma string
	 * @param c
	 * @param seq
	 * @return posição do caractere ou -1 quando não aparece
	 */
	public static int charPos(char c,String string){
		return charPos(c, string.toCharArray());
	}
	
	/**
	 * Método que checa se uma caractere é uma vírgula
	 * @param c
	 * @return resultado da checkagem
	 */
	public static boolean isComma(char c){
		if (c == COMMA)
			return true;
		return false;
	}
	/**
	 * Método que checa se uma caractere é um ponto
	 * @param c
	 * @return resultado da checkagem
	 */
	public static boolean isPoint(char c){
		if (c == POINT)
			return true;
		return false;
	}
	
	
	/**
	 * Método que verifica se uma string pode ser convertido em uma forma decimal
	 * @param string
	 * @return true caso seja decimal else caso contrário
	 */
	public static boolean isDecimal(String string){
		int sk = 0, ck = 0;
		char[] seq = string.toCharArray();
		for (int i = 0; i < seq.length; i++) {
			if (seq[i] == SIGNAL){
				if (i > 0)
					return false;
				if (sk > 0)
					return false;
				sk++;
				continue;
			}else if (seq[i] == COMMA){
				if (ck > 0)
					return false;
				ck++;
				continue;
			}
			if (!Character.isDigit(seq[i]))
				return false;
		}
		return true;
	}

	/**
	 * Conta o número de casas decimais de uma string
	 * @param string
	 * @return número de casas decimais
	 */
	public static int countDigits(String string){ 
		char[] seq = string.toCharArray();
		int k = 0;
		boolean start = false;
		for (char c : seq) {
			if (start)k++;
			if ((c == COMMA) || (c == POINT)){
				start = true;
			}
		}
		return k;
	}
	
	/**
	 * Arredonda um valor cfme um número de casas decimais
	 * @param val
	 * @param digits
	 * @return
	 */
	public static double round(double val, String pattern) {
		int digits = countDigits(pattern);
		return round(val,digits);
	}
	
	 /**
     * Round a double value to a specified number of decimal
     * places.
     * @param val the value to be rounded.
     * @param digits the number of decimal places to round to.
     * @return val rounded to places decimal places.
     */
	public static double round(double val, int digits) {
        long factor = (long)Math.pow(10,digits);
        //Shift the decimal the correct number of places to the right
        val = val * factor;
        //Round to the nearest integer.
        long tmp = Math.round(val);
        //Shift the decimal the correct number of places back to the left.
        return (double)tmp / factor;
    }
	
	public static Date getDate(DateTime dateTime){
		int d = dateTime.getDay();
		int M = dateTime.getMonth() + 1;
		int y = dateTime.getYear();
		int H = dateTime.getHours();
		int m = dateTime.getMinutes();
		int s = dateTime.getSeconds();
		String date = d +"/"+ M + "/" +y+" "+H+":"+m+":"+s;
		String pattern = "d/M/y H:m:s";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String getStrDate(DateTime dateTime,String pattern){
		int d = dateTime.getDay();
		int M = dateTime.getMonth() + 1;
		int y = dateTime.getYear();
		int H = dateTime.getHours();
		int m = dateTime.getMinutes();
		int s = dateTime.getSeconds();
		String strDate = d +"/"+ M + "/" +y+" "+H+":"+m+":"+s;
		String p = "d/M/y H:m:s";
		SimpleDateFormat sdf = new SimpleDateFormat(p);
		try {
			Date date = sdf.parse(strDate);
			sdf.applyPattern(pattern);
			return sdf.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void setDate(DateTime dateTime,Date date){
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("dd");
		int day = new Integer(sdf.format(date)).intValue();
		sdf.applyPattern("MM");
		int month = new Integer(sdf.format(date)).intValue() - 1;
		sdf.applyPattern("yyyy");
		int year = new Integer(sdf.format(date)).intValue();
		dateTime.setDay(day);
		dateTime.setMonth(month);
		dateTime.setYear(year);
	}
	
	public static void setTime(DateTime dateTime,Date date){
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("HH");
		int hour = new Integer(sdf.format(date)).intValue();
		sdf.applyPattern("mm");
		int min = new Integer(sdf.format(date)).intValue();
		dateTime.setHours(hour);
		dateTime.setMinutes(min);
	}
	
	public static int getNumDias(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("MM");
		int month = new Integer(sdf.format(date)).intValue() - 1;
		sdf.applyPattern("yyyy");
		int year = new Integer(sdf.format(date)).intValue();
		Calendar cal = (Calendar) Calendar.getInstance(). clone();
		cal.set(Calendar.YEAR,year);
		cal.set(Calendar.MONTH,month);
		return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
	
	public static int getNumDias(int mes, int ano){
		Calendar cal = (Calendar) Calendar.getInstance(). clone();
		cal.set(Calendar.YEAR,ano);
		cal.set(Calendar.MONTH,mes);
		return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
	
	public static void printArray(Object[] array){
		if (array == null){
			System.out.println("Null array");
			return;
		}
		String out = "{";
		for (int i = 0; i < array.length; i++) {
			if (i > 0)
				out += ", ";
			out += array[i].toString();
		}
		out += "}";
		System.out.println("Array lenght="+array.length+" values="+out);
	}
	
	public static Image resize(Image image,Point size){
		if (image == null || (size == null))
			return null;
		Device device = image.getDevice();
		ImageData data = image.getImageData();
		return new Image(device,data.scaledTo(size.x, size.y));
	}
	
	public static Point getSize(Image image){
		ImageData data = image.getImageData();
		return new Point(data.width,data.height);
	}
	/**	
	 * Redimensiona uma imagem sem deformar,
	 * de acordo com o tamanho de um container
	 * @param image
	 * @param size
	 * @return Image
	 */
	public static Image resizeProp(Image image,Point size){
		if (image == null || (size == null))
			return null;
		Device device = image.getDevice();
		ImageData imageData = image.getImageData();
		double imageWidth = imageData.width;
		double imageHeight = imageData.height;
		double containerWidth = size.x;
		double containerHeight = size.y;
		double prop = 0;
		if (containerWidth >= containerHeight){
			if (imageWidth / (imageHeight / containerHeight) > containerWidth)
				prop = imageWidth / containerWidth;
			else
				prop = imageHeight / containerHeight;
		}else{
			if (imageHeight / (imageWidth / containerWidth) > containerHeight)
				prop = imageHeight / containerHeight;
			else
				prop = imageWidth / containerWidth;
		}
		Point newSize = new Point(0,0);
		newSize.x = new Double(imageWidth / prop).intValue();
		newSize.y = new Double(imageHeight / prop).intValue();
		return new Image(device,imageData.scaledTo(newSize.x, newSize.y));
	}
	
	/**
	 * Método que imprime o nome dos campos e o conteï¿½do deles cfme o ojeto passado
	 * @param object
	 */
	public static void printFields(Object object){
		if (object == null){
			System.out.println(object);
			return;
		}
		Field[] fields = object.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			try {
				fields[i].setAccessible(true);
				System.out.println(fields[i].getName()+" = "+fields[i].get(object));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		System.out.println();
	}
	
	public static boolean compare(Object obj1,Object obj2){
		if (!obj1.getClass().equals(obj2.getClass()))
			return false;
		Field[] fields = obj1.getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			try {
				System.out.println(field.get(obj1) != field.get(obj2));
				if (field.get(obj1) != field.get(obj2))
					return false;
			} catch (IllegalArgumentException e) {
				return false;
			} catch (IllegalAccessException e) {
				return false;
			}
		}
		return true;	
	}
	/**
	 * Retorna uma string numérica
	 * @param string
	 * @return
	 */
	public static String numberString(String string){
		String numString = "";
		for (char c : string.toCharArray()) {
			if (Character.isDigit(c))
				numString += c;
		}
		return numString;
	}
	
	public static boolean isIn(String string,String[] array){
		for (String listItem : array){
			if (string.compareTo(listItem)==0)
				return true;
		}
		return false;
	}
	
	public static boolean isIn(String string,Iterable<String> list){
		for (String listItem : list) {
			if (string.compareTo(listItem)==0)
				return true;
		}
		return false;
	}
	
	/**
	 * Adiciona os eventos para verificação se a tela for fechada
	 * @param shell
	 */
	public static void setCloseConfirmation(final Shell shell,final Flag flag,final String message){
		setFlag(flag, shell);
		shell.addShellListener(new ShellAdapter(){
			@Override
			public void shellClosed(ShellEvent arg0) {
				if (flag.getArg()&&(!Dialog.question(shell, message))){
					arg0.doit = false;
				}
			}
		});
	}
	
	
	private static void setFlag(final Flag flag,Control control){
		if (control instanceof Composite) {
			Composite comp = (Composite)control;
			Control[] children = comp.getChildren();
			if (children.length > 0){
				for (Control child : children) 
					setFlag(flag, child);
			}else{
				Listener listener = new Listener(){
					@Override
					public void handleEvent(Event arg0) {
						flag.setArg(true);
					}
				};
				if (control instanceof Button)
					control.addListener(SWT.Selection, listener);
				else if (control instanceof DateTime)
					control.addListener(SWT.Selection, listener);
				else	
					control.addListener(SWT.Modify, listener);
			}
		}else{
			Listener listener = new Listener(){
				@Override
				public void handleEvent(Event arg0) {
					flag.setArg(true);
				}
			};
			if (control instanceof Button)
				control.addListener(SWT.Selection, listener);
			else if (control instanceof DateTime)
				control.addListener(SWT.Selection, listener);
			else	
				control.addListener(SWT.Modify, listener);
		}
	}
	
	
	public static void setFontSize(int size,Control control){
		Font font = control.getFont();
		FontData[] fd = font.getFontData();
		fd[0].setHeight(size);
		control.setFont(new Font(Display.getDefault(),fd));
	}
	
	public static void boldFont(Control control){
		Font font = control.getFont();
		FontData[] fd = font.getFontData();
		fd[0].setStyle(SWT.BOLD);
		control.setFont(new Font(Display.getDefault(),fd));
	}
	
	public static void boldFont(TreeItem item){
		Font font = item.getFont();
		FontData[] fd = font.getFontData();
		fd[0].setStyle(SWT.BOLD);
		item.setFont(new Font(Display.getDefault(),fd));
	}
	
	public static void unBoldFont(Control control){
		Font font = control.getFont();
		FontData[] fd = font.getFontData();
		fd[0].setStyle(SWT.NONE);
		control.setFont(new Font(Display.getDefault(),fd));
	}
	
	public static void setFontStyle(Control control, int fontStyle){
		Font font = control.getFont();
		FontData[] fd = font.getFontData();
		fd[0].setStyle(fontStyle);
		control.setFont(new Font(Display.getDefault(),fd));
	}
	
	public static void setBackground(Composite composite,Color color){
		composite.setBackground(color);
		Control[] controls = composite.getChildren();
		for (Control control : controls) {
			if (control instanceof Composite) {
				Composite childComp = (Composite)control;
				setBackground(childComp, color);
			}else{
				control.setBackground(color);
			}
		}
	}
	
	public static Date getDate(int dia, int mes, int ano){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String strDate = (dia < 10) ? "0"+dia : ""+dia;
		strDate += "/";
		strDate += (mes < 10) ? "0"+mes : ""+mes;
		strDate += "/";
		strDate += ano;
		try {
			Date date = sdf.parse(strDate);
			return date;
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static int getDay(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		String strDay = sdf.format(date);
		return Integer.parseInt(strDay);
	}
	public static int getMonth(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("MM");
		String strMonth = sdf.format(date);
		return Integer.parseInt(strMonth);
	}
	public static int getYear(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		String strYear = sdf.format(date);
		return Integer.parseInt(strYear);
	}
	
	
	public static void boldFont(TableItem item) {
		Font font = item.getFont();
		FontData[] fd = font.getFontData();
		fd[0].setStyle(SWT.BOLD);
		item.setFont(new Font(Display.getDefault(),fd));
	}
	
	public static Date decDay(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int dia = calendar.get(Calendar.DAY_OF_MONTH);
		int mes = calendar.get(Calendar.MONTH)+1;
		int ano = calendar.get(Calendar.YEAR);
		if (dia == 1){
			if (mes == 1){
				dia = 31;
				mes = 12;
				ano--;
			}else{
				mes--;
				dia = getNumDias(mes-1, ano);
			}
		}else{
			dia--;
		}
		calendar.set(Calendar.DAY_OF_MONTH, dia);
		calendar.set(Calendar.MONTH, mes-1);
		calendar.set(Calendar.YEAR, ano);
		return calendar.getTime();
	}
	
	public static Date incDay(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int dia = calendar.get(Calendar.DAY_OF_MONTH);
		int mes = calendar.get(Calendar.MONTH)+1;
		int ano = calendar.get(Calendar.YEAR);
		int max = getNumDias(mes-1, ano);
		if (dia == max){
			if (mes == 12){
				dia = 1;
				mes = 1;
				ano++;
			}else{
				mes++;
				dia = 1;
			}
		}else{
			dia++;
		}
		calendar.set(Calendar.DAY_OF_MONTH, dia);
		calendar.set(Calendar.MONTH, mes-1);
		calendar.set(Calendar.YEAR, ano);
		return calendar.getTime();
	}
	
	public static String digitsOnly(String string){
		char[] ca = string.toCharArray();
		StringBuilder builder = new StringBuilder();
		for (char c : ca) {
			if (Character.isDigit(c))
				builder.append(c);
		}
		
		return builder.toString();
	}
	
}
