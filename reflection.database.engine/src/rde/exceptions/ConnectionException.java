package rde.exceptions;

/**
 * Exceção lançada quando não é possível fazer uma conecção com o banco de dados 
 * @author Roger
 *
 */
public class ConnectionException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -327001319951319271L;

	public ConnectionException() {
		super();
	}

	public ConnectionException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ConnectionException(String arg0) {
		super(arg0);
	}

	public ConnectionException(Throwable arg0) {
		super(arg0);
	}

}
