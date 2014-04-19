package org.sldc.protocols;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.sldc.CSQLProtocol;
import org.sldc.exception.ProtocolException;

public class CSQLHTTPProtocol implements CSQLProtocol {

	private String address = null;
	
	public CSQLHTTPProtocol(String addr) {
		this.address = addr;
	}
	
	@Override
	public String Retrieve() throws ProtocolException {
		InputStream is = null;
		try {
			URL url = new URL(this.address);
			is = url.openStream();
			BufferedReader br=new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			String line;
			while((line=br.readLine())!=null) 
				sb.append(line);
			
			is.close();
			return sb.toString();
		} catch (MalformedURLException e) {
			throw new ProtocolException(e);
		} catch (IOException e) {
			throw new ProtocolException(e);
		}
	}

}
