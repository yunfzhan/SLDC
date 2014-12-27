package org.sldc.protocols;

import java.io.File;
import java.io.IOException;

import org.sldc.assist.IChunkDataIntf;
import org.sldc.assist.IConstants;

public abstract class CSQLChunkDataImpl implements IChunkDataIntf, IConstants {
	protected File createTempFile() throws IOException {
		return File.createTempFile("sldc", "dmp");
	}
	
	public String toString() {
		return this.getClass().getName();
	}
	
	public abstract long size();
	
	public abstract Object searchByElement(String elem, int indicator); // Specific for html-like or xml-like
	public abstract Object search(String re); // regular expression search
}
