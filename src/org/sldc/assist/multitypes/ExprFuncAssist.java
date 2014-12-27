package org.sldc.assist.multitypes;

import org.sldc.exception.NotSupportedOperation;

public class ExprFuncAssist {
	public static final Object getByExprList(Object o, Object[] params) {
		if(SubItemsAssist.isSupportType(o)) {
			if(params.length==0||params.length>2||!SubItemsAssist.isParamLegal(params))
				return new NotSupportedOperation(new Throwable());
			return SubItemsAssist.subItems(o, params);
		}
		return null;
	}
}
