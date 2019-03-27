package com.mercury.server.exception;

public class ProcessMessageException extends Exception {

	private static final long serialVersionUID = 9118986924557618339L;

	public ProcessMessageException() {

	}

	public ProcessMessageException(String message) {
		super(message);
	}

	public ProcessMessageException(Throwable ex) {
		super(ex);
	}

	public ProcessMessageException(String message, Throwable ex) {
		super(message, ex);
	}
}
