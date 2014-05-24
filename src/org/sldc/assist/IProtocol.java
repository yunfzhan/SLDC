package org.sldc.assist;

import java.util.Map;

import org.sldc.exception.ProtocolException;

/**
 * @version 0.8
 * @author Yunfei
 * Whatever protocol is implemented, it returns its own IChunkData for later access by SLDC
 */
public interface IProtocol {
	public IChunkDataIntf Retrieve(Map<String, String> assistParams) throws ProtocolException;
}
