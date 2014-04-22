package org.sldc;

import org.sldc.exception.ProtocolException;

public interface CSQLProtocol {
	public Object Retrieve() throws ProtocolException;
}
