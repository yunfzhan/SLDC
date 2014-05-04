package org.sldc.exception;

@SuppressWarnings("serial")
public class NotSupportOperation extends SLDCException {

	public NotSupportOperation(Throwable t) {
		super(t);
	}

	public long exceptionID() {
		return super.ERROR_BASE+9;
	}
	
	public String getMessage() {
		return "Do not support the operation!";
	}
}
