package fr.rowlaxx.utils.generic.bounds;

import fr.rowlaxx.utils.generic.GenericUtilsException;

/**
 * Exception class for Bounds Resolver.
 * This exception should not be thrown unless is an internal resolving problem occur.
 * This include : 
 * - Unknow Type
 * - The impossibility to solve at least one TypeVariable
 * @version 2021-11-23
 * @author Theo
 * @since 1.0.0
 */
public class BoundsResolverException extends GenericUtilsException {

	private static final long serialVersionUID = -8324150669320629827L;

	public BoundsResolverException() {
		super();
	}

	public BoundsResolverException(String message, Throwable cause) {
		super(message, cause);
	}

	public BoundsResolverException(String message) {
		super(message);
	}

	public BoundsResolverException(Throwable cause) {
		super(cause);
	}	
}
