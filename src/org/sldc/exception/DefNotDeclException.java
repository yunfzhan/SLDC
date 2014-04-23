package org.sldc.exception;

@SuppressWarnings("serial")
public class DefNotDeclException extends SLDCException {

	private String varName = null;
	
	public DefNotDeclException(String name, Throwable t){
		super(t);
		this.varName = name;
	}
	
	public long exceptionID() {
		return super.ERROR_BASE+4;
	}
	
	public String getMessage() {
		return this.varName+" is not declared yet.";
	}
}
