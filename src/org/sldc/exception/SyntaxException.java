package org.sldc.exception;

@SuppressWarnings("serial")
public class SyntaxException extends SLDCException {

	public SyntaxException(Throwable t) {
		super(t);
	}

	public long exceptionID() {
		return super.ERROR_BASE+8;
	}
	
	public String getMessage() {
		return "Syntax Error!";
	}
}
