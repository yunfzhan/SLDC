package org.sldc.exception;

public class SLDCException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1789142200296788800L;
	private static final String errorMsg = "Unknown error!";
	
	public long exceptionID(){
		return this.serialVersionUID;
	}
	
	public String getMessage(){
		return this.errorMsg;
	}
}
