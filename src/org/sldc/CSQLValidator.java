package org.sldc;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sldc.csql.cSQLBaseListener;
import org.sldc.csql.cSQLParser;
import org.sldc.csql.cSQLParser.FundeclContext;
import org.sldc.csql.syntax.Scope;
import org.sldc.exception.DefConflictException;
import org.sldc.exception.DefNotDeclException;
import org.sldc.exception.IRuntimeError;
import org.sldc.exception.SLDCException;

public class CSQLValidator extends cSQLBaseListener implements IRuntimeError {
	private Scope currentScope = new Scope();
	private List<SLDCException> exceptions = new ArrayList<SLDCException>();
	private CSQLProtocolFactory _pFactory = null;
	public CSQLValidator(ParseTree tree, CSQLProtocolFactory factory)
	{
		this._pFactory = factory;
		this.currentScope.setInput(tree);
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
	public void exitVarAssign(@NotNull cSQLParser.VarAssignContext ctx) {
		CSQLExecutable runner = new CSQLExecutable(this.currentScope);
		Object r = runner.visit(ctx);
		if(r instanceof SLDCException)
			this.exceptions.add((SLDCException) r);
	}
	
	@Override 
	public void enterStats(@NotNull cSQLParser.StatsContext ctx) {
		this.currentScope.setInput(ctx);
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
			}
			else
			{
				scope = this.currentScope.addAnonymous(ctx);
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
//			this.exceptions.add(new InvalidFormat(matchstr));
//			return false;
		}
		return true;
	}
	
	@Override 
	public void enterHttp(@NotNull cSQLParser.HttpContext ctx) {
		String urlchars = "_\\-#$%0-9A-Za-z";
		String domains = "["+urlchars+"]+";
		String port = "(:[0-9]+)";
		String regex = "^http://"+domains+"([.]"+domains+")*"+port+"?";
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
		Object key = ctx.Identifier() != null?ctx.Identifier().getText():ctx;
		
		try {
			CSQLProtocol protocol = _pFactory.Create(ctx.protocol().getText());
			Object r = protocol.Retrieve();
			this.currentScope.addVariable(key, r);
		} catch (SLDCException e) {
			this.exceptions.add(e);
		}
	}
	
	@Override 
	public void enterSelectExpr(@NotNull cSQLParser.SelectExprContext ctx) {
		Scope scope = this.currentScope.addAnonymous(ctx);
		this.currentScope = scope;
	}
	
	@Override 
	public void exitSelectExpr(@NotNull cSQLParser.SelectExprContext ctx) {	
		CSQLExecutable runner = new CSQLExecutable(this.currentScope);
		this.currentScope = this.currentScope.getUpperScope();
		this.currentScope.addVariable(ctx, runner.visit(ctx));
	}
}
