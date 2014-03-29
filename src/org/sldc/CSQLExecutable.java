package org.sldc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sldc.csql.CSQLErrorListener;
import org.sldc.csql.cSQLBaseVisitor;
import org.sldc.csql.cSQLLexer;
import org.sldc.csql.cSQLParser;
import org.sldc.csql.syntax.Scope;
import org.sldc.exception.DefNotDeclException;
import org.sldc.exception.InvalidType;
import org.sldc.exception.SLDCException;

public class CSQLExecutable extends cSQLBaseVisitor<Object> {
	
	private Scope baseScope = null;
	
	public CSQLExecutable(Scope scope)
	{
		this.baseScope = scope;
	}
	
	public static ParseTree getWalkTree(InputStream is) throws IOException
	{
		// create a stream that reads from file
		ANTLRInputStream input = new ANTLRInputStream(is);
		// create a lexer that feeds off of input
		cSQLLexer lexer = new cSQLLexer(input);
		// create a buffer of tokens pulled from the lexer
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		// create a parser that feeds off the tokens buffer
		cSQLParser parser = new cSQLParser(tokens);
		parser.removeErrorListeners();
		parser.addErrorListener(new CSQLErrorListener());
		// begin with the selectExpr rule
		return parser.program();
	}
	
	public Object run() throws IOException, SLDCException
	{
		if(this.baseScope==null) throw new InvalidType();
		InputStream in = new ByteArrayInputStream(this.baseScope.getInput().getBytes());
		ParseTree tree = getWalkTree(in);
		return visit(tree);		
	}
	
	private boolean isNumber(Object obj)
	{
		return (obj instanceof Integer) || (obj instanceof Float) || (obj instanceof Double);
	}
	
	@Override 
	public Object visitMulDiv(@NotNull cSQLParser.MulDivContext ctx) 
	{
		Object left = visit(ctx.expr(0));
		Object right = visit(ctx.expr(1));
		
		if(!isNumber(left)||!isNumber(right))
			return new InvalidType();
		
		if(ctx.MULDIV().getText().equals("*")) return (Double)left*(Double)right;
		else return (Double)left/(Double)right;
	}
	
	@Override 
	public Object visitAddSub(@NotNull cSQLParser.AddSubContext ctx) 
	{
		Object left = visit(ctx.expr(0));
		Object right = visit(ctx.expr(1));
		
		if(!isNumber(left)||!isNumber(right))
			return new InvalidType();
		
		if(ctx.ADDSUB().getText().equals("+")) return (Double)left+(Double)right;
		else return (Double)left-(Double)right;
	}
	
	@Override 
	public Object visitVar(@NotNull cSQLParser.VarContext ctx) 
	{
		String Id = ctx.getText();
		try {
			Object value = this.baseScope.getVarValue(Id);
			return value;
		} catch (DefNotDeclException e) {
			return e;
		}
	}
	
	@Override
	public Object visitInt(@NotNull cSQLParser.IntContext ctx) 
	{
		return ctx.INT().getText();
	}
	
	@Override 
	public Object visitNum(@NotNull cSQLParser.NumContext ctx) 
	{ 
		return ctx.Number().getText();
	}
	
	@Override 
	public Object visitString(@NotNull cSQLParser.StringContext ctx) 
	{ 
		return ctx.String().getText(); 
	}
}
