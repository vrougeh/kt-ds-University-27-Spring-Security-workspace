package com.ktdsuniversity.edu.exceptions;

public class HelloSpringApiException extends RuntimeException {
	private static final long serialVersionUID = -7175349620697935148L;

	private int errorStatus;
	private Object error;

	public HelloSpringApiException(String message, int errorStatus, Object error) {
		super(message);
		this.errorStatus = errorStatus;
		this.error = error;
	}

	public int getErrorStatus() {
		return this.errorStatus;
	}
	public Object getError() {
		return this.error;
	}

}
