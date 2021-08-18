package cw.controls.imagemap;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import cw.tools.Util;

public class CImageMap {

	private Device device;
	private String filePackage;

	public CImageMap(){
		super();
	}
	public CImageMap(Device device,String filePackage) {
		super();
		this.device = device;
		this.filePackage = filePackage.replace('.', '/');
		if (this.filePackage.substring(
			this.filePackage.length()-1,this.filePackage.length()).compareTo("/")!= 0)
			this.filePackage += "/";
	}

	/**
	 *
	 * @param fileName 
	 * @return
	 */
	public Image get(String fileName){
		try {
			if (device != null && (filePackage != null) && (fileName != null)){
				return new Image(device,CImageMap.class.getResourceAsStream(filePackage+fileName));
			}
		} catch (IllegalArgumentException e) {
			String error = (filePackage+fileName).replace('/', '\\');
			System.err.println("Arquivo de imagem \""+error+"\" não existe! ");
		}
		return null;
	}
	
	public Image get(String fileName,Point size){
		return Util.resize(get(fileName), size);
	}
	
}
