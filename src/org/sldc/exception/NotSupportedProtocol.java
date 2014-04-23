package org.sldc.exception;

@SuppressWarnings("serial")
public class NotSupportedProtocol extends SLDCException {
	
	private String protocol = null;
	
	public NotSupportedProtocol(String name, Throwable t){
		super(t);
		this.protocol = name;
	}
	
	public long exceptionID() {
		return super.ERROR_BASE+6;
	}
	
	public String getMessage() {
		return this.protocol+" is not a supported protocol yet.";
	}
}
