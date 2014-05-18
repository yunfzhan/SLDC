package org.sldc;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.sldc.assist.CSQLProtocolFactoryImpl;
import org.sldc.assist.CSQLUtils;
import org.sldc.core.CSQLExecutable;
import org.sldc.csql.cSQLParser;
import org.sldc.exception.SLDCException;
/**
 * 
 * @author Yunfei Zhang
 * TODO 1.$ function is going to add a condition parameter to filter result further. e.g. $(a,'tr','t', $line['td']!="")
 * 		2.Add a Loop function to enable loop operation in one sentence.
 * 		3.Add a judgment function to judge a condition in one sentence.
 * 		4.FTP and database support.
 * 		5.Protocol format check using regular expression.
 * 		6.Unicode support for extensions
 * 		7.Cope with 'where' clause
 *		8.Multi-threads support
 *		9.'with' clause support
 *		10.'order' and 'group' clauses support in select
 */
public final class CSQLMain {
	
//	public static String getStreamContent(
//			   InputStream is,
//			   String          encoding ) throws IOException
//	{
//		BufferedReader br = new BufferedReader( new InputStreamReader(is, encoding ));
//		StringBuilder sb = new StringBuilder();
//		String line;
//		while(( line = br.readLine()) != null ) {
//			sb.append( line );
//			sb.append( '\n' );
//		}
//		return sb.toString();
//	}
	
	public static void unittest() {
		String text = "This is a example of an sample tet for SQL-Like core test module tet unit test.";
		String pattern = "tet";
		ArrayList<Integer> found = CSQLUtils.BoyerMoore(pattern, text);
		if(found.size()!=0)
		{
			for(int i=0;i<found.size();i++)
				System.out.println(found.get(i));
		}
	}
	
	public static void main(String[] args) throws IOException {
//		unittest();
		String inputfile = null;
		if(args.length>0) inputfile = args[0];
		
		InputStream is = System.in;
		if(inputfile!=null) is = new FileInputStream(inputfile);
		
		cSQLParser parser = CSQLExecutable.getWalkTree(is);
		ParseTree tree = parser.program();
		// create a generic parse tree walker that can trigger callbacks
		ParseTreeWalker walker = new ParseTreeWalker();
		// walk the tree created during the parse, trigger callbacks
		CSQLValidator validator = new CSQLValidator(tree, new CSQLProtocolFactoryImpl());
		walker.walk(validator, tree);
		
		List<SLDCException> exs = validator.getErrors();
		if(exs.size()!=0)
		{
			for(SLDCException ex : exs)
			{
				System.err.println(ex.getMessage());
				ex.printStackTrace();
				System.err.println();
			}
		}
		else
		{
			CSQLExecutable runner = new CSQLExecutable(validator.getScope());
			Object r = runner.visit(tree);
			if(r instanceof SLDCException)
				((SLDCException)r).printStackTrace();
			else if(r!=null)
				System.out.println(r);
		}
	}

}
