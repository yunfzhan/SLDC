package org.sldc.assist.multitypes;

import java.util.ArrayList;

import org.sldc.assist.CSQLUtils;
import org.sldc.assist.IChunkDataIntf;
import org.sldc.assist.IProtocol;
import org.sldc.assist.IProtocolFactory;
import org.sldc.assist.ItemIterator;
import org.sldc.core.CSQLWhereExecution;
import org.sldc.csql.syntax.Scope;
import org.sldc.exception.NotSupportedProtocol;
import org.sldc.exception.ProtocolException;

public class ProtocolsHelper {
	public static Object Retrieve(Object addrs, Scope scope) throws ProtocolException, NotSupportedProtocol {
		IProtocolFactory _pFactory = CSQLUtils.getProtocolFactory();
		CSQLWhereExecution runner = new CSQLWhereExecution(scope);
		if(CSQLUtils.isString(addrs))
		{
			addrs = CSQLUtils.removeStringBounds((String) addrs);
			IProtocol protocol = _pFactory.Create((String) addrs);
			return protocol.Retrieve(runner);
		}else if(CSQLUtils.isArray(addrs)||CSQLUtils.isCollection(addrs)) {
			ArrayList<IChunkDataIntf> res = new ArrayList<IChunkDataIntf>();
			ItemIterator items = new ItemIterator(addrs);
			for(Object addr : items)
			{
				IProtocol prot = _pFactory.Create(CSQLUtils.removeStringBounds((String) addr));
				res.add(prot.Retrieve(runner));
			}
			return res;
		}
		return Scope.UnDefined;
	}
}
