package org.sldc.assist.multitypes;

import java.util.ArrayList;
import java.util.Collection;

import org.sldc.assist.CSQLUtils;
import org.sldc.assist.IChunkDataIntf;
import org.sldc.assist.IProtocol;
import org.sldc.assist.IProtocolFactory;
import org.sldc.core.CSQLWhereExecution;
import org.sldc.csql.syntax.Scope;
import org.sldc.exception.NotSupportedProtocol;
import org.sldc.exception.ProtocolException;

public class ProtocolsHelper {
	@SuppressWarnings("unchecked")
	public static Object Retrieve(Object addrs, CSQLWhereExecution runner) throws ProtocolException, NotSupportedProtocol {
		IProtocolFactory _pFactory = CSQLUtils.getProtocolFactory();
		if(CSQLUtils.isString(addrs))
		{
			addrs = CSQLUtils.removeStringBounds((String) addrs);
			IProtocol protocol = _pFactory.Create((String) addrs);
			return protocol.Retrieve(runner);
		}else if(CSQLUtils.isArray(addrs)) {
			ArrayList<IChunkDataIntf> res = new ArrayList<IChunkDataIntf>();
			for(String addr : (String[])addrs)
			{
				IProtocol prot = _pFactory.Create(CSQLUtils.removeStringBounds(addr));
				res.add(prot.Retrieve(runner));
			}
			return res;
		}else if(CSQLUtils.isCollection(addrs)) {
			ArrayList<IChunkDataIntf> res = new ArrayList<IChunkDataIntf>();
			for(String addr : (Collection<String>)addrs)
			{
				IProtocol prot = _pFactory.Create(CSQLUtils.removeStringBounds(addr));
				res.add(prot.Retrieve(runner));
			}
			return res;
		}
		return Scope.UnDefined;
	}
}
