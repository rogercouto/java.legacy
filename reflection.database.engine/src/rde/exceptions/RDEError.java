package rde.exceptions;

public class RDEError extends UnsupportedOperationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7824608164504763135L;

	public RDEError() {
		super();
	}

	public RDEError(String message) {
		super(message);
	}

	public RDEError(Throwable cause) {
		super(cause);
	}

	public RDEError(String message, Throwable cause) {
		super(message, cause);
	}

}
