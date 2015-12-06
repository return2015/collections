package com.returnsoft.collection.exception;

import java.io.Serializable;

public class SaleStateInvalidException extends Exception implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6782233176895659864L;

	public SaleStateInvalidException() {
		super("Estado de venta inválido");
	}
	
	

}
