package org.sldc;

import org.sldc.exception.NotSupportedProtocol;

public interface CSQLProtocolFactory {
	public CSQLProtocol Create(String protocol) throws NotSupportedProtocol;
}
