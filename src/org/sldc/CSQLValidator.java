package org.sldc;

import org.sldc.csql.cSQLBaseListener;
import org.sldc.csql.syntax.Scope;

public class CSQLValidator extends cSQLBaseListener {
	private Scope currentScope = new Scope();
}
