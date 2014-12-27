package org.sldc.assist;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sldc.exception.NotSupportedOperation;

public class HTMLAnalyzer implements IConstants {
	/**
	 * Jsoup does grammar check. So incomplete tags such as 'td','tbody' will get errors. This function complements incomplete tags.
	 * @param contents
	 * @return
	 */
	private static String amendBodyDrop(String contents) {
		String content = contents.trim();
		if(content.startsWith("<")){
			String s = content.substring(1).trim();
			if(s.startsWith("tr")||s.startsWith("td")||s.startsWith("tbody")||s.startsWith("th")||s.startsWith("thead")||s.startsWith("tfoot")){
				return "<table>"+contents+"</table>";
			}
		}
		return contents;
	}
	
	private static Object getByIndicator(Document doc, String elem, int indicator) throws NotSupportedOperation {
		switch(indicator) {
		case HTML_TAG:
			return doc.getElementsByTag(elem);
		case HTML_ATTR:
			return doc.getElementsByAttribute(elem);
		case HTML_INNER_TEXT:
			return doc.text();
		case HTML_INNER_HTML:
			return doc.html();
		default:
			throw new NotSupportedOperation(new Throwable());
		}
	}
	
	public static ArrayList<String> startAnalyze(String contents, String element, int indicator) throws IOException, NotSupportedOperation {
		String fragment = amendBodyDrop(contents);
		Document doc = Jsoup.parse(fragment);
		Object o = getByIndicator(doc,element,indicator);
		ArrayList<String> res = new ArrayList<String>();
		if(o instanceof Elements){
			Elements elems = (Elements)o;
			
			for(Element elem : elems)
				res.add(elem.toString());
		}else if(CSQLUtils.isString(o)){
			res.add((String)o);
		}
		return res;
	}
	
	public static ArrayList<String> startAnalyze(File f, String element, int indicator) throws IOException, NotSupportedOperation {
		CharsetDetector detector = new CharsetDetector();
		String[] probs = detector.detectAllCharsets(new FileInputStream(f));
		ArrayList<String> res = new ArrayList<String>();
		if(probs.length>0)
		{
			Document doc = Jsoup.parse(f,probs[0]);
			Object o = getByIndicator(doc,element,indicator);
			if(o instanceof Elements)
			{
				Elements elems = (Elements)o;
				for(Element elem : elems)
				{
					//System.out.println(elems.get(i));
					res.add(elem.toString());
				}
			}else if(CSQLUtils.isString(o)){
				res.add((String)o);
			}
		}
		return res;
	}
}
