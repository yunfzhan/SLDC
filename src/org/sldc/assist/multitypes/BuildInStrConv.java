package org.sldc.assist.multitypes;

import org.sldc.protocols.CSQLChunkDataImpl;

public class BuildInStrConv {
	public static String toString(Object o) {
		if(o instanceof CSQLChunkDataImpl)
			return ((CSQLChunkDataImpl)o).toString();
		else if(o instanceof String)
			return (String)o;
		return o.toString();
	}
}
