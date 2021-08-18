package rde.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class Copy {

	/** 
	 * Copia arquivos de um local para o outro 
	 * @param origem - Arquivo de origem 
	 * @param destino - Arquivo de destino 
	 * @param overwrite - Confirmação para sobrescrever os arquivos 
	 * @throws IOException 
	 */  
	public static void file(File origem,File destino,boolean overwrite) throws IOException{  
		if (destino.exists() && !overwrite){  
			System.err.println(destino.getName()+" já existe, ignorando...");  
			return;  
		}  
		FileInputStream   fisOrigem = new FileInputStream(origem);  
		FileOutputStream fisDestino = new FileOutputStream(destino);  
		FileChannel fcOrigem = fisOrigem.getChannel();    
		FileChannel fcDestino = fisDestino.getChannel();    
		fcOrigem.transferTo(0, fcOrigem.size(), fcDestino);    
		fisOrigem.close();    
		fisDestino.close();  
	}  

	/** 
	 * Copia todos os arquivos de dentro de uma pasta para outra 
	 * @param origem - Diretório onde estão os arquivos a serem copiados 
	 * @param destino - Diretório onde os arquivos serão copiados 
	 * @param overwrite - Confirmação para sobrescrever os arquivos 
	 * @throws IOException 
	 */  
	public static void dir(File origem,File destino,boolean overwrite) throws IOException{  
		if (!destino.exists())  
			destino.mkdir();  
		if (!origem.isDirectory())  
			throw new UnsupportedOperationException("Origem deve ser um diretório");  
		if (!destino.isDirectory())  
			throw new UnsupportedOperationException("Destino deve ser um diretório");  
		File[] files = origem.listFiles();  
		for (File file : files) {  
			if (file.isDirectory())  
				dir(file,new File(destino+"\\"+file.getName()),overwrite);  
			else{  
				System.out.println("Copiando arquivo: "+file.getName());  
				file(file, new File(destino+"\\"+file.getName()),overwrite);  
			}  
		}  
	}  

}
