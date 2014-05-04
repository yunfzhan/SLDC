package org.sldc.protocols;

import java.io.File;
import java.io.IOException;

import org.sldc.assist.CSQLChunkDataIntf;

public abstract class CSQLChunkDataImpl implements CSQLChunkDataIntf {
	protected File createTempFile() throws IOException {
		return File.createTempFile("sldc", "dmp");
	}
	
	public abstract Object searchByTag(String tag); // Specific for html-like or xml-like
	public abstract Object search(String re); // regular expression search
}
