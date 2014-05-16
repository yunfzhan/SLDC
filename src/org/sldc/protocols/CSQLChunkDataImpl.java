package org.sldc.protocols;

import java.io.File;
import java.io.IOException;

import org.sldc.assist.IChunkDataIntf;

public abstract class CSQLChunkDataImpl implements IChunkDataIntf {
	protected File createTempFile() throws IOException {
		return File.createTempFile("sldc", "dmp");
	}
	
	public String toString() {
		return this.getClass().getName();
	}
	
	public abstract long size();
	
	public abstract Object searchByTag(String tag); // Specific for html-like or xml-like
	public abstract Object search(String re); // regular expression search
}
