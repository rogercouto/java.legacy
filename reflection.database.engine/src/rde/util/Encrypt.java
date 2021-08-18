package rde.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import rde.RDE;

public class Encrypt {
	
	public static String md5(String password){
		try  {  
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(password.getBytes());
			BigInteger hash = new BigInteger(1, md.digest());
			return hash.toString(16);  
		}catch(NoSuchAlgorithmException e)  {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();  
		}  
		return null;
	}

}
