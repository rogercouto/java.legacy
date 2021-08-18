package rde.manager;

import java.sql.Connection;

import rde.connection.ConnectionManager;
import rde.database.Database;
import rde.datasource.DataSource;
import rde.exceptions.ConnectionException;
import rde.persistence.Persistence;

/**
 * Classe que contem as entidades responsáveis por toda a conecção com o Banco
 * @author Roger
 */
public class DAOManager {

	protected static ConnectionManager connectionManager = null;
	protected static DataSource dataSource = null;
	protected static Persistence persistence = null;
	
	/**
	 * Seta o Database com as propriedades e inicia o DataModule
	 * @param database - Classe com os dados sobre o banco de dados
	 * @throws ConnectionException
	 */
	public static void setDatabase(Database database) throws ConnectionException{
		ConnectionManager.testConnection(database);
		connectionManager = new ConnectionManager(database);
		dataSource = new DataSource(connectionManager);
		persistence = new Persistence(connectionManager);
	}
	
	/**
	 * Retorna o DataSource responsável pela busca dos dados no DB
	 * @return DataSource 
	 */
	public static DataSource getDataSource() {
		return dataSource;
	}
	/**
	 * Retorna a Persistence responsável pela atualização no DB
	 * @return Persistence
	 */
	public static Persistence getPersistence() {
		return persistence;
	}

	/**
	 * Retrona java.sql.Connection para uso direto
	 * @return
	 */
	public static Connection getConnection(){
		if (!isConnected())
			connect();
		return connectionManager.getConnection();
	}
	
	/**
	 * Realiza a conexão com o banco de dados
	 */
	public static void connect(){
		connectionManager.connect();
	}
	/**
	 * Fecha a conexão com o banco de dados
	 */
	public static void unconnect(){
		connectionManager.unconnect();
	}
	/**
	 * Verifica se está conectado com o banco de dados
	 * @return
	 */
	public static boolean isConnected(){
		return connectionManager.isConnected();
	}
	
	/**
	 * Inicia uma session se o banco de dados for transicional
	 */
	public static void beginSession(){
		connectionManager.beginSession();
	}
	
	/**
	 * Confirma uma session se o banco de dados for transicional
	 */
	public static void commitSession(){
		connectionManager.commitSession();
	}
	
	/**
	 * Encerra uma session se o banco de dados for transicional
	 */
	public static void endSession(){
		connectionManager.endSession();
	}
	
}
