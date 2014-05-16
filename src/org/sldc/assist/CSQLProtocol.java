package org.sldc.assist;

import org.sldc.exception.ProtocolException;

public interface CSQLProtocol {
	public IChunkDataIntf Retrieve() throws ProtocolException;
}
