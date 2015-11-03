package com.returnsoft.collection.exception;

import java.io.Serializable;
import java.util.List;

public class MultipleErrorsException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 371906433892450573L;
	
	private List<Exception> errors;
	
	public MultipleErrorsException(List<Exception> errors) {
		super("Ocurrieron múltiples errores");
		this.errors = errors;
	}

	public List<Exception> getErrors() {
		return errors;
	}


}
