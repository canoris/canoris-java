package com.canoris.api.exception;

/**
 * CanorisException is thrown when a JsonParseException occurs.
 * We wrap the JsonParseException and extract the HTTP error code
 * from the HttpResponse object. Essentially this is the only 
 * meaningful part of the response in case of an error. 
 * 
 * @author stelios
 *
 */
public class CanorisException extends Exception {
	
	private int httpErrorCode;

	/**
	 * Parameterized constructor.
	 * 
	 * @param code
	 * 			The HTTP error code
	 * @param cause
	 * 			The initial exception (Throwable)
	 */
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
