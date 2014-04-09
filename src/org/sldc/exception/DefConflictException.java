package org.sldc.exception;

@SuppressWarnings("serial")
public class DefConflictException extends SLDCException {
	
	private String varName = null;
	
	public DefConflictException(String name) {
		this.varName = name;
	}
	
	public long exceptionID() {
		return super.ERROR_BASE+5;
	}
	
	public String getMessage() {
		return this.varName+" is defined more than once.";
	}
}
