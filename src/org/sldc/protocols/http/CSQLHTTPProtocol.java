package org.sldc.protocols.http;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import org.sldc.assist.IChunkDataIntf;
import org.sldc.assist.IProtocol;
import org.sldc.exception.ProtocolException;

public class CSQLHTTPProtocol implements IProtocol {

	private String address = null;
	
	public CSQLHTTPProtocol(String addr) {
		this.address = addr;
	}
	
	@Override
	public IChunkDataIntf Retrieve(Map<String, String> assistParams) throws ProtocolException {
		try {
			CSQLHttpChunkImpl impl = new CSQLHttpChunkImpl(assistParams);
			impl.save(this.address);
			
			return impl;
		} catch (MalformedURLException e) {
			throw new ProtocolException(e, new Throwable());
		} catch (IOException e) {
			throw new ProtocolException(e, new Throwable());
		}
	}

}
