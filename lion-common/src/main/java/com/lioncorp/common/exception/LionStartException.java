package com.lioncorp.common.exception;

public class LionStartException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public LionStartException(String msg) {
		super(msg);
	}

	public LionStartException(String msg, Throwable th) {
		super(msg, th);
	}

	public LionStartException(Throwable th) {
		super(th);
	}

}
