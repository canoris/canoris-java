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

	private static final long serialVersionUID = -8546249037388884241L;

	private int httpErrorCode;
    private String httpErrorMessage;

    /**
     * Parameterized constructor.
     *
     * @param code
     *             The HTTP error code
     * @param message
     *             The HTTP error message
     */
    public CanorisException(int code, String message) {
        super(String.valueOf(code));
        this.httpErrorCode = code;
        this.httpErrorMessage = message;
    }

    public void setHttpErrorCode(int httpErrorCode) {
        this.httpErrorCode = httpErrorCode;
    }
    public int getHttpErrorCode() {
        return httpErrorCode;
    }

    public void setHttpErrorMessage(String httpErrorMessage) {
        this.httpErrorMessage = httpErrorMessage;
    }

    public String getHttpErrorMessage() {
        return httpErrorMessage;
    }

    public String toString() {
        return "HTTP_ERROR_CODE : " + this.getHttpErrorCode() + ", MESSAGE: " + this.getHttpErrorMessage();
    }
}
