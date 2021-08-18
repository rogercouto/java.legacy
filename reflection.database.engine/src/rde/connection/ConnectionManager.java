package rde.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;

import rde.RDE;
import rde.database.Database;
import rde.exceptions.ConnectionException;
import rde.exceptions.RDEError;
import rde.util.Logger;

/**
 * Classe para gerenciar a conec��o com o banco de dados
 * @author Roger
 *
 */
public class ConnectionManager {

	private static int count = 0;
	private static boolean showConnections = false;

	public static boolean isShowConnections() {
		return showConnections;
	}
	public static void showConnections(boolean showConnections) {
		ConnectionManager.showConnections = showConnections;
	}

	private Database database;
	private Connection connection;
	private Savepoint savepoint;
	private boolean inSession = false;

	/**
	 * Construtor
	 * @param database - Database com as configura��es do banco
	 */
	public ConnectionManager(Database database) {
		super();
		this.database = database;
	}

	//TODO Connection menagment
	/**
	 * M�todo que conecta com o banco de dados
	 */
	public void connect(){
		if (showConnections)
			System.out.print("connect("+(++count)+")");
		try {
			if (connection == null){
				Class.forName(database.getClassName());
				if (database.getUser() == null)
					connection = DriverManager.getConnection(database.getUlr());
				else{
					String pswrd = database.getPassword() != null? database.getPassword() : "";
					connection = DriverManager.getConnection(database.getUlr(),database.getUser(),pswrd);
				}
			}else{
				RDEError error = new RDEError("Already connected with database!");
				if (RDE.logErrors)
					Logger.log(error);
				throw error;
			}
		} catch (ClassNotFoundException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		} catch (SQLException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		}
	}

	/**
	 * M�todo que encerra a conec��o com o banco de dados
	 * @return confirma��o da opera��o
	 */
	public boolean unconnect(){
		if (showConnections)
			System.out.println("- unconnect("+count+")");
		if (connection == null)
			return false;
		try {
			connection.close();
			connection = null;
			return true;
		} catch (SQLException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * M�todo que encerra a conec��o com o banco de dados
	 * @return Connection - Conec��o com o Database
	 */
	public Connection getConnection(){
		return connection;
	}

	/**
	 * M�todo que verifica se est� conectado com o banco de dados
	 * @return boolean - resultado da verifica��o
	 */
	public boolean isConnected(){
		return (connection != null);
	}

	/**
	 * Prepara um SQL Statement para comunica��o com o DB
	 * @param sql - String com o comando sql
	 * @return PreparedStatemnt resultante
	 */
	public PreparedStatement prepareStatement(String sql){
		try {
			if (connection != null)
				return connection.prepareStatement(sql);
		} catch (SQLException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Cria um SQL Statement para comunica��o com o DB
	 * @return Statement resultante
	 */
	public Statement createStatement(){
		try {
			if (connection != null)
				return connection.createStatement();
		} catch (SQLException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Testa a conec��o com o DB
	 * @throws ConnectionException - Exce��o lan�ada quando n�o � possivel conectar com o DB
	 */
	public static void testConnection(Database database) throws ConnectionException{
		Connection tmpConn = null;
		try {
			Class.forName(database.getClassName());
			if (database.getUser() == null)
				tmpConn = DriverManager.getConnection(database.getUlr());
			else{
				String pswrd = database.getPassword() != null? database.getPassword() : "";
				tmpConn = DriverManager.getConnection(database.getUlr(),database.getUser(),pswrd);
			}
		} catch (ClassNotFoundException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
			throw new ConnectionException("Driver de banco de dados n�o encontrado!");
		} catch (SQLException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
			throw new ConnectionException("N�o foi poss�vel conectar com o banco de dados!");
		}
		if (tmpConn != null){
			try {
				tmpConn.close();
			} catch (SQLException e) {
				if (RDE.logErrors)
					Logger.log(e);
				e.printStackTrace();
			}
		}
	}

	//TODO Session managment

	/**
	 * Inicia uma transa��o
	 */
	public void beginSession(){
		//System.err.println("beginSession()"+count);
		try {
			if (connection == null)
				throw new UnsupportedOperationException("Not connected to database!");
			inSession = true;
			if (database.isTransactional()){
				connection.setAutoCommit(false);
				savepoint = connection.setSavepoint();
			}
		} catch (SQLException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		}
	}
	/**
	 * Confirma as altera��es na transa��o com o DB
	 */
	public void commitSession(){
		if (connection == null){
			RDEError error = new RDEError("Not connected to database!");
			if (RDE.logErrors)
				Logger.log(error);
			throw error;
		}
		try {
			if (database.isTransactional()){
				connection.commit();
				savepoint = connection.setSavepoint();
			}
		} catch (SQLException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		}
	}
	/**
	 * Desfaz as altera��es na transa��o com o DB
	 */
	public void rolbackSession(){
		if (connection == null){
			RDEError error = new RDEError("Not connected to database!");
			if (RDE.logErrors)
				Logger.log(error);
			throw error;
		}
		try {
			if (database.isTransactional()){
				connection.rollback(savepoint);
			}
		} catch (SQLException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		}
	}
	/**
	 * Encerra a transa��o com o DB
	 */
	public void endSession(){
		//System.err.println("endSession()"+count);
		inSession = false;
		savepoint = null;
		try {
			if (database.isTransactional())
				connection.setAutoCommit(true);
		} catch (SQLException e) {
			if (RDE.logErrors)
				Logger.log(e);
			e.printStackTrace();
		}
	}

	/**
	 * Verifica se tem uma Transa��o aberta
	 * @return boolean resultado da verifica��o
	 */
	public boolean inSession(){
		return inSession;
	}

	//TODO Getters Setters

	/**
	 * Retorna o Database da conec��o
	 * @return Database
	 */
	public Database getDatabase() {
		return database;
	}

}
