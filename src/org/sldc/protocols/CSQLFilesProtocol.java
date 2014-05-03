package org.sldc.protocols;

import java.io.File;
import java.io.FileInputStream;

import org.sldc.CSQLProtocol;
import org.sldc.exception.InvalidType;
import org.sldc.exception.ProtocolException;

public class CSQLFilesProtocol implements CSQLProtocol {

	private String filepath = null;
	
	public CSQLFilesProtocol(String path) {
		this.filepath = path;
	}
	
	@Override
	public Object Retrieve() throws ProtocolException {
		File f = new File(this.filepath);
		try {
			if(!f.isFile()||!f.canRead()) throw new InvalidType(new Throwable());
			long len = f.length();
			Byte[] result = new Byte[(int) len];
			FileInputStream fis = new FileInputStream(f);
			byte[] buff = new byte[256];
			int byteread = 0;
			int dstpos = 0;
			while((byteread=fis.read(buff))!=-1)
			{
				System.arraycopy(buff, 0, result, dstpos, byteread);
				dstpos += byteread;
			}
			fis.close();
			return result;
		} catch (Exception e) {
			throw new ProtocolException(e, new Throwable());
		}
	}

}
