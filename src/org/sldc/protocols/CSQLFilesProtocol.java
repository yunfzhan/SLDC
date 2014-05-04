package org.sldc.protocols;

import java.io.File;

import org.sldc.assist.CSQLChunkDataIntf;
import org.sldc.assist.CSQLProtocol;
import org.sldc.exception.InvalidType;
import org.sldc.exception.ProtocolException;

public class CSQLFilesProtocol implements CSQLProtocol {

	private String filepath = null;
	
	public CSQLFilesProtocol(String path) {
		this.filepath = path;
	}
	
	@Override
	public CSQLChunkDataIntf Retrieve() throws ProtocolException {
		File f = new File(this.filepath);
		try {
			if(!f.isFile()||!f.canRead()) throw new InvalidType(new Throwable());
			CSQLFileChunkImpl impl = new CSQLFileChunkImpl(f);
			return impl;
		} catch (Exception e) {
			throw new ProtocolException(e, new Throwable());
		}
	}

}
