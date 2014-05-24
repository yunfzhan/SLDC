package org.sldc.protocols.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sldc.assist.CSQLUtils;
import org.sldc.assist.HTMLAnalyzer;
import org.sldc.exception.InvalidType;
import org.sldc.protocols.CSQLChunkDataImpl;

public class CSQLHttpChunkImpl extends CSQLChunkDataImpl {

	private final String METHOD = "method";
	private final String PROPERTYFILE = "propfile";
	
	private File internalPath = null;
	
	Map<String, String> assistParams = null;
	
	CSQLHttpChunkImpl(Map<String, String> assistParams) {
		this.assistParams = assistParams;
	}
	
	public void save(String address) throws IOException {
		this.internalPath = createTempFile();
		HttpRequestHelper helper = new HttpRequestHelper(this.internalPath);
		String method = (assistParams!=null&&assistParams.containsKey(METHOD))?assistParams.get(METHOD):"GET";
		if(method.equalsIgnoreCase("GET"))
			helper.doGet(address);
		else if(method.equalsIgnoreCase("POST")){
			Map<String, String> post = new HashMap<String, String>();
			if(assistParams.containsKey(PROPERTYFILE)) {
				File f = new File(assistParams.get(PROPERTYFILE));
				if(f.exists()){
					Properties props = new Properties();
					props.load(new FileInputStream(f));
					for(Object key : props.keySet())
					{
						Object value = props.get(key);
						post.put(key.toString(), value.toString());
					}
				}
			}
			helper.doPost(address, post);
		}
	}
	
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
	public Object getItem(Object idx) {
		try{
			if(CSQLUtils.isInt(idx))
			{
				BufferedReader reader = new BufferedReader(new FileReader(this.internalPath));
				try{
					if(CSQLUtils.isInt(idx)){
						long index = CSQLUtils.ToInt(idx);
						reader.skip(index-1);
						char[] result = new char[1];
						reader.read(result);
						return result[0];
					}
				}finally{
					reader.close();
				}
			}else if(CSQLUtils.isString(idx))
			{
				return HTMLAnalyzer.startAnalyze(this.internalPath, (String)idx);
			}
			return null;
		} catch (InvalidType e) {
			return e;
		}catch (FileNotFoundException e) {
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
		return getItem(tag);
	}

	@Override
	public Object search(String re) {
		final int PAGESIZE = 2048;
		try {
			Pattern p = Pattern.compile(re);
			ArrayList<String> res = new ArrayList<String>();
			
			InputStream is = new FileInputStream(this.internalPath);
			/*
			 * Read one page at a time. And search regular expression matches in two pages.
			 * 
			 */
			byte[] page = new byte[PAGESIZE*2];
			int bytesread = 0, beg = 0, lastop = 0;
			while((bytesread=is.read(page, beg, PAGESIZE))!=-1){
				String s = new String(page,0,beg==0?PAGESIZE:(PAGESIZE+bytesread)/*Specify range especially for bytesread not equal to PAGESIZE*/);
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

	@Override
	public long size() {
		return this.internalPath.length();
	}
}
