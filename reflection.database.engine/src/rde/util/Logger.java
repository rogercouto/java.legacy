package rde.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

	public static File logFile = new File("log.txt");
	
	public static void log(Exception exception){
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(logFile,true));
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			writer.println(sdf.format(new Date()));
			writer.println(exception.getMessage());
			StackTraceElement[] ste = exception.getStackTrace();
			for (StackTraceElement element : ste) {
				writer.println(element.toString());
			}
			writer.println();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
