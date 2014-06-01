package org.sldc.exception;

@SuppressWarnings("serial")
public class ProtocolException extends SLDCException {
	
	private Exception e = null;
	
	public ProtocolException(Exception ex, Throwable t)
	{
		super(t);
		this.e = ex;
	}
	
	public long exceptionID() {
		return super.ERROR_BASE+7;
	}
	
	public String getMessage() {
		return "Protocol Error with "+this.e.toString();
	}
	
	public Exception getNestedException(){
		return this.e;
	}
}
