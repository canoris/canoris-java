package com.canoris.exception;

import java.io.IOException;

public class CanorisException extends Exception {
	
	private int httpErrorCode;

	public CanorisException(int code, Throwable cause) {
		super(String.valueOf(code), cause);
		this.httpErrorCode = code;
	}
	
	public void setHttpErrorCode(int httpErrorCode) {
		this.httpErrorCode = httpErrorCode;
	}
	public int getHttpErrorCode() {
		return httpErrorCode;
	}
}
