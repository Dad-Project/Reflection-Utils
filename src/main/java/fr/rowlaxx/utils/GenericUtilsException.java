package fr.rowlaxx.utils;

public class GenericUtilsException extends RuntimeException {
	private static final long serialVersionUID = -8915224053551673644L;

	public GenericUtilsException() {
		super();
	}

	public GenericUtilsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
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