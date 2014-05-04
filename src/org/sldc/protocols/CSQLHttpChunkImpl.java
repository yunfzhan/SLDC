package org.sldc.protocols;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sldc.assist.CSQLUtils;

public class CSQLHttpChunkImpl extends CSQLChunkDataImpl {

	private File internalPath = null;
	
	public void save(InputStream is) throws IOException {
		BufferedReader br=new BufferedReader(new InputStreamReader(is));
		this.internalPath = createTempFile();
		FileWriter os = new FileWriter(this.internalPath);
		try{
			String line;
			while((line=br.readLine())!=null) 
				os.write(line);
		}finally{
			os.close();
			is.close();
		}
	}
	
	@Override
	public Object fetchItem(Object idx) {
		try{
			if(CSQLUtils.isInt(idx))
			{
				RandomAccessFile raf = new RandomAccessFile(this.internalPath,"r");
				try{
					int beg = (Integer)idx;
					raf.seek(beg);
					return raf.read();
				}finally{
					raf.close();
				}
			}else if(CSQLUtils.isString(idx))
			{
				String[] res = HTMLAnalyzer.startAnalyze(this.internalPath, (String)idx);
				return res;
			}
			return null;
		} catch (FileNotFoundException e) {
			return e;
		} catch (IOException e) {
			return e;
		}
	}

	public String toString() {
		try {
			Reader r = new FileReader(this.internalPath);
			char[] buff = new char[1024];
			StringBuilder sb = new StringBuilder();
			while(r.read(buff)!=-1)
				sb.append(buff);
			r.close();
			return sb.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public Object searchByTag(String tag) {
		return fetchItem(tag);
	}

	@Override
	public Object search(String re) {
		String s = toString();
		Pattern p = Pattern.compile(re);
		Matcher m = p.matcher(s);
		if(!m.find()) return false;
		String[] res = new String[m.groupCount()];
		for(int i=0;i<res.length;i++)
			res[i] = m.group(i);
		return res;
	}
}
