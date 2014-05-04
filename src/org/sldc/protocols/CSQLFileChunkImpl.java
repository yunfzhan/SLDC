package org.sldc.protocols;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.sldc.assist.CSQLUtils;
import org.sldc.exception.NotSupportOperation;

public class CSQLFileChunkImpl extends CSQLChunkDataImpl {

	private File file = null;
	
	public CSQLFileChunkImpl(File f){
		file = f;
	}
	
	@Override
	public Object fetchItem(Object idx) {
		try {
			Object result = null;
			RandomAccessFile raf = new RandomAccessFile(this.file, "r");
			if(CSQLUtils.isInt(idx)){
				int beg = (Integer)idx;
				raf.seek(beg);
				result = raf.read();
			}
			raf.close();
			return result;
		} catch (FileNotFoundException e) {
			return e;
		} catch (IOException e) {
			return e;
		}
	}

	public String toString() {
		return "";
	}

	@Override
	public Object searchByTag(String tag) {
		return new NotSupportOperation(new Throwable());
	}

	@Override
	public Object search(String re) {
		return null;
	}
	
}
