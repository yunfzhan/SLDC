package org.sldc.assist;

import org.sldc.exception.NotSupportedProtocol;

public class CSQLProtocolFactoryImpl implements CSQLProtocolFactory {

	@Override
	public CSQLProtocol Create(String protocol) throws NotSupportedProtocol {
		String prtl = protocol.substring(0, protocol.indexOf(':'));
		try{
			if(prtl!=null)
			{
				String className = "CSQL"+prtl.toUpperCase()+"Protocol";	
				Class<?> protoClass = Class.forName("org.sldc.protocols."+className);
				return (CSQLProtocol) protoClass.getConstructor(String.class).newInstance(protocol);
			}
		} catch (Exception e) {
			throw new NotSupportedProtocol(prtl, new Throwable());
		}
		return null;
	}

}
