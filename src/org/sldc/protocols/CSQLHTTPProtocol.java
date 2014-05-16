package org.sldc.protocols;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.sldc.assist.IChunkDataIntf;
import org.sldc.assist.CSQLProtocol;
import org.sldc.exception.ProtocolException;

public class CSQLHTTPProtocol implements CSQLProtocol {

	private String address = null;
	
	public CSQLHTTPProtocol(String addr) {
		this.address = addr;
	}
	
	@Override
	public IChunkDataIntf Retrieve() throws ProtocolException {
		try {
			URL url = new URL(this.address);
			CSQLHttpChunkImpl impl = new CSQLHttpChunkImpl();
			impl.save(url.openStream());
			
			return impl;
		} catch (MalformedURLException e) {
			throw new ProtocolException(e, new Throwable());
		} catch (IOException e) {
			throw new ProtocolException(e, new Throwable());
		}
	}

}
