package org.sldc.protocols;

import java.util.HashMap;
import java.util.Map;

import org.sldc.assist.IProtocol;
import org.sldc.assist.IProtocolFactory;
import org.sldc.exception.NotSupportedProtocol;

public class CSQLProtocolFactoryImpl implements IProtocolFactory {

	private static final Map<String, String> path4Protocol = new HashMap<String, String>();
	
	static {
		path4Protocol.put("HTTP", "org.sldc.protocols.http.CSQLHTTPProtocol");
		path4Protocol.put("HTTPS", "org.sldc.protocols.http.CSQLHTTPProtocol");
		path4Protocol.put("FILES", "org.sldc.protocols.files.CSQLFilesProtocol");
		path4Protocol.put("FTP", "org.sldc.protocols.ftp.CSQLFTPProtocol");
		path4Protocol.put("DB", "org.sldc.protocols.db.CSQLDBProtocol");
	}
	
	@Override
	public IProtocol Create(String protocol) throws NotSupportedProtocol {
		String prtl = protocol.substring(0, protocol.indexOf(':')).toUpperCase();
		try{
			if(!path4Protocol.containsKey(prtl)) return null;
				
			Class<?> protoClass = Class.forName(path4Protocol.get(prtl));
			return (IProtocol) protoClass.getConstructor(String.class).newInstance(protocol);
		} catch (Exception e) {
			throw new NotSupportedProtocol(prtl, new Throwable());
		}
	}

}
