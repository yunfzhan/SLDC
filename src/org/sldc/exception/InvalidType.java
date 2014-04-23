package org.sldc.exception;

@SuppressWarnings("serial")
public class InvalidType extends SLDCException {
	
	public InvalidType(Throwable t) {
		super(t);
	}

	public long exceptionID() {
		return super.ERROR_BASE+2;
	}
	
	public String getMessage() {
		return "Invalid data type is found.";
	}
}
