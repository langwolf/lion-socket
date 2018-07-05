package com.lioncorp.common.exception;


public class InsertIndexException extends RuntimeException {


    /**
	 * 
	 */
	private static final long serialVersionUID = 467103812708529877L;

	public InsertIndexException() {
    }

    public InsertIndexException(String message, Throwable cause) {
        super(message, cause);
    }

    public InsertIndexException(String message) {
        super(message);
    }
}
