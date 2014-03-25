package org.sldc.exception;

import java.util.List;

public interface IRuntimeError {
	public List<SLDCException> getErrors();
}
