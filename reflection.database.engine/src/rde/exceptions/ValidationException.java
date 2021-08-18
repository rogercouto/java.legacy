package rde.exceptions;

/**
 * Exceção lançada quando um objeto não passa na validação
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
