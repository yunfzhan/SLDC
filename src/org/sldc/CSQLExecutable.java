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
import org.sldc.exception.NotBuildInFunction;
import org.sldc.exception.SLDCException;


public class CSQLExecutable extends cSQLBaseVisitor<Object> {
	
	private Scope currentScope = null;
	
	public CSQLExecutable(Scope scope)
	{
		this.currentScope = scope;
	}
	
	private static InputStream StringToStream(String content)
	{
		return new ByteArrayInputStream(content.getBytes());
	}
	
	public static cSQLParser getWalkTree(String content) throws IOException
	{
		InputStream is = StringToStream(content);
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
		return parser;
	}
	
	/*
	 * Evaluate if it's a variable or expression.
	 * 
	 */
	private Object getVarOrExpr(ParseTree expr)
	{
		String name = expr.getText();
		Object result = this.currentScope.getVarValue(name);
		if(result instanceof SLDCException)
			result = visit(expr);
		return result;
	}
	
	public Object execScope() throws IOException, SLDCException
	{
		if(this.currentScope==null) throw new InvalidType();
		return visit(this.currentScope.getInput());
	}
	
	@Override 
	public Object visitMulDiv(@NotNull cSQLParser.MulDivContext ctx) 
	{
		try
		{
			Object left = getVarOrExpr(ctx.expr(0));
			Object right = getVarOrExpr(ctx.expr(1));
			
			if(!CSQLBuildIns.isNumeric(left)||!CSQLBuildIns.isNumeric(right))
				return new InvalidType();
			
			Double l = CSQLBuildIns.convertToDbl(left);
			Double r = CSQLBuildIns.convertToDbl(right);
			
			return (ctx.op.getText().equals("*"))?l*r:l/r;
		}catch(InvalidType e)
		{
			return e;
		}
	}
	
	@Override 
	public Object visitAddSub(@NotNull cSQLParser.AddSubContext ctx) 
	{
		try
		{
			Object left = getVarOrExpr(ctx.expr(0));
			Object right = getVarOrExpr(ctx.expr(1));
			
			if(!CSQLBuildIns.isNumeric(left)||!CSQLBuildIns.isNumeric(right))
				return new InvalidType();
	
			Double l = CSQLBuildIns.convertToDbl(left);
			Double r = CSQLBuildIns.convertToDbl(right);
			
			return (ctx.ADDSUB().getText().equals("+"))?l+r:l-r;
		}catch(InvalidType e)
		{
			return e;
		}
	}
	
	@Override 
	public Object visitFunc(@NotNull cSQLParser.FuncContext ctx) {
		String funcName = ctx.Identifier().getText();
		try{
			int size = ctx.exprList().expr().size();
			Object[] params = new Object[size];
			for(int i=0;i<size;i++)
			{
				params[i] = getVarOrExpr(ctx.exprList().expr(i));
			}
			
			Object result = CSQLBuildIns.invoke(funcName, params);
			
			if(result instanceof NotBuildInFunction)
			{
				Scope scope = this.currentScope.getFuncValue(funcName);
				for(int i=0;i<size;i++)
				{
					String varName = ctx.exprList().expr(i).getText();
					scope.setVarValue(varName, params[i]);
				}
				
				CSQLExecutable runner = new CSQLExecutable(scope);
				return runner.execScope();
			}
			else
				return result;
		}catch(DefNotDeclException e){
			return e;
		} catch (IOException e) {
			return e;
		} catch (SLDCException e) {
			return e;
		}
	}
	
	@Override 
	public Object visitSelectExpr(@NotNull cSQLParser.SelectExprContext ctx) {
		Scope scope = this.currentScope.getAnonymous(ctx);
		this.currentScope = scope;
		Object r = visitChildren(ctx);
		this.currentScope = scope.getUpperScope();
		return r;
	}
	
	@Override 
	public Object visitStatReturn(@NotNull cSQLParser.StatReturnContext ctx) 
	{
		Object result = null;
		if(ctx.expr() != null)
		{
			result = getVarOrExpr(ctx.expr());
		}
		return result;
	}
	
	@Override 
	public Object visitVar(@NotNull cSQLParser.VarContext ctx) 
	{
		String Id = ctx.getText();
		Object value = this.currentScope.getVarValue(Id);
		return value;
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
	
//	@Override 
//	public Object visitProtocols(@NotNull cSQLParser.ProtocolsContext ctx) {
//		try {
//			if(ctx.Identifier()!=null)
//				this.baseScope.addAlias(ctx.Identifier().getText(), ctx.protocol().getText());
//			return ctx.protocol().getText();
//		} catch (DefConflictException e) {
//			return e;
//		}
//	}
}
