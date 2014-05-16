package org.sldc;

import java.lang.reflect.Method;

public interface IFunctionInterface {
	public Method getMethodByNameParam(String name, Object[] parameters) throws NoSuchMethodException;
}
