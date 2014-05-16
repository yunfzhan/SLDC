package org.sldc.assist;

import org.sldc.exception.NotSupportedProtocol;

/**
 * @version 1.0
 * @author Yunfei
 * Factory create a kind of protocol by its indicator.
 */
public interface IProtocolFactory {
	public IProtocol Create(String protocol) throws NotSupportedProtocol;
}
