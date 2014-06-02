package org.sldc.protocols.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class HttpRequestHelper {
	/**
     * Connection timeout
     */
    private int connectTimeOut = 5000;

    /**
     * Reading timeout
     */
    private int readTimeOut = 10000;

    /**
     * Request encoding
     */
    private String requestEncoding = "utf8";
    
    private File responseTo = null;
    private Map<String, String> header = null;
    
    HttpRequestHelper(File f, Map<String, String> header) {
    	responseTo = f;
    	this.header = header;
    }
    
    public int getConnectTimeOut()
    {
        return connectTimeOut;
    }

    public int getReadTimeOut()
    {
        return readTimeOut;
    }

    public String getRequestEncoding()
    {
        return requestEncoding;
    }

    public void setConnectTimeOut(int connectTimeOut)
    {
        this.connectTimeOut = connectTimeOut;
    }

    public void setReadTimeOut(int readTimeOut)
    {
        this.readTimeOut = readTimeOut;
    }

    public void setRequestEncoding(String requestEncoding)
    {
        this.requestEncoding = requestEncoding;
    }
    
    public boolean isSaveFile() {
    	return this.responseTo!=null;
    }
    
    public String doGet(String reqUrl) throws MalformedURLException, IOException {
    	return doGet(reqUrl, getRequestEncoding());
    }

    public String doGet(String reqUrl, String recvEncoding) throws MalformedURLException, IOException
    {
        HttpURLConnection url_con = null;
        String responseContent = null;
        try
        {
            StringBuilder params = new StringBuilder();
            String queryUrl = reqUrl;
            
            int paramIndex = reqUrl.indexOf("?");
            if (paramIndex > 0)
            {
                queryUrl = reqUrl.substring(0, paramIndex);
                String parameters = reqUrl.substring(paramIndex + 1, reqUrl.length());
                String[] paramArray = parameters.split("&");
                for (String string : paramArray)
                {
                    int index = string.indexOf("=");
                    if (index > 0)
                    {
                        String parameter = string.substring(0, index);
                        String value = string.substring(index + 1, string.length());
                        params.append(parameter).append("=").append(URLEncoder.encode(value,getRequestEncoding())).append("&");
                    }
                }
            }

            url_con = initConnection(queryUrl,"GET");
            writeOutputStream(url_con, params.toString());
            responseContent = recvResponse(url_con, recvEncoding);
        }
        finally
        {
            if (url_con != null)
            {
                url_con.disconnect();
            }
        }

        return responseContent;
    }

    private HttpURLConnection initConnection(String reqUrl, String method) throws MalformedURLException, IOException {
    	String proxyHost = System.getProperty("http.proxyHost");
    	String proxyPort = System.getProperty("http.proxyPort");
    	HttpURLConnection conn = null;
    	if(proxyHost!=null) {
	    	Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort==null?80:Integer.valueOf(proxyPort)));
	    	conn = (HttpURLConnection) (new URL(reqUrl)).openConnection(proxy);
    	}else
    		conn = (HttpURLConnection) (new URL(reqUrl)).openConnection();
    	for(String key : header.keySet())
    		conn.setRequestProperty(key, header.get(key));
        conn.setRequestMethod(method);
		conn.setConnectTimeout(getConnectTimeOut());
		conn.setReadTimeout(getReadTimeOut());
		conn.setUseCaches(false);
		conn.setDoOutput(true);
		return conn;
    }
    
    private void writeOutputStream(HttpURLConnection conn, String params) throws IOException {
    	if(params.endsWith("&")) params = params.substring(0,params.length()-1);
    	
    	byte[] bytes = params.getBytes();
    	conn.setRequestProperty("Content-Length", String.valueOf(bytes.length));
        conn.getOutputStream().write(bytes, 0, bytes.length);
		conn.getOutputStream().flush();
		conn.getOutputStream().close();
    }
    
    private void writeOutputStream(HttpURLConnection conn, Map<String, String> parameters) throws IOException {
    	StringBuilder params = new StringBuilder();
        for (Iterator<Entry<String, String>> iter = parameters.entrySet().iterator(); iter.hasNext();)
        {
            Entry<String, String> element = (Entry<String, String>) iter.next();
            params.append(element.getKey()).append("=").append(URLEncoder.encode(element.getValue(),getRequestEncoding())).append("&");
        }

        writeOutputStream(conn, params.toString());        
    }
    
    private String recvResponse(HttpURLConnection conn, String recvEncoding) throws IOException {
    	InputStream in = conn.getInputStream();
    	// TODO cope with encoding
		BufferedReader br = new BufferedReader(new InputStreamReader(in,recvEncoding));
		
		try{
			String tempLine = null;
			String crlf=System.getProperty("line.separator");
			if(!isSaveFile())
			{
				StringBuilder temp = new StringBuilder();
		        while ((tempLine = br.readLine()) != null)
		        {
		            temp.append(tempLine).append(crlf);
		        }
		        return temp.toString();
			}else{
				FileWriter fw = new FileWriter(this.responseTo);
				while ((tempLine = br.readLine()) != null)
		        {
		            fw.write(tempLine);
		            fw.write(crlf);
		        }
				fw.close();
				return null;
			}
		}finally{
	        br.close();
	        in.close();
		}
    }
    
    public String doGet(String reqUrl, Map<String, String> parameters) throws MalformedURLException, IOException {
    	return doGet(reqUrl, parameters, getRequestEncoding());
    }
    
    public String doGet(String reqUrl, Map<String, String> parameters,String recvEncoding) throws MalformedURLException, IOException
    {
        HttpURLConnection url_con = null;
        String responseContent = null;
        try
        {
            url_con = initConnection(reqUrl, "GET");
			writeOutputStream(url_con, parameters);			
			responseContent = recvResponse(url_con, recvEncoding);
        }
        finally
        {
            if (url_con != null)
            {
                url_con.disconnect();
            }
        }

        return responseContent;
    }
    
    public String doPost(String reqUrl, Map<String, String> parameters) throws IOException {
    	return doPost(reqUrl,parameters,getRequestEncoding());
    }
    
    public String doPost(String reqUrl, String params) throws IOException {
    	return doPost(reqUrl, params, getRequestEncoding());
    }
    
    public String doPost(String reqUrl, String params, String recvEncoding) throws MalformedURLException, IOException {
    	HttpURLConnection url_con = null;
        String responseContent = null;
        try
        {
            url_con = initConnection(reqUrl, "POST");
            writeOutputStream(url_con, params);
            responseContent = recvResponse(url_con, recvEncoding);
        }
        finally
        {
            if (url_con != null)
            {
                url_con.disconnect();
            }
        }
        return responseContent;
    }
    
    public String doPost(String reqUrl, Map<String, String> parameters, String recvEncoding) throws IOException
    {
        HttpURLConnection url_con = null;
        String responseContent = null;
        try
        {
            url_con = initConnection(reqUrl, "POST");
            writeOutputStream(url_con, parameters);
            responseContent = recvResponse(url_con, recvEncoding);
        }
        finally
        {
            if (url_con != null)
            {
                url_con.disconnect();
            }
        }
        return responseContent;
    }
}
