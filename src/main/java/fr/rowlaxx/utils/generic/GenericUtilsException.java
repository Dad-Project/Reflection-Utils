package fr.rowlaxx.utils.generic;

/**
 * Exception class for all the generic related stuff.
 * @author Theo
 * @since 1.0.0
 * @version 2021-11-23
 */
public class GenericUtilsException extends RuntimeException {

	private static final long serialVersionUID = -4173546150421668027L;

	public GenericUtilsException() {
		super();
	}

	protected GenericUtilsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public GenericUtilsException(String message, Throwable cause) {
		super(message, cause);
	}

	public GenericUtilsException(String message) {
		super(message);
	}

	public GenericUtilsException(Throwable cause) {
		super(cause);
	}	
}