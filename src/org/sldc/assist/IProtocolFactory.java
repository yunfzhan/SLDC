package org.sldc.assist;

import org.sldc.exception.NotSupportedProtocol;

public interface IProtocolFactory {
	public CSQLProtocol Create(String protocol) throws NotSupportedProtocol;
}
