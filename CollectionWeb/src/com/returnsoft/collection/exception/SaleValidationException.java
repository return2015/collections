package com.returnsoft.collection.exception;

import java.util.List;

public class SaleValidationException extends Exception{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7170782711417112960L;

	private List<Exception> errors;
	
	public SaleValidationException(List<Exception> errors) {
		super();
		this.errors=errors;
	}

	public List<Exception> getErrors() {
		return errors;
	}
	
	

}
