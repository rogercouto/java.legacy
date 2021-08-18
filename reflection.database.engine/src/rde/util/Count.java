package rde.util;

import java.util.Date;

public class Count {

	private static long time;
	
	public static void start(){
		time = new Date().getTime();
	}
	public static void stop(){
		System.err.println((new Date().getTime() - time)+" miliseconds");
		time = 0;
	}
	public static void stop(String message){
		System.err.println(message+" "+(new Date().getTime() - time)+" miliseconds");
		time = 0;
	}
	
}
