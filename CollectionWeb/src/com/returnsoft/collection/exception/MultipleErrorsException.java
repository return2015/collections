package com.returnsoft.collection.exception;

import java.util.List;

public class MultipleErrorsException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5681838028038442642L;
	
	private List<Exception> errors;

	public MultipleErrorsException(List<Exception> errors) {
		super("Se encontraron "+errors.size()+" errores.");
		this.errors=errors;
	}

	public List<Exception> getErrors() {
		return errors;
	}
	
	
	
	

}
