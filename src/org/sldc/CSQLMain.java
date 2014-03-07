package org.sldc;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.sldc.csql.cSQLLexer;
import org.sldc.csql.cSQLParser;

public final class CSQLMain {

	public static void main(String[] args) throws IOException {
		String inputfile = null;
		if(args.length>0) inputfile = args[0];
		InputStream is = System.in;
		if(inputfile!=null) is = new FileInputStream(inputfile);
		
		// create a stream that reads from file
		ANTLRInputStream input = new ANTLRInputStream(is);
		// create a lexer that feeds off of input
		cSQLLexer lexer = new cSQLLexer(input);
		// create a buffer of tokens pulled from the lexer
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		// create a parser that feeds off the tokens buffer
		cSQLParser parser = new cSQLParser(tokens);
		// begin with the selectExpr rule
		ParseTree tree = parser.selectExpr();
		// create a generic parse tree walker that can trigger callbacks
		ParseTreeWalker walker = new ParseTreeWalker();
		// walk the tree created during the parse, trigger callbacks
		walker.walk(new CSQLListener(), tree);

		System.out.println();
	}

}
