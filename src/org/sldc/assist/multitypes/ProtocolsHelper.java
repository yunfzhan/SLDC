package org.sldc.assist.multitypes;

import java.util.ArrayList;
import java.util.Collection;

import org.sldc.assist.IChunkDataIntf;
import org.sldc.assist.CSQLProtocol;
import org.sldc.assist.IProtocolFactory;
import org.sldc.assist.CSQLUtils;
import org.sldc.csql.syntax.Scope;
import org.sldc.exception.NotSupportedProtocol;
import org.sldc.exception.ProtocolException;

public class ProtocolsHelper {
	@SuppressWarnings("unchecked")
	public static Object Retrieve(IProtocolFactory _pFactory, Object addrs) throws ProtocolException, NotSupportedProtocol {
		if(CSQLUtils.isString(addrs))
		{
			addrs = CSQLUtils.removeStringBounds((String) addrs);
			CSQLProtocol protocol = _pFactory.Create((String) addrs);
			return protocol.Retrieve();
		}else if(CSQLUtils.isArray(addrs)) {
			ArrayList<IChunkDataIntf> res = new ArrayList<IChunkDataIntf>();
			for(String addr : (String[])addrs)
			{
				CSQLProtocol prot = _pFactory.Create(CSQLUtils.removeStringBounds(addr));
				res.add(prot.Retrieve());
			}
			return res;
		}else if(CSQLUtils.isCollection(addrs)) {
			ArrayList<IChunkDataIntf> res = new ArrayList<IChunkDataIntf>();
			for(String addr : (Collection<String>)addrs)
			{
				CSQLProtocol prot = _pFactory.Create(CSQLUtils.removeStringBounds(addr));
				res.add(prot.Retrieve());
			}
			return res;
		}
		return Scope.UnDefined;
	}
}
