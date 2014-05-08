package org.sldc.assist.multitypes;

import java.util.Collection;
import java.util.Map;

import org.sldc.assist.CSQLUtils;
import org.sldc.protocols.CSQLChunkDataImpl;

public class BuildInLength {
	@SuppressWarnings("rawtypes")
	public static long length(Object o) {
		if(o instanceof CSQLChunkDataImpl)
			return ((CSQLChunkDataImpl)o).size();
		else if(CSQLUtils.isString(o))
			return ((String)o).length();
		else if(CSQLUtils.isArray(o))
			return ((Object[])o).length;
		else if(o instanceof Collection)
			return ((Collection)o).size();
		else if(o instanceof Map)
			return ((Map)o).size();
		return 0;
	}
}
