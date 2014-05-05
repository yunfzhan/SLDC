package org.sldc.protocols;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	@Override
	public Object searchByTag(String tag) {
		return new NotSupportOperation(new Throwable());
	}

	@Override
	public Object search(String re) {
		final int PAGESIZE = 2048;
		try {
			Pattern p = Pattern.compile(re);
			ArrayList<String> res = new ArrayList<String>();
			
			InputStream is = new FileInputStream(this.file);
			/*
			 * Read one page at a time. And search regular expression matches in two pages.
			 * 
			 */
			byte[] page = new byte[PAGESIZE*2];
			int bytesread = 0, beg = 0, lastop = 0;
			while((bytesread=is.read(page, beg, PAGESIZE))!=-1){
				String s = new String(page,0,beg==0?PAGESIZE:(PAGESIZE+bytesread));
				Matcher m = p.matcher(s);
				while(m.find()){
					if(m.start()<=lastop) continue;
					res.add(m.group());
					//res.add(s.substring(m.start()-50,m.end()+20));
					lastop = m.start();
				}
				
				lastop=(lastop>=beg)?lastop-beg:0;
				
				if(beg!=0)
					System.arraycopy(page, PAGESIZE, page, 0, bytesread);
				else
					beg = PAGESIZE;
			}
			is.close();
			return res.size()==0?false:res;
		} catch (FileNotFoundException e) {
			return e;
		} catch (IOException e) {
			return e;
		}
	}
	
}
