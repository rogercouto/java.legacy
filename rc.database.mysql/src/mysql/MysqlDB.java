package mysql;

import rde.RDE;
import rde.database.Database;

public class MysqlDB extends Database{

	private String hostname = "localhost";
	private String port = "3306";
	private String database = null;
	private String user = null;
	private String password = null;

	public MysqlDB(String database,String user,String password) {
		super();
		this.database = database;
		this.user = user;
		this.password = password;
		initialize();
	}
	public MysqlDB(String hostname, String port,String database,
			String user,String password ) {
		super();
		this.hostname = hostname;
		this.port = port;
		this.database = database;
		this.user = user;
		this.password = password;
		initialize();
	}
	public MysqlDB(String hostname, String database,
			String user,String password ) {
		super();
		this.hostname = hostname;
		this.database = database;
		this.user = user;
		this.password = password;
		initialize();
	}
	public MysqlDB(String database,String user) {
		super();
		this.database = database;
		this.user = user;
		initialize();
	}

	private void initialize(){
		this.driver = "com.mysql.jdbc.Driver";
		this.url = "jdbc:mysql://"+hostname+":"+port+"/"+database+"?useTimezone=true&serverTimezone=UTC";
		this.fileType = RDE.FILETYPE_BLOB;
		this.transactional = true;
		this.booleanPattern = null;
		this.datePattern = null;
		this.stringType = null;
		this.concatFunction = "CONCAT";

	}
	public String getHostname() {
		return hostname;
	}
	public String getPort() {
		return port;
	}
	public String getDatabase() {
		return database;
	}
	public String getUser() {
		return user;
	}
	public String getPassword() {
		return password;
	}

}
