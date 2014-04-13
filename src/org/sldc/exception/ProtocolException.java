package org.sldc.exception;

@SuppressWarnings("serial")
public class ProtocolException extends SLDCException {
	
	private String msg = null;
	
	public ProtocolException(String msg)
	{
		this.msg = msg;
	}
	
	public long exceptionID() {
		return super.ERROR_BASE+7;
	}
	
	public String getMessage() {
		return this.msg;
	}
}
