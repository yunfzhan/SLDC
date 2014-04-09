package org.sldc.exception;

@SuppressWarnings("serial")
public class SLDCException extends Exception {
	
	protected final long ERROR_BASE=10000;
	
	public long exceptionID(){
		return -1;
	}
	
	public String getMessage(){
		return "Unknown error!";
	}
}
