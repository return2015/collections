package com.returnsoft.collection.exception;

import java.util.List;

public class FileMultipleErrorsException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5681838028038442642L;
	
	private List<String> errors;

	public FileMultipleErrorsException(List<String> errors) {
		super();
		this.errors=errors;
	}

	public List<String> getErrors() {
		return errors;
	}
	
	
	
	

}
