package fr.rowlaxx.utils;

/**
 * Exception class for all the generic related stuff.
 * @author Theo
 * @since 1.0.0
 * @version 2021-11-23
 */
public class ReflectionUtilsException extends RuntimeException {

	private static final long serialVersionUID = -4173546150421668027L;

	public ReflectionUtilsException() {
		super();
	}

	protected ReflectionUtilsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ReflectionUtilsException(String message, Throwable cause) {
		super(message, cause);
	}

	public ReflectionUtilsException(String message) {
		super(message);
	}

	public ReflectionUtilsException(Throwable cause) {
		super(cause);
	}	
}