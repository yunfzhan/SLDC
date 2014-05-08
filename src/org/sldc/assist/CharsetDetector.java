package org.sldc.assist;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;
import org.mozilla.intl.chardet.nsPSMDetector;

public class CharsetDetector {
	private boolean found = false;
	private String result = "";
	
	public String[] detectAllCharsets(InputStream in) throws IOException {	
		int lang = nsPSMDetector.ALL;
		String[] prob;
		// Initalize the nsDetector() ;
		nsDetector detector = new nsDetector(lang);
		// Set an observer...
		// The Notify() will be called when a matching charset is found.

		detector.Init(new nsICharsetDetectionObserver(){
			public void Notify(String charset)
			{
				found = true;
				result = charset;
			}
		});
		
		BufferedInputStream imp = new BufferedInputStream(in);
		byte[] buf = new byte[1024];
		int len;
		boolean isAscii = true;
		while ((len = imp.read(buf, 0, buf.length)) != -1)
		{
			// Check if the stream is only ascii.
			if (isAscii)
				isAscii = detector.isAscii(buf, len);
			// DoIt if non-ascii and not done yet.
			if (!isAscii)
			{
				if (detector.DoIt(buf, len, false))
				break;
			}
		}
		imp.close();
		in.close();
		detector.DataEnd();
		
		if (isAscii)
		{
			prob = new String[]
			{
				"ASCII"
			};
		} else if (found)
		{
			prob = new String[]
			{
				result
			};
		} else
		{
			prob = detector.getProbableCharsets();
		}
		return prob;
	}
}
