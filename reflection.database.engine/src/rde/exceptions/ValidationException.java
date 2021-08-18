package rde.exceptions;

/**
 * Exce��o lan�ada quando um objeto n�o passa na valida��o
 * @author Roger
 *
 */
public class ValidationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -571841593243623071L;

	public ValidationException() {
	}

	public ValidationException(String message) {
		super(message);
	}

	public ValidationException(Throwable message) {
		super(message);
	}

	public ValidationException(String message, Throwable arg1) {
		super(message, arg1);
	}

}
