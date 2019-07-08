package com.mercury.server.exception;

import com.mercury.server.exception.data.ErrorCode;

public class MGSException extends Throwable {

	private static final long serialVersionUID = -4587939106535664369L;

	private ErrorCode errorCode;

	public MGSException() {

	}

	public MGSException(ErrorCode errorCode) {
		super(errorCode.toString());
		setErrorCode(errorCode);
	}

	public MGSException(ErrorCode errorCode, Throwable e) {
		super(errorCode.toString(), e);
		setErrorCode(errorCode);
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

}
