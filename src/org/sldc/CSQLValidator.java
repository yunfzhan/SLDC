package org.sldc;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.sldc.csql.cSQLBaseListener;
import org.sldc.csql.cSQLParser;
import org.sldc.csql.cSQLParser.FundeclContext;
import org.sldc.csql.syntax.Scope;
import org.sldc.exception.DefConflictException;
import org.sldc.exception.DefNotDeclException;
import org.sldc.exception.IRuntimeError;
import org.sldc.exception.InvalidFormat;
import org.sldc.exception.SLDCException;

public class CSQLValidator extends cSQLBaseListener implements IRuntimeError {
	private Scope currentScope = new Scope();
	private List<SLDCException> exceptions = new ArrayList<SLDCException>();
	
	private String codelines = null;
	
	public CSQLValidator(String lines)
	{
		this.codelines = lines;
		this.currentScope.setInput(this.codelines);
	}
	
	public Scope getScope()
	{
		return this.currentScope;
	}
	
	public List<SLDCException> getErrors()
	{
		return this.exceptions;
	}
	
	@Override 
	public void enterVarDecl(@NotNull cSQLParser.VarDeclContext ctx) 
	{
		List<cSQLParser.VarAssignContext> list = ctx.varAssign();
		for(int i=0;i<list.size();i++)
		{
			cSQLParser.VarAssignContext vac = list.get(i);
			TerminalNode id = vac.Identifier();
			try {
				this.currentScope.addVariables(id.getText(), null);
			} catch (SLDCException e) {
				this.exceptions.add(e);
			}
		}
	}
	
	@Override 
	public void exitVarAssign(@NotNull cSQLParser.VarAssignContext ctx) {
		if(ctx.EQU()!=null)
		{
			CSQLExecutable runner = new CSQLExecutable(this.currentScope);
			Object value = runner.visit(ctx.expr());
			try {
				this.currentScope.setVarValue(ctx.Identifier().getText(), value);
			} catch (DefNotDeclException e) {
				this.exceptions.add(e);
			}
		}
	}
	
	@Override 
	public void enterStats(@NotNull cSQLParser.StatsContext ctx) {
		String text = this.codelines.substring(ctx.start.getStartIndex(), ctx.stop.getStopIndex());
		this.currentScope.setInput(text);
	}
	
	@Override 
	public void enterBlock(@NotNull cSQLParser.BlockContext ctx) {
		try {
			Scope scope = null;
			if(ctx.parent instanceof cSQLParser.FundeclContext)
			{
				cSQLParser.FundeclContext parent = (FundeclContext) ctx.parent;
				String funcName = parent.Identifier().getText();
				scope = this.currentScope.addFunction(funcName);
				int size = parent.funcParms().Identifier().size();
				for(int i=0;i<size;i++)
				{
					String param = parent.funcParms().Identifier(i).getText();
					scope.addVariables(param, null);
				}
			}
			else
			{
				scope = this.currentScope.addAnonymous();
			}
			this.currentScope = scope;
		} catch (DefConflictException e) {
			this.exceptions.add(e);
		}
	}
	
	@Override 
	public void exitBlock(@NotNull cSQLParser.BlockContext ctx) { 
		this.currentScope = this.currentScope.getUpperScope();
	}
	
	private boolean formatCheck(String regex, String matchstr){
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(matchstr);
		if(!matcher.matches())
		{
			this.exceptions.add(new InvalidFormat());
			return false;
		}
		return true;
	}
	
	@Override 
	public void enterHttp(@NotNull cSQLParser.HttpContext ctx) {
		String regex = "^http://[_-#$%0-9A-Za-z]+[_-#$%0-9A-Za-z.]*";
		formatCheck(regex, ctx.getText());
	}
	
	@Override 
	public void enterFtp(@NotNull cSQLParser.FtpContext ctx) {
		String regex = "";
		formatCheck(regex, ctx.getText());
	}
	
	@Override 
	public void enterFile(@NotNull cSQLParser.FileContext ctx) {
		String regex = "";
		formatCheck(regex, ctx.getText());
	}
	
	@Override 
	public void enterDatabase(@NotNull cSQLParser.DatabaseContext ctx) {
		String regex = "";
		formatCheck(regex, ctx.getText());
	}
	
	@Override 
	public void exitProtocols(@NotNull cSQLParser.ProtocolsContext ctx) {
		if(ctx.Identifier() != null){
			try {
				this.currentScope.addAlias(ctx.Identifier().getText(), ctx.protocol().getText());
			} catch (DefConflictException e) {
				this.exceptions.add(e);
			}
		}
	}

}
