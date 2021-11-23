package fr.rowlaxx.utils.generic;

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