package com.canoris.exception;

import java.io.IOException;

public class CanorisException extends Exception {
	
	private int httpErrorCode;

	public CanorisException(int code, Throwable cause) {
		super(String.valueOf(code), cause);
	}
	
	public int getHttpErrorCode() {
		return httpErrorCode;
	}
}
