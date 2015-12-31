package com.returnsoft.collection.exception;

import java.util.List;

public class FileMultipleErrorsException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5681838028038442642L;
	
	private List<Exception> errors;

	public FileMultipleErrorsException(List<Exception> errors) {
		super();
		this.errors=errors;
	}

	public List<Exception> getErrors() {
		return errors;
	}
	
	
	
	

}
