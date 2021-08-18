package rde.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import rde.util.Logger;


/**
 * Classe que contém as propriedades do banco de dados
 * @author Roger
 */
public class Database {

	protected String driver = null;
	protected String url = null;
	protected String user = null;
	protected String password = null;
	protected boolean transactional = true;
	protected String booleanPattern = null;
	protected String datePattern = null;
	protected int fileType;
	protected String fileDir = "";
	protected String fileExt = "RDF";
	protected long maxFileSize = Long.MAX_VALUE;
	protected String stringType = "VARCHAR(80)";
	protected String concatFunction = null;
	
	public Database() {
		super();
	}
	public Database(String className, String ulr) {
		super();
		this.driver = className;
		this.url = ulr;
	}
	public Database(String className, String ulr, String user, String password) {
		super();
		this.driver = className;
		this.url = ulr;
		this.user = user;
		this.password = password;
	}
	public Database(File file){
		if (!file.exists())
			throw new RuntimeException("Arquivo de propriedades não existe!");
		Properties properties = new Properties();
		FileInputStream propertiesFile = null;
		try {
			propertiesFile = new FileInputStream(file);
			properties.load(propertiesFile);
			driver = properties.getProperty("jdbc.driver");
			url = properties.getProperty("jdbc.url");
            user = properties.getProperty("jdbc.username");
            password = properties.getProperty("jdbc.password");
		} catch (FileNotFoundException e) {
			Logger.log(e);
			e.printStackTrace();
		} catch (IOException e) {
			Logger.log(e);
			e.printStackTrace();
		}
	}
	/**
	 * Retorna o nome do Driver JDBC
	 * @return Driver JDBC Class Name
	 */
	public String getClassName() {
		return driver;
	}
	/**
	 * Seta o nome do Driver JDBC
	 * @param className - Driver JDBC Class Name
	 */
	public void setClassName(String className) {
		this.driver = className;
	}
	/**
	 * retorna a ulr do DB
	 * @return String caminho do DB
	 */
	public String getUlr() {
		return url;
	}
	/**
	 * Seta a ULR do banco de dados
	 * @param ulr - Caminho do banco de dados 
	 */
	public void setUlr(String ulr) {
		this.url = ulr;
	}
	/**
	 * Retorna o nome do usuário do SGBD
	 * @return String - User name
	 */
	public String getUser() {
		return user;
	}
	/**
	 * Seta o nome do usuário do SGBD
	 * @param user - User name
	 */
	public void setUser(String user) {
		this.user = user;
	}
	/**
	 * Retorna a senha do usuario do SGBD
	 * @return String - senha
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * Seta a senha do usuario do SGBD
	 * @param password - senha do usuario
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * Retorna o suporte do SGDB a transactions
	 * @return boolean  - suporte a transactions
	 */
	public boolean isTransactional() {
		return transactional;
	}
	/**
	 * Indica se o SGBD suporta Transactions
	 * @param transactional
	 */
	public void setTransactional(boolean transactional) {
		this.transactional = transactional;
	}
	/**
	 * Retorna o padrão para tipos boolean armazenados como texto em agluns SGBD
	 * @return padrão de boolean como texto ex:(true;false)
	 */
	public String getBooleanPattern() {
		return booleanPattern;
	}
	/**
	 * Seta o padrão para tipos boolean armazenados como texto em agluns SGBD
	 * @param booleanPattern padrão de boolean como texto ex: (true;false)
	 */
	public void setBooleanPattern(String booleanPattern) {
		this.booleanPattern = booleanPattern;
	}
	/**
	 * Retorna o padrão para tipos Date armazenados como texto em agluns SGBD
	 * @return padrão de Date como texto ex:(yyyy-MM-dd)
	 */
	public String getDatePattern() {
		return datePattern;
	}
	/**
	 * Seta o padrão para tipos Date armazenados como texto em agluns SGBD
	 */
	public void setDatePattern(String datePattern) {
		this.datePattern = datePattern;
	}
	/**
	 * Retorna o Modo que o banco de dados armazena arquivos(File)
	 * @return indice do tipo - FILETYPE_BLOB,FILETYPE_BYTE,FILETYPE_SYSTEM,FILETYPE_NONE
	 */
	public int getFileType() {
		return fileType;
	}
	/**
	 * Indica o Modo que o banco de dados armazena arquivos(File)
	 * @param fileType - FILETYPE_BLOB,FILETYPE_BYTE,FILETYPE_SYSTEM,FILETYPE_NONE
	 */
	public void setFileType(int fileType) {
		this.fileType = fileType;
	}
	/**
	 * Retorna o caminho que o banco de dados salva os arquivos no computador (somente tipo FILETYPE_SYSTEM)
	 * @return caminho dos arquivos
	 */
	public String getFileDir() {
		return fileDir;
	}
	/**
	 * Indica o caminho que o banco de dados salva os arquivos no computador (somente tipo FILETYPE_SYSTEM)
	 * @param fileDir - Caminho dos arquivos
	 */
	public void setFileDir(String fileDir) {
		this.fileDir = fileDir;
	}
	/**
	 * Retorna o tamanho máximo para os arquivos(File) salvos no DB
	 * @return long - tamanho máximo
	 */
	public long getMaxFileSize() {
		return maxFileSize;
	}
	/**
	 * Indica o tamanho máximo para os arquivos(File) salvos no DB
	 * @param maxFileSize - tamanho máximo
	 */
	public void setMaxFileSize(long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}
	/**
	 * Retorna o tipo de arquivos do SGDB compativel com o String do Java
	 * @return String com o tipo (ex:VARCHAR)
	 */
	public String getStringType() {
		return stringType;
	}
	/**
	 * Indica o tipo de arquivos do SGDB compativel com o String do Java
	 * @param stringType - String do tipo usado no DB
	 */
	public void setStringType(String stringType) {
		this.stringType = stringType;
	}
	
	public String getConcatFunction() {
		return concatFunction;
	}
	public void setConcatFunction(String concatFunction) {
		this.concatFunction = concatFunction;
	}
	
	public String getFileExt() {
		return fileExt;
	}
	public void setFileExt(String fileExt) {
		this.fileExt = fileExt;
	}
	
	
}
