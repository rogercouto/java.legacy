package rde.persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rde.RDE;
import rde.annotations.NotNull;
import rde.annotations.PrimaryKey;
import rde.annotations.Unique;
import rde.connection.ConnectionManager;
import rde.database.Database;
import rde.exceptions.ValidationException;
import rde.util.Convert;
import rde.util.Logger;

public class Persistence {

	private ConnectionManager connectionManager;
	private Database database;

	public Persistence(ConnectionManager connectionManager) {
		super();
		this.connectionManager = connectionManager;
		database = this.connectionManager.getDatabase();
	}

	/**
	 * M�todo que insere um novo registro no banco de dados
	 * @param object - Objeto a ser inserido
	 * @return  boolean - confirma��o da opera��o
	 * @throws ValidationException - Exce��o caso o objeto n�o passe na valida��o
	 */
	@SuppressWarnings("unused")
	public boolean insert(Object object) throws ValidationException{
		boolean closeConn = false;
		if (!connectionManager.isConnected()){
			connectionManager.connect();
			closeConn = true;
		}
		boolean endSession = false;
		if (!connectionManager.inSession()){
			connectionManager.beginSession();
			endSession = true;
		}
		try {
			validate(object,RDE.OP_INSERT);
		} catch (ValidationException e) {
			if (endSession){
				connectionManager.rolbackSession();
				connectionManager.endSession();
			}
			if (closeConn){
				connectionManager.unconnect();
			}
			if (RDE.logErrors)
				Logger.log(e);
			throw e;
		}
		Field[] fields = object.getClass().getDeclaredFields();
		String sql = "INSERT INTO "+object.getClass().getSimpleName().toLowerCase()+"(";
		int count = 0;
		for (Field field : fields) {
			if (RDE.getKeyGen(field) != null)
				continue;
			if (count > 0)sql += ",";
			sql += field.getName();
			count++;
		}
		sql += ") VALUES ";
		for (int i = 0; i < count; i++) {
			sql += (i == 0)? " (" : ",";
			sql += "?";
		}
		sql += ")";
		try {
			PreparedStatement ps = connectionManager.prepareStatement(sql);
			count = 1;
			for (Field field : fields) {
				if (RDE.getKeyGen(field) != null)
					continue;
				field.setAccessible(true);
				Object value = field.get(object);
				if (value == null){
					ps.setObject(count, null);
					count++;
					continue;
				}
				if (field.getType().equals(Character.class)
				||(field.getType().equals(Character.TYPE))){
					Character c = (Character)value;
					ps.setString(count, String.valueOf(c.charValue()));
				}else if (field.getType().equals(Date.class)){
					if (database.getDatePattern() != null){
						String string = new SimpleDateFormat(database.getDatePattern()).format((Date)value);
						ps.setString(count, string);
					}else{
						Date date = (Date)value;
						java.sql.Timestamp sqlTime = new java.sql.Timestamp(date.getTime());
						//java.sql.Date sqlDate = new java.sql.Date(date.getTime());
						//ps.setDate(count, sqlDate);
						ps.setTimestamp(count, sqlTime);
					}
				}else if ((field.getType().equals(Boolean.TYPE) || field.getType().equals(Boolean.class))&&(database.getBooleanPattern() != null)){
					String[] bp = database.getBooleanPattern().split(";");
					ps.setString(count, ((Boolean)value ? bp[0] : bp[1]));
				}else if (field.getType().equals(File.class)){
					File file = (File)value;
					if (file == null)
						ps.setObject(count, null);
					else{
						switch (database.getFileType()) {
						case RDE.FILETYPE_BLOB:
							ps.setBlob(count, Convert.fileToInputStream(file));
							break;
						case RDE.FILETYPE_BYTE:
							ps.setBytes(count, Convert.fileToBytes(file));
							break;
						case RDE.FILETYPE_SYSTEM:
							ps.setString(count, saveFileOnDisk(file));
							break;
						default:
							ps.setObject(count, null);
						break;
						}
					}
				}else{
					ps.setObject(count, value);
				}
				count++;
			}
			ps.executeUpdate();
			ps.close();
			for (Field field : fields) {
				String keyGen = RDE.getKeyGen(field);
				if (keyGen == null)
					continue;
				sql = "SELECT "+keyGen;
				Statement stmt = connectionManager.createStatement();
				ResultSet result = stmt.executeQuery(sql);
				if (result.next()){
					field.setAccessible(true);
					Object key = result.getObject(1);
					if ((key.getClass() == Long.class || key.getClass() == Long.TYPE) &&
					(field.getType() == Integer.class || field.getType() == Integer.TYPE)){
						long l = (Long)key;
						field.set(object,Convert.longToInt(l));
					}else if (key.getClass() == BigInteger.class){
						BigInteger b = (BigInteger)key;
						field.set(object, b.intValue());
					}else
						field.set(object,result.getObject(1));
				}
				stmt.close();
				result.close();
			}
			if (endSession){
				connectionManager.commitSession();
				connectionManager.endSession();
			}
			if (closeConn){
				connectionManager.unconnect();
			}
			return true;
		} catch (SQLException e) {
			if (endSession){
				connectionManager.rolbackSession();
				connectionManager.endSession();
			}
			if (closeConn){
				connectionManager.unconnect();
			}
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		} catch (IOException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		}
		return false;
	}
	//TODO Update
	private boolean update(Object object,Field[] fields) throws ValidationException{
		List<File> filesToDelete = new ArrayList<File>();
		boolean closeConn = false;
		if (!connectionManager.isConnected()){
			connectionManager.connect();
			closeConn = true;
		}
		boolean endSession = false;
		if (!connectionManager.inSession()){
			connectionManager.beginSession();
			endSession = true;
		}
		try {
			validate(object,RDE.OP_UPDATE);
		} catch (ValidationException e) {
			if (endSession){
				connectionManager.rolbackSession();
				connectionManager.endSession();
			}
			if (closeConn){
				connectionManager.unconnect();
			}
			throw e;
		}
		Field[] keys = RDE.getPrimaryKeys(object.getClass());
		String sql = "UPDATE "+object.getClass().getSimpleName().toLowerCase()+" SET ";
		int count = 0;
		for (Field field : fields) {
			field.setAccessible(true);
			if (RDE.getKeyGen(field)!=null)
				continue;
			if (field.getType().equals(File.class)&&(!changeFile(object, field)))
				continue;
			if (count > 0)
				sql += ", ";
			sql += field.getName()+" = ?";
			count++;
		}
		sql += " WHERE ";
		for (Field key : keys) {
			if (key != keys[0])
				sql += ",";
			sql += key.getName()+" = ?";
		}
		try {
			PreparedStatement ps = connectionManager.prepareStatement(sql);
			count = 0;
			for (Field field : fields) {
				if (RDE.getKeyGen(field)!=null)
					continue;
				if ((field.getType().equals(File.class))&& (!changeFile(object, field)))
					continue;
				count++;
				Object value = field.get(object);
				if (value == null){
					ps.setObject(count, null);
					if (field.getType().equals(File.class)){
						File file = getOldFile(object, field);
						if (file != null){
							filesToDelete.add(file);
						}
					}
					continue;
				}
				if (field.getType().equals(Character.class)
				||(field.getType().equals(Character.TYPE))){
					Character c = (Character)value;
					ps.setString(count, String.valueOf(c.charValue()));
				}else if (field.getType().equals(Date.class)){
					if (database.getDatePattern() != null){
						String string = new SimpleDateFormat(database.getDatePattern()).format((Date)value);
						ps.setString(count, string);
					}else{
						Date date = (Date)value;
						java.sql.Timestamp sqlTime = new java.sql.Timestamp(date.getTime());
						//java.sql.Date sqlDate = new java.sql.Date(date.getTime());
						//ps.setDate(count, sqlDate);
						//ps.setTimestamp(count, sqlTime);
						ps.setString(count, sqlTime.toString());
					}
				}else if ((field.getType().equals(Boolean.TYPE) || field.getType().equals(Boolean.class))	&&(database.getBooleanPattern() != null)){
					String[] bp = database.getBooleanPattern().split(";");
					ps.setString(count, ((Boolean)value ? bp[0] : bp[1]));
				}else if (field.getType().equals(File.class)){
					File file = (File)value;
					switch (database.getFileType()) {
					case RDE.FILETYPE_BLOB:
						ps.setBlob(count, Convert.fileToInputStream(file));
						break;
					case RDE.FILETYPE_BYTE:
						ps.setBytes(count, Convert.fileToBytes(file));
						break;
					case RDE.FILETYPE_SYSTEM:
						deleteOldFile(object,field);
						if (file != null)
							ps.setString(count, saveFileOnDisk(file));
						break;
					default:
						ps.setObject(count, null);
					break;
					}
				}else{
					ps.setObject(count, value);
				}
			}
			count++;
			for (int i = 0; i < keys.length; i++) {
				keys[i].setAccessible(true);
				ps.setObject((i+count), keys[i].get(object));
			}
            ps.executeUpdate();
			ps.close();
			if (endSession){
				connectionManager.commitSession();
				connectionManager.endSession();
			}
			if (closeConn)
				connectionManager.unconnect();
			for (File file : filesToDelete) {
				if (file.exists())
					file.delete();
			}
			return true;
		} catch (SQLException e) {
			if (endSession){
				connectionManager.rolbackSession();
				connectionManager.endSession();
			}
			if (closeConn){
				connectionManager.unconnect();
			}
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		} catch (IOException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * M�todo que altera um registro do Banco de dados
	 * @param object - Objeto a ser alterado
	 * @return  boolean - confirma��o da opera��o
	 * @throws ValidationException
	 * @throws SecurityException
	 * @throws ValidationException - Exce��o caso o objeto n�o passe na valida��o
	 */
	public boolean update(Object object) throws SecurityException, ValidationException{
		return update(object,object.getClass().getDeclaredFields());
	}

	/**
	 * M�todo que altera um registro do Banco de dados
	 * @param object - Objeto a ser alterado
	 * @param indices - indices dos campos a serem alterados
	 * @return  boolean - confirma��o da opera��o
	 * @throws ValidationException - Exce��o caso o objeto n�o passe na valida��o
	 */
	public boolean update(Object object,int[] indices) throws ValidationException{
		return update(object,RDE.getFields(object.getClass(), indices));
	}

	/**
	 * M�todo que altera um registro do Banco de dados
	 * @param object - Objeto a ser alterado
	 * @param fieldNames - Nomes dos campos a serem alterados
	 * @return  boolean - confirma��o da opera��o
	 * @throws ValidationException - Exce��o caso o objeto n�o passe na valida��o
	 */
	public boolean update(Object object,String[] fieldNames) throws ValidationException{
		return update(object,RDE.getFields(object.getClass(), fieldNames));
	}

	//TODO Delete
	/**
	 * M�todo que exclui um registro do banco de dados
	 * @param object - objeto a ser excluido
	 * @return boolean - confirma��o da opera��o
	 * @throws ValidationException
	 */
	public boolean delete(Object object) throws ValidationException{
		boolean closeConn = false;
		if (!connectionManager.isConnected()){
			connectionManager.connect();
			closeConn = true;
		}
		boolean endSession = false;
		if (!connectionManager.inSession()){
			connectionManager.beginSession();
			endSession = true;
		}
		validate(object, RDE.OP_DELETE);
		Field[] keys = RDE.getPrimaryKeys(object.getClass());
		String sql = " DELETE FROM "+object.getClass().getSimpleName().toLowerCase();
		sql += " WHERE ";
		for (Field key : keys) {
			if (key != keys[0])
				sql += " AND ";
			sql += key.getName()+" = ?";
		}
		try {
			PreparedStatement ps = connectionManager.prepareStatement(sql);
			for (int i = 0; i < keys.length; i++) {
				keys[i].setAccessible(true);
				ps.setObject((i+1), keys[i].get(object));
			}
			deleteOldFiles(object);
			ps.executeUpdate();
			ps.close();
			if (endSession){
				connectionManager.commitSession();
				connectionManager.endSession();
			}
			if (closeConn){
				connectionManager.unconnect();
			}
			return true;
		} catch (SQLException e) {
			if (endSession){
				connectionManager.rolbackSession();
				connectionManager.endSession();
			}
			if (closeConn){
				connectionManager.unconnect();
			}
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		}
		return false;
	}

	//TODO File methods

	private String saveFileOnDisk(File file){
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmssSS");
		if (database.getFileDir().trim().length() == 0)
			database.setFileDir(System.getProperty("user.dir"));
		String prefix = database.getFileDir();
		if (database.getFileDir().substring(database.getFileDir().length()-1, database.getFileDir().length()).compareTo("\\") != 0)
			prefix += "\\";
		String fileName =  sdf.format(new Date())+"."+database.getFileExt();
		File dbFile = new File(prefix+fileName);
		try {
			if (!dbFile.exists())
				dbFile.createNewFile();
			FileInputStream origem = new FileInputStream(file);
			FileOutputStream destino = new FileOutputStream(dbFile);
			FileChannel fcOrigem = origem.getChannel();
			FileChannel fcDestino = destino.getChannel();
			fcOrigem.transferTo(0, fcOrigem.size(), fcDestino);
			origem.close();
			destino.close();
		} catch (FileNotFoundException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		} catch (IOException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		}
		return fileName;
	}

	private File getFileFromDisk(String fileName){
		String prefix = (database.getFileDir().trim().length() != 0) ? database.getFileDir() : System.getProperty("user.dir");
		if (prefix.substring(prefix.length()-1, prefix.length()).compareTo("\\") != 0)
			prefix += "\\";
		return new File(prefix+fileName);
	}

	private void deleteOldFiles(Object object) throws IllegalArgumentException, IllegalAccessException, SQLException{
		if (database.getFileType() != RDE.FILETYPE_SYSTEM)
			return;
		Field[] fields = object.getClass().getDeclaredFields();
		for (Field field : fields) {
			deleteOldFile(object, field);
		}
	}

	private void deleteOldFile(Object object,Field field) throws IllegalArgumentException, IllegalAccessException, SQLException{
		if (!field.getType().equals(File.class))
			return;
		Field[] keys = RDE.getPrimaryKeys(object.getClass());
		Object[] values = new Object[keys.length];
		for (int i = 0; i < keys.length; i++) {
			keys[i].setAccessible(true);
			values[i] = keys[i].get(object);
		}
		String sql = "SELECT "+field.getName()+" FROM ";
		sql += object.getClass().getSimpleName().toLowerCase()+" WHERE ";
		for (Field key : keys) {
			if (!key.equals(keys[0]))
				sql += " AND ";
			sql += key.getName()+" = "+key.get(object);
		}
		Statement stmt = connectionManager.createStatement();
		ResultSet result = stmt.executeQuery(sql);
		if (result.next()){
			String fileName = "";
			if (database.getFileDir() != null || database.getFileDir().trim().length() == 0)
				fileName = database.getFileDir();
			if (fileName.trim().length() > 0 && fileName.substring(fileName.trim().length() -1, fileName.trim().length()).compareTo("/") != 0)
				fileName += "/";
			fileName = result.getString(field.getName());
			if (fileName != null){
				File file = getFileFromDisk(fileName);
				if (file.exists())
					file.delete();
			}
		}
		stmt.close();
		result.close();
	}

	private File getOldFile(Object object, Field field) throws IllegalArgumentException, IllegalAccessException, SQLException{
		if (!field.getType().equals(File.class))
			return null;
		Field[] keys = RDE.getPrimaryKeys(object.getClass());
		Object[] values = new Object[keys.length];
		for (int i = 0; i < keys.length; i++) {
			keys[i].setAccessible(true);
			values[i] = keys[i].get(object);
		}
		String sql = "SELECT "+field.getName()+" FROM ";
		sql += object.getClass().getSimpleName().toLowerCase()+" WHERE ";
		for (Field key : keys) {
			if (!key.equals(keys[0]))
				sql += " AND ";
			sql += key.getName()+" = "+key.get(object);
		}
		Statement stmt = connectionManager.createStatement();
		ResultSet result = stmt.executeQuery(sql);
		File file = null;
		if (result.next()){
			String fileName = "";
			if (database.getFileDir() != null || database.getFileDir().trim().length() == 0)
				fileName = database.getFileDir();
			if (fileName.trim().length() > 0 && fileName.substring(fileName.trim().length() -1, fileName.trim().length()).compareTo("/") != 0)
				fileName += "/";
			fileName = result.getString(field.getName());
			if (fileName != null){
				file = getFileFromDisk(fileName);
			}
		}
		stmt.close();
		result.close();
		return file;
	}

	private boolean changeFile(Object object,Field field){
		if (!field.getType().equals(File.class))
			return false;
		if (database.getFileType() == RDE.FILETYPE_BLOB || database.getFileType() == RDE.FILETYPE_BYTE)
			return true;
		try {
			File dir = new File(database.getFileDir());
			File file = (File)field.get(object);
			if (file == null)
				return true;
			if (dir.getAbsolutePath().compareTo(file.getParentFile().getAbsolutePath())!=0)
				return true;
		} catch (IllegalArgumentException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		}
		return false;
	}

	//TODO Validation
	/**
	 * M�todo que valida um objeto antes de uma opera��o no banco de dados
	 * @param object
	 * @throws ValidationException
	 */
	public void validate(Object object,int type) throws ValidationException{
		try {
			Field[] fields = object.getClass().getDeclaredFields();
			Field[] keys = RDE.getPrimaryKeys(object.getClass());
			Object[] oldValues = new Object[keys.length];
			if (type == RDE.OP_UPDATE || type == RDE.OP_DELETE){
				String sql = "SELECT * FROM ";
				sql += object.getClass().getSimpleName().toLowerCase();
				sql += " WHERE ";
				for (Field key : keys) {
					if (key != keys[0])
						sql += " AND ";
					sql += key.getName()+" = ?";
				}
				PreparedStatement ps = connectionManager.prepareStatement(sql);
				for (int i = 0; i < keys.length; i++) {
					keys[i].setAccessible(true);
					oldValues[i] = keys[i].get(object);
					ps.setObject(i+1, oldValues[i]);
				}
				ResultSet res = ps.executeQuery();
				if (!res.next()){
					ps.close();
					res.close();
					throw new ValidationException("Opera��o Imposs�vel (Primary key(s) = NULL)!");
				}
				ps.close();
				res.close();
				if (type == RDE.OP_DELETE){
					return;
				}
			}
			String error = "";
			for (Field field : fields) {
				field.setAccessible(true);
				if (field.isAnnotationPresent(NotNull.class)){
					Object value = field.get(object);
					if (value == null){
						if (error.trim().length() > 0)
							error += ","+'\r';
						error += field.getAnnotation(NotNull.class).errorMessage();
					}
				}
				if (field.isAnnotationPresent(Unique.class)){
					boolean check = true;
					if (field.isAnnotationPresent(PrimaryKey.class)){
						Object value = field.get(object);
						for (int i = 0; i < keys.length; i++) {
							if (keys[i].getName().compareTo(field.getName())==0){
								if (value != null && value.equals(oldValues[i])){
									check = false;
									break;
								}
							}
						}

					}
					if (check){
						String sql = "SELECT * FROM ";
						sql += object.getClass().getSimpleName().toLowerCase();
						sql += " WHERE ";
						sql += field.getName();
						sql += " = ?";
						for (Field key : keys) {
							if (key.getName().compareTo(field.getName())==0)
								break;
							key.setAccessible(true);
							if (key.get(object)== null )
								break;
							sql += " AND ";
							sql += key.getName()+" <> ?";
						}
						PreparedStatement ps = connectionManager.prepareStatement(sql);
						int count = 1;
						ps.setObject(count, field.get(object));
						for (Field key : keys) {
							if (key.getName().compareTo(field.getName())==0)
								break;
							if (key.get(object)== null )
								break;
							count++;
							ps.setObject(count, key.get(object));
						}
						ResultSet result = ps.executeQuery();
						if (result.next()){
							if (error.trim().length() > 0)
								error += ","+'\r';
							error += field.getAnnotation(Unique.class).errorMessage();
						}
						ps.close();
						result.close();
					}
				}
				if (field.getType().equals(File.class)){
					long maxSize = database.getMaxFileSize();
					File file = (File)field.get(object);
					if (file != null && file.length() > maxSize){
						if (error.trim().length() > 0)
							error += ","+'\r';
					}
				}
			}
			if (error.trim().length() > 0)
				throw new ValidationException(error+"!");
		} catch (IllegalArgumentException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		} catch (SQLException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		}
	}

	//TODO Direct Persistence

	public void connect(){
		connectionManager.connect();
	}
	public boolean isConnected(){
		return connectionManager.isConnected();
	}
	public void unconnect(){
		connectionManager.unconnect();
	}
	public PreparedStatement prepareStatement(String sql){
		return connectionManager.prepareStatement(sql);
	}
	public Database getDatabase(){
		return database;
	}

	public void executeScript(InputStream is,boolean showSql) throws IOException, SQLException{
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String script = "";
		while (br.ready()){
			String tmp = br.readLine();
			if (tmp.length() >= 2 && (tmp.substring(0, 2).compareTo("--")==0))
				continue;
			script += tmp;
		}
		String[] sql = script.split("[;]");
		boolean unconnect = false;
		if (!connectionManager.isConnected()){
			connectionManager.connect();
			unconnect = true;
		}
		Statement stmt = connectionManager.createStatement();
		for (String string : sql) {
			if (showSql)
				System.out.println(string);
			stmt.executeUpdate(string);
		}
		if (unconnect)
			connectionManager.unconnect();
	}

	public void executeUpdate(String sql) throws SQLException{
		boolean unconnect = false;
		if (!connectionManager.isConnected()){
			connectionManager.connect();
			unconnect = true;
		}
		Statement stmt = connectionManager.createStatement();
		stmt.executeUpdate(sql);
		if (unconnect)
			connectionManager.unconnect();
	}
	public void executeUpdate(String sql,boolean showSql) throws SQLException{
		if (sql.trim().length() == 0)
			return;
		boolean unconnect = false;
		if (!connectionManager.isConnected()){
			connectionManager.connect();
			unconnect = true;
		}
		Statement stmt = connectionManager.createStatement();
		stmt.executeUpdate(sql);
		if (unconnect)
			connectionManager.unconnect();
	}

}
