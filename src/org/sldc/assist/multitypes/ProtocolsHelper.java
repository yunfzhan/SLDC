package org.sldc.assist.multitypes;

import java.util.ArrayList;
import java.util.Collection;

import org.sldc.assist.CSQLChunkDataIntf;
import org.sldc.assist.CSQLProtocol;
import org.sldc.assist.CSQLProtocolFactory;
import org.sldc.assist.CSQLUtils;
import org.sldc.exception.NotSupportedProtocol;
import org.sldc.exception.ProtocolException;

public class ProtocolsHelper {
	@SuppressWarnings("unchecked")
	public static Object Retrieve(CSQLProtocolFactory _pFactory, Object addrs) throws ProtocolException, NotSupportedProtocol {
		if(CSQLUtils.isString(addrs))
		{
			addrs = CSQLUtils.removeStringBounds((String) addrs);
			CSQLProtocol protocol = _pFactory.Create((String) addrs);
			return protocol.Retrieve();
		}else if(CSQLUtils.isArray(addrs)) {
			ArrayList<CSQLChunkDataIntf> res = new ArrayList<CSQLChunkDataIntf>();
			for(String addr : (String[])addrs)
			{
				CSQLProtocol prot = _pFactory.Create(addr);
				res.add(prot.Retrieve());
			}
			return res;
		}else if(addrs instanceof Collection) {
			ArrayList<CSQLChunkDataIntf> res = new ArrayList<CSQLChunkDataIntf>();
			for(String addr : (Collection<String>)addrs)
			{
				CSQLProtocol prot = _pFactory.Create(addr);
				res.add(prot.Retrieve());
			}
			return res;
		}
		return null;
	}
}
