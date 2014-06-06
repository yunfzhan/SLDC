package org.sldc.exception;

@SuppressWarnings("serial")
public class SyntaxException extends SLDCException {

	private String msg = "Syntax Error!";
	
	public SyntaxException(Throwable t) {
		super(t);
	}
	
	public SyntaxException(Throwable t, String error) {
		super(t);
		msg = error;
	}

	public long exceptionID() {
		return super.ERROR_BASE+8;
	}
	
	public String getMessage() {
		return msg;
	}
}
