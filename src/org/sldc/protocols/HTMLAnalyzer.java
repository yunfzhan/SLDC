package org.sldc.protocols;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.parser.ParserDelegator;

public class HTMLAnalyzer extends ParserCallback {
	
	private static HTMLAnalyzer startAnalyze(Reader r, String tag) throws IOException {
		ParserDelegator ps = new ParserDelegator();
		ParserCallback pc = new HTMLAnalyzer(tag);
		
		ps.parse(r, pc, true);
		return (HTMLAnalyzer) pc;
	}
	
	public static String[] startAnalyze(String contents, String tag) throws IOException {
		StringReader reader = new StringReader(contents);
		HTMLAnalyzer pa = startAnalyze(reader,tag);
		int size = pa.tagpos.size();
		String[] res = new String[size];
		for(int i=0;i<size;i++)
		{
			ArrayList<Integer> arr = pa.tagpos.get(i);
			res[i] = contents.substring(arr.get(0),arr.get(1)+1);
			//System.out.println("Index: "+i+","+res[i]);
		}
		return res;
	}
	
	public static String[] startAnalyze(File f, String tag) throws IOException {
		FileReader reader = new FileReader(f);
		HTMLAnalyzer pa = startAnalyze(reader, tag);
		int size = pa.tagpos.size();
		RandomAccessFile raf = new RandomAccessFile(f,"r");
		String[] res = new String[size];
		for(int i=0;i<size;i++)
		{
			ArrayList<Integer> arr = pa.tagpos.get(i);
			int beg = arr.get(0);
			int end = arr.get(1);
			byte[] buff = new byte[end-beg+1];
			raf.seek(beg);
			raf.read(buff);
			res[i] = new String(buff/*,"utf8"*/);
		}
		raf.close();
		return res;
	}
	
	private String tag = null;
	ArrayList<ArrayList<Integer>> tagpos = new ArrayList<ArrayList<Integer>>();
	private Stack<ArrayList<Integer>> stack = new Stack<ArrayList<Integer>>();
	
	public HTMLAnalyzer(String tag) {
		this.tag = tag;
	}
	
	public void handleText(char[] data, int pos) {
		
	}
	
	public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
		if(this.tag.equalsIgnoreCase(t.toString()))
		{
			ArrayList<Integer> arr = new ArrayList<Integer>();
			arr.add(pos);
			stack.push(arr);
		}
	}
	
	public void handleError(String errorMsg, int pos) {
		
	}
	
	public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, int pos) {
		
	}
	
	public void handleComment(char[] data, int pos) {
		
	}
	
	public void handleEndTag(HTML.Tag t, int pos) {
		String endtag = t.toString();
		if(this.tag.equalsIgnoreCase(endtag))
		{
			ArrayList<Integer> arr = stack.pop();
			arr.add(pos+endtag.length()+2);
			tagpos.add(arr);
		}
	}
}
