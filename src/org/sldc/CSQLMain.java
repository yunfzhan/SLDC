package org.sldc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.sldc.csql.cSQLParser;
import org.sldc.csql.syntax.Scope;
import org.sldc.exception.SLDCException;

public final class CSQLMain {
	
	public static String getStreamContent(
			   InputStream is,
			   String          encoding ) throws IOException
	{
		BufferedReader br = new BufferedReader( new InputStreamReader(is, encoding ));
		StringBuilder sb = new StringBuilder();
		String line;
		while(( line = br.readLine()) != null ) {
			sb.append( line );
			sb.append( '\n' );
		}
		return sb.toString();
	}
	
	public static void main(String[] args) throws IOException {
		String inputfile = null;
		if(args.length>0) inputfile = args[0];
		
		InputStream is = System.in;
		if(inputfile!=null) is = new FileInputStream(inputfile);
		
		String text = getStreamContent(is, "utf8");
		cSQLParser parser = CSQLExecutable.getWalkTree(text);
		ParseTree tree = parser.program();
		// create a generic parse tree walker that can trigger callbacks
		ParseTreeWalker walker = new ParseTreeWalker();
		// walk the tree created during the parse, trigger callbacks
		CSQLValidator validator = new CSQLValidator(tree, new CSQLProtocolFactoryImpl());
		walker.walk(validator, tree);
		
		List<SLDCException> exs = validator.getErrors();
		if(exs.size()!=0)
		{
			for(int i=0;i<exs.size();i++)
				System.err.println(exs.get(i).getMessage());
		}
		else
		{
			Scope currentScope = validator.getScope();
			CSQLExecutable runner = new CSQLExecutable(currentScope);
			runner.visit(tree);
		}
		System.out.println();
	}

}
