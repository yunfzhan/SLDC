package org.sldc;

import java.io.IOException;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.sldc.csql.cSQLLexer;
import org.sldc.csql.cSQLParser;

public final class CSQLMain {

	public static void main(String[] args) throws IOException {
		// create a stream that reads from file
		ANTLRInputStream input = new ANTLRInputStream(System.in);
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
		walker.walk(new SQLListener(), tree);
		System.out.println();
	}

}
