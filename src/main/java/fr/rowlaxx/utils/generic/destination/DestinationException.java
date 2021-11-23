package fr.rowlaxx.utils.generic.destination;

import fr.rowlaxx.utils.generic.GenericUtilsException;

public class DestinationException extends GenericUtilsException {
	private static final long serialVersionUID = -3726336177959962413L;

	public DestinationException() {
		super();
	}

	public DestinationException(String message, Throwable cause) {
		super(message, cause);
	}

	public DestinationException(String message) {
		super(message);
	}

	public DestinationException(Throwable cause) {
		super(cause);
	}
}