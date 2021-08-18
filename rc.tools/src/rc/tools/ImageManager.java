package rc.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ImageManager {

	public static InputStream getImputStream(String resource){
		return ImageManager.class.getResourceAsStream(resource);
	}
	
	public static InputStream getImputStream(File file){
		if (file == null || !file.exists())
			return null;
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		}
		return null;
	}
	
}
