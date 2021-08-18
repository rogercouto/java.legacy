package rde.datasource;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import rde.RDE;
import rde.connection.ConnectionManager;
import rde.database.Database;
import rde.exceptions.RDEError;
import rde.util.Convert;
import rde.util.Logger;

/**
 * Classe respons�vel pelas consultas ao Banco de dados
 * @author Roger
 */
public class DataSource {

	private ConnectionManager connectionManager;
	private Database database;

	/**
	 * Construtor do DataSource
	 * @param connectionManager - Responsavel pela comunica��o com o DB
	 */
	public DataSource(ConnectionManager connectionManager) {
		super();
		this.connectionManager = connectionManager;
		database = this.connectionManager.getDatabase();
	}

	/**
	 * M�todo que busca um registro do banco de dados
	 * @param c - classe do objeto
	 * @param keyValues - valores das chaves prim�rias (m�ltiplas chaves)
	 * @return objeto resultante
	 */
	public Object getObject(Class<?> c,Object[] keyValues){
		Field[] fields = c.getDeclaredFields();
		Field[] keys = RDE.getPrimaryKeys(c);
		if (keys.length != keyValues.length)
			return null;
		String sql = "SELECT * FROM "+c.getSimpleName().toLowerCase()+" WHERE ";
		for (int i = 0; i < keys.length; i++) {
			if (i > 0) sql += " AND ";
			sql += keys[i].getName() +" = ?";
		}
		try {
			boolean unconnect = false;
			if (!connectionManager.isConnected()){
				connectionManager.connect();
				unconnect = true;
			}
			PreparedStatement ps = connectionManager.prepareStatement(sql);
			for (int i = 0; i < keyValues.length; i++) {
				ps.setObject(i+1, keyValues[i]);
			}
            ResultSet res = ps.executeQuery();
			Object object = c.asSubclass(c).newInstance();
			if (res.next()){
				for (Field field : fields) {
					field.setAccessible(true);
                    if (res.getObject(field.getName()) == null)
                        continue;
					if ((field.getType().equals(Integer.TYPE)) || (field.getType().equals(Integer.class))){
						field.set(object, res.getInt(field.getName()));
					}else if ((field.getType().equals(Long.TYPE)) || (field.getType().equals(Long.class))){
						field.set(object, res.getLong(field.getName()));
					}else if ((field.getType().equals(Float.TYPE)) || (field.getType().equals(Float.class))){
						field.set(object, res.getFloat(field.getName()));
					}else if ((field.getType().equals(Double.TYPE)) || (field.getType().equals(Double.class))){
						field.set(object, res.getDouble(field.getName()));
					}else if (field.getType().equals(Date.class)){
						if (database.getDatePattern() != null){
							String dateTime = res.getString(field.getName());
							if (dateTime.trim().length() == 8)
								field.set(object,new SimpleDateFormat("HH:mm:ss").parse(dateTime));
							else
								field.set(object,new SimpleDateFormat(database.getDatePattern()).parse(dateTime));
						}else{
							//java.sql.Date sqlDate = res.getDate(field.getName());
							/*
							java.sql.Timestamp sqlTime = res.getTimestamp(field.getName());
							long l = sqlTime.getTime();
							field.set(object,new Date(l));

							Date date = res.getDate(field.getName());
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(date);
							calendar.add(Calendar.DAY_OF_MONTH, 1);
							*/
							field.set(object, Convert.fromDatabase(res.getString(field.getName())));
						}
					}else if (field.getType().equals(Character.class)
							||(field.getType().equals(Character.TYPE))){
						field.set(object, res.getString(field.getName()).toCharArray()[0]);
					}else if (field.getType().equals(Boolean.class)||(field.getType().equals(Boolean.TYPE))){
						if (database.getBooleanPattern() == null){
							boolean b = res.getBoolean(field.getName());
							field.set(object, b);
						}else{
							String[] tf = database.getBooleanPattern().split("[;]");
							if (tf.length == 2){
								if (res.getString(field.getName()).compareTo(tf[0])==0)
									field.set(object, true);
								else if (res.getString(field.getName()).compareTo(tf[1])==0)
									field.set(object, false);
							}
						}
					}else if (field.getType().equals(File.class)){
						switch (database.getFileType()) {
						case RDE.FILETYPE_BLOB:
							Blob blob = res.getBlob(field.getName());
							InputStream inputStream = blob.getBinaryStream();
							field.set(object,Convert.inputStreamToTmpFile(inputStream));
							break;
						case RDE.FILETYPE_BYTE:
							field.set(object,Convert.bytesToTmpFile(res.getBytes(field.getName())));
							break;
						case RDE.FILETYPE_SYSTEM:
							field.set(object, getFileFromDisk(res.getString(field.getName())));
							break;
						default:
							break;
						}
					}else{
						field.set(object, res.getObject(field.getName()));
					}
				}
				ps.close();
				res.close();
			}else{
				object = null;
			}
			if (unconnect)
				connectionManager.unconnect();
			return object;
		} catch (SQLException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		} catch (InstantiationException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		} catch (ParseException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * M�todo que busca um registro do banco de dados
	 * @param c - classe do objeto
	 * @param keyValue - valor das chaves prim�rias
	 * @return objeto resultante
	 */
	public Object getObject(Class<?> c,Object keyValue){
		return getObject(c, new Object[]{keyValue});
	}


	/**
	 * M�todo que retorna uma lista do banco de dados
	 * @param c - classe da lista
	 * @param filter - filtro sql para a consulta
	 * @param order - indice do campo que a lista deve ser ordenada
	 * @return List resultante
	 */
	public List<Object> getList(Class<?> c,String[] filter,int order){
		Field[] fields = c.getDeclaredFields();
		List<Object> list = new LinkedList<Object>();
		String sql = "SELECT * FROM "+c.getSimpleName().toLowerCase();
		if (filter != null){
			sql += " WHERE ";
			for (int i = 0; i < filter.length; i++) {
				if (i > 0) sql += " AND ";
				sql += filter[i];
			}
		}
		if (order >= 0)
			sql += " ORDER BY "+fields[order].getName();
		try {
			boolean unconnectOnClose = false;
			if (!connectionManager.isConnected()){
				connectionManager.connect();
				unconnectOnClose = true;
			}
			Statement stmt = connectionManager.createStatement();
            ResultSet res = stmt.executeQuery(sql);
			while (res.next()){
				Object object = c.asSubclass(c).newInstance();
				for (Field field : fields) {
					field.setAccessible(true);
                    if (res.getObject(field.getName()) == null)
                        continue;
					if ((field.getType().equals(Integer.TYPE)) || (field.getType().equals(Integer.class))){
						field.set(object, res.getInt(field.getName()));
					}else if ((field.getType().equals(Long.TYPE)) || (field.getType().equals(Long.class))){
						field.set(object, res.getLong(field.getName()));
					}else if ((field.getType().equals(Float.TYPE)) || (field.getType().equals(Float.class))){
						field.set(object, res.getFloat(field.getName()));
					}else if ((field.getType().equals(Double.TYPE)) || (field.getType().equals(Double.class))){
						field.set(object, res.getDouble(field.getName()));
					}else if (field.getType().equals(Date.class)){
						if (database.getDatePattern() != null){
							String strDate = res.getString(field.getName());
							if (strDate.length() == 8)
								field.set(object,new SimpleDateFormat("HH:mm:ss").parse(res.getString(field.getName())));
							else
								field.set(object,new SimpleDateFormat(database.getDatePattern()).parse(res.getString(field.getName())));
						}else{
							//System.out.println(res.getDate(field.getName())+" ??? "+res.getString(field.getName()));
							/*
							java.sql.Date sqlDate = res.getDate(field.getName());
							long l = sqlDate.getTime();
							field.set(object,new Date(l));
							Date date = res.getDate(field.getName());

							Calendar calendar = Calendar.getInstance();
							calendar.setTime(date);
							calendar.add(Calendar.DAY_OF_MONTH, 1);
							field.set(object, calendar.getTime());
							*/
							field.set(object, Convert.fromDatabase(res.getString(field.getName())));
						}
					}else if (field.getType().equals(Character.class)
							||(field.getType().equals(Character.TYPE))){
						field.set(object, res.getString(field.getName()).toCharArray()[0]);
					}else if (field.getType().equals(Boolean.class)||(field.getType().equals(Boolean.TYPE))){
						if (database.getBooleanPattern() == null){
							boolean b = res.getBoolean(field.getName());
							field.set(object, b);
						}else{
							String[] tf = database.getBooleanPattern().split("[;]");
							if (tf.length == 2){
								if (res.getString(field.getName()).compareTo(tf[0])==0)
									field.set(object, true);
								else if (res.getString(field.getName()).compareTo(tf[1])==0)
									field.set(object, false);
							}
						}
					}else if (field.getType().equals(File.class)){
						switch (database.getFileType()) {
						case RDE.FILETYPE_BLOB:
							Blob blob = res.getBlob(field.getName());
							InputStream inputStream = blob.getBinaryStream();
							field.set(object,Convert.inputStreamToTmpFile(inputStream));
							break;
						case RDE.FILETYPE_BYTE:
							field.set(object,Convert.bytesToTmpFile(res.getBytes(field.getName())));
							break;
						case RDE.FILETYPE_SYSTEM:
							field.set(object, getFileFromDisk(res.getString(field.getName())));
							break;
						default:
							break;
						}
					}else{
						field.set(object, res.getObject(field.getName()));
					}
				}
				list.add(object);
			}
			stmt.close();
			res.close();
			if (unconnectOnClose)
				connectionManager.unconnect();
			return list;
		} catch (SQLException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		} catch (InstantiationException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		} catch (ParseException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * M�todo que retorna uma lista do banco de dados
	 * @param c - classe da lista
	 * @return List resultante
	 */
	public List<Object> getList(Class<?> c){
		return getList(c,null,-1);
	}

	/**
	 * M�todo que retorna uma lista do banco de dados
	 * @param c - classe da lista
	 * @param order - indice do campo que a lista deve ser ordenada
	 * @return List resultante
	 */
	public List<Object> getList(Class<?> c,int order){
		return getList(c,null,order);
	}

	/**
	 * M�todo que retorna uma lista do banco de dados
	 * @param c - classe da lista
	 * @param filter - filtro sql para a consulta
	 * @return List resultante
	 */
	public List<Object> getList(Class<?> c,String[] filter){
		return getList(c,filter,-1);
	}

	/**
	 * M�todo que retorna um vetor de registros do banco de dados
	 * @param c - classe da lista
	 */
	public Object[] getArray(Class<?> c){
		return getList(c,null,-1).toArray();
	}

	/**
	 * M�todo que retorna um vetor de registros do banco de dados
	 * @param c - classe da lista
	 * @param order - �ndice que o vetor deve ser ordenado
	 * @return List resultante
	 */
	public Object[] getArray(Class<?> c,int order){
		return getList(c,null,order).toArray();
	}

	/**
	 * M�todo que retorna um vetor de registros do banco de dados
	 * @param c - classe da lista
	 * @param filter - filtro sql para a consulta
	 * @return List resultante
	 */
	public Object[] getArray(Class<?> c,String[] filter){
		return getList(c,filter,-1).toArray();
	}
	/**
	 * M�todo que retorna um vetor de registros do banco de dados
	 * @param c - classe da lista
	 * @param filter - filtro sql para a consulta
	 * @param order - �ndice que o vetor deve ser ordenado
	 * @return List resultante
	 */
	public Object[] getArray(Class<?> c,String[] filter,int order){
		return getList(c,filter,order).toArray();
	}

	/**
	 * M�todo que busca uma lista do DB
	 * @param string - campo a ser comparado
	 * @param c - classe da busca
	 * @param index - �ndice do campo
	 * @param findMethod - M�todo de busca: FIND_EQUALS, FIND_START_WITH, FIND_END_WITH, FIND_CONTAIN
	 * @param caseSensitive - Indica se a busca diferencia mai�suclas de min�suclas (o banco deve diferenciar tamb�m)
	 * @return List - Lista resultante
	 */
	public List<Object> findList(String string,Class<?> c,int index,int findMethod,boolean caseSensitive){
		Field[] fields = c.getDeclaredFields();
		if (index >= fields.length){
			RDEError error = new RDEError("Index out of bounds!");
			if (RDE.logErrors)
				Logger.log(error);
			throw error;
		}
		return findList(string, c, fields[index], findMethod, index,caseSensitive);
	};

	/**
	 * M�todo que busca uma lista do DB
	 * @param string - campo a ser comparado
	 * @param c - classe da busca
	 * @param index - �ndice do campo
	 * @param findMethod - M�todo de busca: FIND_EQUALS, FIND_START_WITH, FIND_END_WITH, FIND_CONTAIN
	 * @return List - Lista resultante
	 */
	public List<Object> findList(String string,Class<?> c,int index,int findMethod){
		Field[] fields = c.getDeclaredFields();
		if (index >= fields.length){
			RDEError error = new RDEError("Index out of bounds!");
			if (RDE.logErrors)
				Logger.log(error);
			throw error;
		}
		return findList(string, c, fields[index], findMethod, index,true);
	};

	/**
	 * M�todo que busca uma lista do DB
	 * @param string - campo a ser comparado
	 * @param c - classe da busca
	 * @param fieldName - Nome do campo da consulta
	 * @param findMethod - M�todo de busca: FIND_EQUALS, FIND_START_WITH, FIND_END_WITH, FIND_CONTAIN
	 * @param caseSensitive - Indica se a busca diferencia mai�suclas de min�suclas (o banco deve diferenciar tamb�m)
	 * @return List - Lista resultante
	 */
	public List<Object> findList(String string,Class<?> c,String fieldName,int findMethod,boolean caseSensitive){
		Field[] fields = c.getDeclaredFields();
		int index = -1;
		for (int i = 0; i < fields.length; i++) {
			if (fieldName.compareTo(fields[i].getName())==0){
				index = i;
				break;
			}
		}
		if (index < 0)
			return null;
		return findList(string, c, fields[index], findMethod, index,caseSensitive);
	}

	/**
	 * M�todo que busca uma lista do DB
	 * @param string - campo a ser comparado
	 * @param c - classe da busca
	 * @param fieldName - Nome do campo da consulta
	 * @param findMethod - M�todo de busca: FIND_EQUALS, FIND_START_WITH, FIND_END_WITH, FIND_CONTAIN
	 * @return List - Lista resultante
	 */
	public List<Object> findList(String string,Class<?> c,String fieldName,int findMethod){
		Field[] fields = c.getDeclaredFields();
		int index = -1;
		for (int i = 0; i < fields.length; i++) {
			if (fieldName.compareTo(fields[i].getName())==0){
				index = i;
				break;
			}
		}
		if (index < 0)
			return null;
		return findList(string, c, fields[index], findMethod, index,true);
	}

	private List<Object> findList(String string,Class<?> c,Field field,int findMethod,int order,boolean caseSensitive){
		string = string.replaceAll("[']", "''");
		String filter = caseSensitive ? "CAST(" : "UPPER(CAST(";
		filter += field.getName();
		filter += " AS "+database.getStringType()+")";
		if (!caseSensitive)
			filter += ")";
		switch (findMethod) {
		case RDE.FIND_EQUALS:
			filter += caseSensitive ? " = '"+string+"'" :" = UPPER('"+string+"')"  ;
			break;
		case RDE.FIND_START_WITH:
			filter += caseSensitive ? " LIKE '"+string+"%'":" LIKE UPPER('"+string+"%')" ;
			break;
		case RDE.FIND_END_WITH:
			filter += caseSensitive ? "LIKE '%"+string+"'":" LIKE UPPER('%"+string+"')" ;
			break;
		case RDE.FIND_CONTAIN:
			filter += caseSensitive ? " LIKE '%"+string+"%'":" LIKE UPPER('%"+string+"%')" ;
			break;
		default:
			break;
		}
		return getList(c, new String[]{filter}, order);
	}

	/**
	 * M�todo que busca um vetor de registros do DB
	 * @param string - campo a ser comparado
	 * @param c - classe da busca
	 * @param index - �ndice do campo
	 * @param findMethod - M�todo de busca: FIND_EQUALS, FIND_START_WITH, FIND_END_WITH, FIND_CONTAIN
	 * @return Object[] - Vetor resultante
	 */
	public Object[] findArray(String string,Class<?> c,int index,int findMethod,boolean caseSensitive){
		return findList(string,c,index,findMethod,caseSensitive).toArray();
	}

	/**
	 * M�todo que busca um vetor de registros do DB
	 * @param string - campo a ser comparado
	 * @param c - classe da busca
	 * @param index - �ndice do campo
	 * @param findMethod - M�todo de busca: FIND_EQUALS, FIND_START_WITH, FIND_END_WITH, FIND_CONTAIN
	 * @return Object[] - Vetor resultante
	 */
	public Object[] findArray(String string,Class<?> c,int index,int findMethod){
		return findList(string,c,index,findMethod,true).toArray();
	}
	/**
	 * M�todo que busca um vetor de registros do DB
	 * @param string - campo a ser comparado
	 * @param c - classe da busca
	 * @param fieldName - Nome do campo da consulta
	 * @param findMethod - M�todo de busca: FIND_EQUALS, FIND_START_WITH, FIND_END_WITH, FIND_CONTAIN
	 * @return Object[] - Vetor resultante
	 */
	public Object[] findArray(String string,Class<?> c,String fieldName,int findMethod,boolean caseSensitive){
		return findList(string,c,fieldName,findMethod,caseSensitive).toArray();
	}
	/**
	 * M�todo que busca um vetor de registros do DB
	 * @param string - campo a ser comparado
	 * @param c - classe da busca
	 * @param fieldName - Nome do campo da consulta
	 * @param findMethod - M�todo de busca: FIND_EQUALS, FIND_START_WITH, FIND_END_WITH, FIND_CONTAIN
	 * @return Object[] - Vetor resultante
	 */
	public Object[] findArray(String string,Class<?> c,String fieldName,int findMethod){
		return findList(string,c,fieldName,findMethod).toArray();
	}
	//TODO File method(s)

	private File getFileFromDisk(String fileName){
		String prefix = (database.getFileDir().trim().length() != 0) ? database.getFileDir() : System.getProperty("user.dir");
		if (prefix.substring(prefix.length()-1, prefix.length()).compareTo("\\") != 0)
			prefix += "\\";
		return new File(prefix+fileName);
	}

	public int getListSize(Class<?> c){
		String sql = "SELECT count(*) FROM "+c.getSimpleName().toLowerCase();
		boolean closeConn = false;
		if (!connectionManager.isConnected()){
			connectionManager.connect();
			closeConn = true;
		}
		int count = -1;
		try {
			Statement statement = connectionManager.createStatement();
			ResultSet res = statement.executeQuery(sql);
			if (res.next()){
				count = res.getInt(1);
			}
		} catch (SQLException e) {
			rde.util.Logger.log(e);
			e.printStackTrace();
		}
		if (closeConn)
			connectionManager.unconnect();
		return count;
	}
	//TODO Direct access

	public void connect(){
		connectionManager.connect();
	}
	public boolean isConnected(){
		return connectionManager.isConnected();
	}
	public void unconnect(){
		connectionManager.unconnect();
	}
	public ResultSet executeQuery(String sql) throws SQLException{
		Statement statement = connectionManager.createStatement();
		return statement.executeQuery(sql);
	}
	public Database getDatabase(){
		return database;
	}
}
