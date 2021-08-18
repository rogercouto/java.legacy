package rde.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import rde.RDE;

public class Convert {

    /**
     * Converte InputStream em um arquivo File
     * @param is - InputStream a ser convertido
     * @return File - Arquvivo gerado
     */
    public static File inputStreamToFile(InputStream is,String fileName){
        File file = new File(fileName);
        OutputStream out;
        try {
            out = new FileOutputStream(file);
            byte buf[]=new byte[1024];
            int len;
            while((len=is.read(buf))>0)
                out.write(buf,0,len);
            out.close();
            is.close();
        } catch (FileNotFoundException e) {
            if (RDE.logErrors)
                Logger.log(e);
            e.printStackTrace();
        } catch (IOException e) {
            if (RDE.logErrors)
                Logger.log(e);
            e.printStackTrace();
        }
        return file;
    }

    /**
     * Converte InputStream em um arquivo File tempor�rio
     * @param is - InputStream a ser convertido
     * @return File - Arquivo tempor�rio
     */
    public static File inputStreamToTmpFile(InputStream is){
        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyyHHmmssSS");
        File file = new File(df.format(new Date())+".tmp");
        OutputStream out;
        try {
            out = new FileOutputStream(file);
            byte buf[]=new byte[1024];
            int len;
            while((len=is.read(buf))>0)
                out.write(buf,0,len);
            out.close();
            is.close();
            file.deleteOnExit();//Deleta o arquivo ao sair
        } catch (FileNotFoundException e) {
            if (RDE.logErrors)
                Logger.log(e);
            e.printStackTrace();
        } catch (IOException e) {
            if (RDE.logErrors)
                Logger.log(e);
            e.printStackTrace();
        }
        return file;
    }

    /**
     * Converte um Arquivo File para o tipo InputStream
     * @param file - Arquivo a ser convertido
     * @return InputStream
     */
    public static InputStream fileToInputStream(File file){
        if ((file == null) || (!file.exists()))
            return null;
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            if (RDE.logErrors)
                Logger.log(e);
            e.printStackTrace();
        }
        return null;
    }
    /**
     * Converte um arquivo File em um byte[]
     * @param file
     * @return byte[]
     * @throws IOException
     */
    public static byte[] fileToBytes(File file) throws IOException{
        InputStream is = new FileInputStream(file);
        // Get the size of the file
        long length = file.length();
        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            extracted(file);
        }
        // Close the input stream and return bytes
        is.close();
        return bytes;
    }

    private static void extracted(File file) throws IOException {
        throw new IOException("Could not completely read file "+file.getName());
    }

    /**
     * Converte um byte[] em um arquivo tempor�rio
     * @param bytes - byte[] a ser convertido
     * @return File - Arquivo tempor�rio
     */
    public static File bytesToFile(byte[] bytes,String fileName){
        File file = new File(fileName);
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.flush();
            fos.close();
            file.deleteOnExit();
            return file;
        } catch (FileNotFoundException e) {
            if (RDE.logErrors)
                Logger.log(e);
            e.printStackTrace();
        } catch (IOException e) {
            if (RDE.logErrors)
                Logger.log(e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Converte um byte[] em um arquivo tempor�rio
     * @param bytes - byte[] a ser convertido
     * @return File - Arquivo tempor�rio
     */
    public static File bytesToTmpFile(byte[] bytes){
        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyyHHmmssSS");
        File file = new File(df.format(new Date())+".tmp");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.flush();
            fos.close();
            file.deleteOnExit();
            return file;
        } catch (FileNotFoundException e) {
            if (RDE.logErrors)
                Logger.log(e);
            e.printStackTrace();
        } catch (IOException e) {
            if (RDE.logErrors)
                Logger.log(e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Converte uma string em data se possivel
     * @param date - String a ser convertida
     * @param pattern - padr�o de data p/ convers�o
     * @return Date - Resultado da convers�o
     */
    public static Date stringToDate(String date,String pattern){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            if (RDE.logErrors)
                Logger.log(e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Converte uma string no formato de DB em data se possivel
     * @param date - String a ser convertida
     * @return Date - Resultado da convers�o
     */
    public static Date stringToDate(String date){
        String pattern = null;
        switch (date.length()) {
        case 10:
            pattern = "yyyy-MM-dd";
            break;
        case 8:
            pattern = "HH:mm:ss";
            break;
        default:
            pattern = "yyyy-MM-dd HH:mm:ss";
            break;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            if (RDE.logErrors)
                Logger.log(e);
            e.printStackTrace();
        }
        return null;
    }
    /**
     * Converte um date em string conforme o tipo
     * @param date - Data a ser convertida
     * @param pattern - Padr�o de data a ser usada
     * @return String - Resultado da convers�o
     */
    public static String dateToString(Date date,String pattern){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    public static int longToInt(long l){
        return new Long(l).intValue();
    }

    public static short intToShort(int i){
        return new Integer(i).shortValue();
    }

    public static float doubletoShort(double d){
        return new Double(d).floatValue();
    }

    @Deprecated
    public static Date fromDatabase(Timestamp timestamp){
        if (timestamp != null){
            long l = timestamp.getTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(l);
            //Corrige as 2 horas a menos
            int plus = 24 - calendar.get(Calendar.HOUR_OF_DAY);
            if (plus > 0)
            	calendar.add(Calendar.HOUR, plus);
            System.out.println(timestamp+" ??? "+calendar.getTime());
            return calendar.getTime();
        }
        return null;
    }

    public static Date fromDatabase(String string){
    	if (string != null){
    		String pattern = "yyyy-MM-dd";
    		if (string.length() == 19)
    			pattern = "yyyy-MM-dd HH:mm:ss";
    		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    		try {
				return sdf.parse(string);
			} catch (ParseException e) {
				throw new RuntimeException("Erro no formato de datas");
			}
    	}
    	return null;
    }

    public static void main(String[] args) {

		String t = "2019-12-08 22:08:16";
		System.out.println(t.length());
		t = "2019-08-05";
		System.out.println(t.length());
	}

}
