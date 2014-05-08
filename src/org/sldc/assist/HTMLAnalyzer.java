package org.sldc.assist;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HTMLAnalyzer {
	public static ArrayList<String> startAnalyze(String contents, String tag) throws IOException {
		Document doc = Jsoup.parse(contents);
		Elements elems = doc.getElementsByTag(tag);
		ArrayList<String> res = new ArrayList<String>();
		for(int i=0;i<elems.size();i++)
			res.add(elems.get(i).toString());
		return res;
	}
	
	public static ArrayList<String> startAnalyze(File f, String tag) throws IOException {
		CharsetDetector detector = new CharsetDetector();
		String[] probs = detector.detectAllCharsets(new FileInputStream(f));
		ArrayList<String> res = new ArrayList<String>();
		if(probs.length>0)
		{
			Document doc = Jsoup.parse(f,probs[0]);
			Elements elems = doc.getElementsByTag(tag);
			int size = elems.size();
			for(int i=0;i<size;i++)
			{
				//System.out.println(elems.get(i));
				Element elem = elems.get(i);
				res.add(elem.toString());
			}
		}
		return res;
	}
}
