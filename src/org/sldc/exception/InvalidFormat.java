package org.sldc.exception;

@SuppressWarnings("serial")
public class InvalidFormat extends SLDCException {
	
	private String varName = null;
	
	public InvalidFormat(String name, Throwable t){
		super(t);
		this.varName = name;
	}
	
	public long exceptionID() {
		return super.ERROR_BASE+3;
	}
	
	public String getMessage() {
		return this.varName+" has an invalid format.";
	}
}
