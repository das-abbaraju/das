package com.picsauditing.exception;

/**
 * Abstract base class all PICS Exceptions that are checked exceptions (which most all exceptions should be).
 * Extend it with your own module exceptions.
 *
 * TODO Eventually, this should include whatever info that we'd like to always capture.
 * TODO We might at some point find a use for a PicsRuntimeException, but probably not.
 *
 */
public abstract class PicsException extends Exception {
	public PicsException() {
	}

	public PicsException(String message) {
		super(message);
	}

	public PicsException(Throwable cause) {
		super(cause);
	}

	public PicsException(String message, Throwable cause) {
		super(message, cause);
	}
}
