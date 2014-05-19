package org.sldc.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sldc.assist.CSQLBuildIns;
import org.sldc.assist.CSQLUtils;
import org.sldc.assist.multitypes.EqualCompareAssist;
import org.sldc.csql.CSQLErrorListener;
import org.sldc.csql.cSQLBaseVisitor;
import org.sldc.csql.cSQLLexer;
import org.sldc.csql.cSQLParser;
import org.sldc.csql.cSQLParser.ExprContext;
import org.sldc.csql.cSQLParser.ExprListContext;
import org.sldc.csql.syntax.Scope;
import org.sldc.exception.DefConflictException;
import org.sldc.exception.DefNotDeclException;
import org.sldc.exception.InvalidType;
import org.sldc.exception.NotBuildInFunction;
import org.sldc.exception.SLDCException;
import org.sldc.exception.SyntaxException;

/**
 * @version 0.5
 * @author Yunfei
 * Syntax parser and executer
 */
public class CSQLExecutable extends cSQLBaseVisitor<Object> {
	
	private static CSQLExecutable _instance = null;
	
	public static CSQLExecutable getSingleInstance(Scope scope){
		if(_instance==null)
			return new CSQLExecutable(scope);
		else
		{
			_instance.setScope(scope);
			return _instance;
		}
	}
	
	private Scope currentScope = null;
	
	public CSQLExecutable(Scope scope)
	{
		setScope(scope);
	}
	
	protected void setScope(Scope newScope) {
		this.currentScope = newScope;
	}
	
	private static InputStream StringToStream(String content)
	{
		return new ByteArrayInputStream(content.getBytes());
	}
	
	public static cSQLParser getWalkTree(InputStream is) throws IOException {
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
	
	public static cSQLParser getWalkTree(String content) throws IOException
	{
		InputStream is = StringToStream(content);
		return getWalkTree(is);
	}
	
	public Object run() throws IOException, SLDCException
	{
		if(this.currentScope==null) throw new InvalidType(new Throwable());
		return visit(this.currentScope.getInput());
	}
	
	public Object visit(@NotNull ParseTree tree) {
		Object result = this.currentScope.getVarValue(tree.getText());
		if(result instanceof SLDCException)
			result = this.currentScope.getVarValue(tree);
		if(result instanceof SLDCException)
			result = super.visit(tree);
		return result;
	}
	
	@Override 
	public Object visitMulDiv(@NotNull cSQLParser.MulDivContext ctx) 
	{
		try
		{
			Object left = visit(ctx.expr(0));
			Object right = visit(ctx.expr(1));
			
			if(CSQLUtils.isInt(left)&&CSQLUtils.isInt(right))
			{
				Long l = CSQLUtils.convertToInt(left);
				Long r = CSQLUtils.convertToInt(right);
				
				return (ctx.op.getText().equals("*"))?l*r:l/r;
			}
			
			if(!CSQLUtils.isNumeric(left)||!CSQLUtils.isNumeric(right))
				return new InvalidType(new Throwable());
			
			Double l = CSQLUtils.convertToDbl(left);
			Double r = CSQLUtils.convertToDbl(right);
			
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
			Object left = visit(ctx.expr(0));
			Object right = visit(ctx.expr(1));
			
			if(ctx.ADDSUB().getText().equals("+")&&(CSQLUtils.isString(left)||CSQLUtils.isString(right)))
			{
				String l = CSQLUtils.removeStringBounds(String.valueOf(left));
				String r = CSQLUtils.removeStringBounds(String.valueOf(right));
				return CSQLUtils.addStringBounds(l+r);
			}else if(ctx.ADDSUB().getText().equals("+")&&CSQLUtils.isChar(left)&&CSQLUtils.isChar(right)){
				return String.valueOf(left)+String.valueOf(right);
			}else if(CSQLUtils.isInt(left)&&CSQLUtils.isInt(right))
			{
				Long l = CSQLUtils.convertToInt(left);
				Long r = CSQLUtils.convertToInt(right);
				
				return (ctx.ADDSUB().getText().equals("+"))?l+r:l-r;
			}
		
			if(!CSQLUtils.isNumeric(left)||!CSQLUtils.isNumeric(right))
				return new InvalidType(new Throwable());
	
			Double l = CSQLUtils.convertToDbl(left);
			Double r = CSQLUtils.convertToDbl(right);
			
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
			int size = ctx.exprList()==null?0:ctx.exprList().expr().size();
			Object[] params = new Object[size];
			for(int i=0;i<size;i++)
			{
				params[i] = visit(ctx.exprList().expr(i));
			}
			
			Object result = CSQLBuildIns.invoke(funcName, params, this.currentScope);
			
			if(result instanceof NotBuildInFunction)
			{
				Scope scope = this.currentScope.getFuncValue(funcName);
				scope.assignFunValues(params); // assign parameter values to formal parameters.
				
				CSQLExecutable runner = new CSQLExecutable(scope);
				result = runner.run();
			}
			return result;
		}catch(SLDCException e){
			return e;
		} catch (IOException e) {
			return e;
		}
	}

	@Override 
	public Object visitStatIf(@NotNull cSQLParser.StatIfContext ctx) {
		Object r = visit(ctx.ifStat().expr());
		//the condition expression is not a boolean. Then return exception
		if(!CSQLUtils.isBool(r)) return new SyntaxException(new Throwable());
		// go into if block
		if((Boolean)r) return visit(ctx.ifStat().stats());
		
		//Check if there are 'else if' statements
		if(ctx.elifStat()!=null)
		{
			int size = ctx.elifStat().size();
			for(int i=0;i<size;i++)
			{
				r = visit(ctx.elifStat(i).expr());
				//the condition expression is not a boolean. Then return exception
				if(!CSQLUtils.isBool(r)) return new SyntaxException(new Throwable());
				// go into if block
				if((Boolean)r) return visit(ctx.elifStat(i).stats());
			}
		}
		
		if(ctx.elseStat()!=null)
		{
			return visit(ctx.elseStat().stats());
		}
		
		return null;
	}
	
	@Override 
	public Object visitForStat(@NotNull cSQLParser.ForStatContext ctx) {
		Object r = visit(ctx.varAssign());
		if(r instanceof SLDCException) return r;
		Object cond = visit(ctx.expr(0));
		if(!CSQLUtils.isBool(cond)) return new SyntaxException(new Throwable());
		while((Boolean)cond)
		{
			r=visit(ctx.stats());
			visit(ctx.expr(1));
			cond = visit(ctx.expr(0));
		}
		return r;
	}
	
	@Override 
	public Object visitWhileStat(@NotNull cSQLParser.WhileStatContext ctx) {
		Object r = visit(ctx.expr());
		if(!CSQLUtils.isBool(r)) return r;
		Boolean cond = (Boolean)r;
		while(cond)
		{
			r=visit(ctx.stats());
			cond = (Boolean)visit(ctx.expr());
		}
		return r;
	}
	
	@Override 
	public Object visitVarAssign(@NotNull cSQLParser.VarAssignContext ctx) {
		// If the variable is defined before, assign value. Otherwise we leave it until it is used.
		Object value = this.currentScope.containVar(ctx.Identifier())?this.currentScope.getVarValue(ctx.Identifier()):null;
		if(ctx.EQU()!=null)
		{
			if(ctx.expr()!=null)
				value = visit(ctx.expr());
			else if(ctx.arrayValues()!=null)
				value = visit(ctx.arrayValues());
			else if(ctx.selectExpr()!=null)
				value = this.currentScope.getVarValue(ctx.selectExpr());	// selectExpr was dealt with in Listerner mode in terms of performance 
			
			try{
				this.currentScope.setVarValue(ctx.Identifier().getText(), value);
			} catch (DefNotDeclException e) {
				try {
					this.currentScope.addVariable(ctx.Identifier().getText(), value);
				} catch (DefConflictException e1) {
					value = e1;
				}
			}
		}
		return value;
	}
	
	@Override 
	public Object visitSelectExpr(@NotNull cSQLParser.SelectExprContext ctx) {
		Boolean where = true;
		if(ctx.condition()!=null)
			where = (Boolean)visit(ctx.condition());
		ArrayList<Object> r = new ArrayList<Object>();
		if(where)
			if(ctx.contents().getText().equals("*"))
				r.add(visit(ctx.contents()));
			else {
				ExprListContext exprs = ctx.contents().exprList();
				for(ExprContext expr : exprs.expr())
				{
					r.add(visit(expr));
				}
			}
		return r;
	}
	
	@Override 
	public Object visitStatReturn(@NotNull cSQLParser.StatReturnContext ctx) 
	{
		return ctx.expr()==null?null:visit(ctx.expr());
	}
	
	@Override 
	public Object visitArrayValues(@NotNull cSQLParser.ArrayValuesContext ctx) {
		ArrayList<Object> result = new ArrayList<Object>();
		for(ExprContext expr : ctx.exprList().expr())
		{
			Object v = visit(expr);
			result.add(v);
		}
		return result;
	}
	
	@Override 
	public Object visitVar(@NotNull cSQLParser.VarContext ctx) 
	{
		return this.currentScope.getVarValue(ctx.getText());
	}
	
	@Override
	public Object visitInt(@NotNull cSQLParser.IntContext ctx) 
	{
		return Long.valueOf(ctx.INT().getText());
	}
	
	@Override 
	public Object visitNum(@NotNull cSQLParser.NumContext ctx) 
	{ 
		return Double.valueOf(ctx.Number().getText());
	}
	
	@Override 
	public Object visitString(@NotNull cSQLParser.StringContext ctx) 
	{ 
		return ctx.String().getText();
	}
	
	@Override 
	public Object visitAnd(@NotNull cSQLParser.AndContext ctx) { 
		Object expr1 = visit(ctx.expr(0));
		Object expr2 = visit(ctx.expr(1));
		
		if(CSQLUtils.isBool(expr1) && CSQLUtils.isBool(expr2))
			return (Boolean)expr1&&(Boolean)expr2;
		else
			return new InvalidType(new Throwable());
	}
	
	@Override 
	public Object visitEqual(@NotNull cSQLParser.EqualContext ctx) {
		Object expr1 = visit(ctx.expr(0));
		Object expr2 = visit(ctx.expr(1));
		
		return EqualCompareAssist.isEqual(expr1, expr2);
	}
	
	@Override 
	public Object visitOr(@NotNull cSQLParser.OrContext ctx) {
		Object expr1 = visit(ctx.expr(0));
		Object expr2 = visit(ctx.expr(1));
		
		if(CSQLUtils.isBool(expr1) && CSQLUtils.isBool(expr2))
			return (Boolean)expr1||(Boolean)expr2;
		else
			return new InvalidType(new Throwable());
	}
	
	@Override 
	public Object visitUnequal(@NotNull cSQLParser.UnequalContext ctx) {
		Object expr1 = visit(ctx.expr(0));
		Object expr2 = visit(ctx.expr(1));
		
		return !EqualCompareAssist.isEqual(expr1, expr2);
	}
	
	@Override 
	public Object visitNot(@NotNull cSQLParser.NotContext ctx) {
		Object r = visit(ctx.expr()); 
		if(!CSQLUtils.isBool(r)) return new SyntaxException(new Throwable());
		Boolean b = (Boolean)r;
		return !b;
	}
	
	@Override 
	public Object visitGreaterEqual(@NotNull cSQLParser.GreaterEqualContext ctx) {
		Object r0 = visit(ctx.expr(0));
		Object r1 = visit(ctx.expr(1));
		
		try {
			if(CSQLUtils.isInt(r0)&&CSQLUtils.isInt(r1))
			{
				Long d0 = CSQLUtils.convertToInt(r0);
				Long d1 = CSQLUtils.convertToInt(r1);
				return d0>=d1;
			}
			
			Double d0 = CSQLUtils.convertToDbl(r0);
			Double d1 = CSQLUtils.convertToDbl(r1);
			return d0>=d1;
		} catch (InvalidType e) {
			return new InvalidType(new Throwable());
		}
	}
	
	@Override 
	public Object visitGreater(@NotNull cSQLParser.GreaterContext ctx) {
		Object r0 = visit(ctx.expr(0));
		Object r1 = visit(ctx.expr(1));
		
		try {
			if(CSQLUtils.isInt(r0)&&CSQLUtils.isInt(r1))
			{
				Long d0 = CSQLUtils.convertToInt(r0);
				Long d1 = CSQLUtils.convertToInt(r1);
				return d0>d1;
			}
			
			Double d0 = CSQLUtils.convertToDbl(r0);
			Double d1 = CSQLUtils.convertToDbl(r1);
			return d0>d1;
		} catch (InvalidType e) {
			return new InvalidType(new Throwable());
		}
	}
	
	@Override 
	public Object visitLower(@NotNull cSQLParser.LowerContext ctx) {
		Object r0 = visit(ctx.expr(0));
		Object r1 = visit(ctx.expr(1));
		
		try {
			if(CSQLUtils.isInt(r0)&&CSQLUtils.isInt(r1))
			{
				Long d0 = CSQLUtils.convertToInt(r0);
				Long d1 = CSQLUtils.convertToInt(r1);
				return d0<d1;
			}
			
			Double d0 = CSQLUtils.convertToDbl(r0);
			Double d1 = CSQLUtils.convertToDbl(r1);
			return d0<d1;
		} catch (InvalidType e) {
			return new InvalidType(new Throwable());
		}
	}
	
	@Override 
	public Object visitLowerEqual(@NotNull cSQLParser.LowerEqualContext ctx) {
		Object r0 = visit(ctx.expr(0));
		Object r1 = visit(ctx.expr(1));
		
		try {
			if(CSQLUtils.isInt(r0)&&CSQLUtils.isInt(r1))
			{
				Long d0 = CSQLUtils.convertToInt(r0);
				Long d1 = CSQLUtils.convertToInt(r1);
				return d0<=d1;
			}
			
			Double d0 = CSQLUtils.convertToDbl(r0);
			Double d1 = CSQLUtils.convertToDbl(r1);
			return d0<=d1;
		} catch (InvalidType e) {
			return new InvalidType(new Throwable());
		} 
	}
	
	@Override 
	public Object visitArray(@NotNull cSQLParser.ArrayContext ctx) {
		Object id = visit(ctx.expr(0));
		Object idx = visit(ctx.expr(1));
		
		return CSQLUtils.fetchArray(id, idx); 
	}
}
