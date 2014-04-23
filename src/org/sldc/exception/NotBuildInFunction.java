package org.sldc.exception;

@SuppressWarnings("serial")
public class NotBuildInFunction extends SLDCException {
	
	private String funcName = null;
	
	public NotBuildInFunction(String name, Throwable t) {
		super(t);
		this.funcName = name;
	}
	
	public long exceptionID() {
		return super.ERROR_BASE+1;
	}
	
	public String getMessage() {
		return this.funcName+" is not a buildin function.";
	}
}
